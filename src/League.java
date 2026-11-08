import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;
import java.util.List;
import java.lang.IllegalArgumentException;
import java.lang.IndexOutOfBoundsException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import robocode.BattleResults;

import javax.json.*;

public class League {

    private static final ArrayList<String> SAMPLE_ENTRANTS = new ArrayList<String>(List.of( 
        "sample.Corners",
        "sample.Fire",
        "sample.SpinBot",
        "sample.SittingDuck",
        "sample.Crazy",
        "sample.MyFirstRobot",
        "sample.PaintingRobot",
        "sample.RamFire",
        "sample.Tracker",
        "sample.Target",
        "sample.TrackFire",
        "sample.VelociRobot",
        "sample.VelociRobot"
    ));
    private final File logDirectory = new File("logs");
    private ArrayList<Entry> entries;
    private ArrayList<ScheduledGame> games;
    private ArrayList<String> entrants;

    public static League Generate(ArrayList<String> EntryNames, final int GAME_SIZE, final int GAME_COUNT_PER_ENTRY) {
        final int ENTRY_COUNT = EntryNames.size();

        League newLeague = new League();
        newLeague.entrants = new ArrayList<>(EntryNames);
        Random random = new Random();

        for (int i = 0; i < ENTRY_COUNT; i++) {
            newLeague.entries.add(new Entry(EntryNames.get(i)));
        }

        // ensure entries are matched uniformly the whole way up. Only matches entries with less than targetScheduledCount amount of games scheduled, 
        // then progressively increment targetScheduledCount until it reaches GAME_COUNT_PER_ENTRY
        int targetScheduledCount = 0;
        int collisionsAllowed = 0;

        while(getLowestGameCount(newLeague.entries) < GAME_COUNT_PER_ENTRY) {
        //for (int j = 0; j < 98; j++) {

            // keep attempting to generate games until the minimum number of games per player reaches the desired count

            ArrayList<Entry> onlyEntriesWithLowestGames = getEntriesWithLessGamesThan(targetScheduledCount, newLeague.entries);
            ArrayList<Entry> nextGame = new ArrayList<>();


            boolean abortGame = false;

            // start at index 0 (relative to some random starting position to prevent final players getting undesirable matchings)
            int i = 0;
            int randOffset = random.nextInt(ENTRY_COUNT);

            // repeat this until a valid game has been created, or restrictions are loosened and we try again
            // loop through every player and attempt to put them in this game
            while (nextGame.size() < GAME_SIZE) {
                //System.out.println(onlyEntriesWithLowestGames);

                // if we haven't iterated through this player yet
                //System.out.printf("Entry Pool: %s%n", onlyEntriesWithLowestGames);

                if (i < onlyEntriesWithLowestGames.size()) {
                    int index = (i++ + randOffset) % onlyEntriesWithLowestGames.size(); // index relative to random starting point
                
                    //System.out.printf("index %d%n", index);

                    // looping through entries. Think of this as the 'current' entry. All checks are based on this entry
                    Entry entry = onlyEntriesWithLowestGames.get(index);

                    if (nextGame.contains(entry)) continue; // skip if already in game, can't play against yourself

                    // count number of 'collisions'
                    // a collision is an instance where an entry is already put into this match has already played against the current player
                    int entryCollisions = 0;
                    for (Entry otherEntry : nextGame) {
                        if (otherEntry.findPastOpponents(newLeague.games).contains(entry)) {
                            // skip this entry from entering this game because the other entry has seen me before
                            entryCollisions++;
                        }
                    }

                    // the desired amount of collisions initially is zero, this is one of the restrictions that get loosened as the algorithm
                    // fails to make games
                    // if too many collisions occur, skip this entry they are not allowed play this game against the currently selected entries
                    if (entryCollisions > collisionsAllowed) {
                        continue;
                    }
 
                    // if the entry already has all their games scheduled. they CANNOT play anymore. This is imperitive to the league
                    if (entry.gamesScheduled >= GAME_COUNT_PER_ENTRY) {
                        continue;
                    }
                    //System.out.printf("Selected Entry %s, for game %s%n", entry, nextGame);

                    // if these checks are passed, they entry can play this game
                    entry.gamesScheduled++;
                    nextGame.add(entry);
                    collisionsAllowed = 0;
                } else {
                    // the code reaches this point when all the valid players have been ran iterated over and a full game was not 
                    // constructed. The following logic is used to progressively loosen the restrictions

                    // System.out.println("Exhausted Entry Pool ");
                    System.out.printf("Entry Pool: %s%n", onlyEntriesWithLowestGames);
                    // System.out.printf("Lowest Game Count: %d%n", getLowestGameCount(newLeague.entries));

                    if (onlyEntriesWithLowestGames.size() < GAME_SIZE && targetScheduledCount != GAME_COUNT_PER_ENTRY) {
                        onlyEntriesWithLowestGames = getEntriesWithLessGamesThan(++targetScheduledCount, newLeague.entries);
                        collisionsAllowed = 0;
                        System.out.printf("entry pool is smaller than %d, increment targetScheduledCount, %d%n", GAME_SIZE, targetScheduledCount);
                        System.out.printf("reset collisionsAllowed %n");
                    } else if (collisionsAllowed == GAME_SIZE) { //increase collision allowed first, if reaches maxium increment targetScheduledCount
                        onlyEntriesWithLowestGames = getEntriesWithLessGamesThan(++targetScheduledCount, newLeague.entries);
                        collisionsAllowed = 0;
                        System.out.printf("collisionsAllowed is maxed out, increment targetScheduledCount, %d%n", targetScheduledCount);
                        System.out.printf("reset collisionsAllowed %n");
                        if (targetScheduledCount == GAME_COUNT_PER_ENTRY) {
                            // all entrants have no more games to play except for a set of entrants less than GAME_COUNT
                            // a game (or games) needs to be played with less than the specified game count

                            // example; 13 entrants, 3 games of 4. entrant #12 is left by themselves
                            // [0, 1, 2, 3]
                            // [7, 8, 9, 10]
                            // [4, 5, 6, 11]
                            // [12]

                            // fix, take 1 player from (GAME_COUNT - P - 1) games and match them in the final game. 
                            // Where P is the amount of players in the final game. P < GAME_COUNT
                            // result:
                            // [0, 1, 2, 3]
                            // [7, 8, 9]
                            // [4, 5, 6]
                            // [12, 10, 11]

                            if (nextGame.size() < GAME_SIZE - 1) { 
                                System.out.println(newLeague.games.size());
                                System.out.println(GAME_SIZE);
                                System.out.println(nextGame.size());

                                System.out.println(newLeague.games.size() + nextGame.size() + 1 - GAME_SIZE);
                                for (int j = newLeague.games.size() + nextGame.size() + 1 - GAME_SIZE; j < newLeague.games.size(); j++) {
                                    System.out.println(j);
                                    Entry entry = newLeague.games.get(j).entries.remove(0);
                                    nextGame.add(entry);
                                }
                            }
                            break;
                        }
                            
                    } else {
                        collisionsAllowed++;
                        System.out.printf("Increment collisionsAllowed: %d%n", collisionsAllowed);
                    }

                    i = 0;
                }

                if (nextGame.size() == GAME_SIZE) {
                    break;
                }
            }

            if (!abortGame) {
                //System.out.printf("Game Scheduled: %s%n", nextGame);
                newLeague.scheduleGame(nextGame);
            } 
        }

        return newLeague;
    }

    private static int getLowestGameCount(ArrayList<Entry> entries) {
        ArrayList<Entry> sortedEntries = new ArrayList<>(entries);
        sortedEntries.sort((a, b) -> Integer.compare(a.gamesScheduled, b.gamesScheduled));

        return sortedEntries.get(0).gamesScheduled;
    }

    private static ArrayList<Entry> getEntriesWithLessGamesThan(int targetScheduledCount, ArrayList<Entry> entries) {
        ArrayList<Entry> onlyEntriesWithLowestGames = new ArrayList<>();
        for (Entry entry : entries) {
            if (entry.gamesScheduled <= targetScheduledCount) {
                onlyEntriesWithLowestGames.add(entry);
            }
        }
        return onlyEntriesWithLowestGames;
    }

    public static void main() {
       League myLeague = League.Generate(SAMPLE_ENTRANTS, 4, 2);
       myLeague.showSchedule();
    }
        
    public League() {
        entries = new ArrayList<>();
        games =  new ArrayList<>();
        entrants = new ArrayList<>();
    }

    public void start(final int BROADCAST_COUNT, File robocodeHome) {
        List<List<ScheduledGame>> partitionedGames = partitionGames(BROADCAST_COUNT);
        
        ExecutorService logReaders = Executors.newFixedThreadPool(BROADCAST_COUNT);
        List<Future<?>> logTasks = new ArrayList<>();
        List<Process> processes = new ArrayList<>();
        String java = Path.of(
                System.getProperty("java.home"),
                "bin",
                "java.exe"
        ).toString();

        if (!logDirectory.exists() && !logDirectory.mkdirs()) {
            throw new IllegalStateException("Could not create log directory: " + logDirectory);
        }

        System.out.printf("Running League with %d games: %n", getGameCount());
        for (int i = 0; i < getGameCount(); i++) {
            System.out.printf("%d. %s%n", i, games.get(i));
        }

        try {
            for (int id = 1; id <= BROADCAST_COUNT; id++) {
                List<List<String>> partition = new ArrayList<>();
                for (ScheduledGame game : partitionedGames.get(id - 1)) {
                    partition.add(ScheduledGame.insertRobotIds(game, entrants));
                }

                System.out.println(partition);

                ProcessBuilder builder = new ProcessBuilder(
                        java,
                        "-cp",
                        System.getProperty("java.class.path"),
                        "./src/GameDriver.java",
                        Integer.toString(id),
                        robocodeHome.getAbsolutePath(),
                        partition.toString()
                );
                builder.redirectErrorStream(true);

                Process process = builder.start();
                processes.add(process);
                int gameId = id;
                logTasks.add(logReaders.submit(() -> streamOutput(process, gameId)));
            }

            int failedGames = 0;
            for (int i = 0; i < processes.size(); i++) {
                int exitCode = processes.get(i).waitFor();
                System.out.printf("Game Driver %d completed%n", i + 1);
                if (exitCode != 0) {
                    System.err.printf("Game %d exited with code %d%n", i + 1, exitCode);
                    failedGames++;
                }
            }

            for (Future<?> logTask : logTasks) {
                logTask.get();
            }
            if (failedGames > 0) {
                throw new IllegalStateException(failedGames + " game process(es) failed");
            }

            System.out.printf("League Phase Over %n");
        } catch (IOException e) {
            processes.forEach(Process::destroyForcibly);
            throw new IllegalStateException("Could not start a game process", e);
        } catch (InterruptedException e) {
            processes.forEach(Process::destroyForcibly);
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Interrupted while waiting for game processes", e);
        } catch (ExecutionException e) {
            processes.forEach(Process::destroyForcibly);
            throw new IllegalStateException("Could not read game process output", e.getCause());
        } finally {
            logReaders.shutdownNow();
        }
    }

    private void recieveResponseFromGameDriver(JsonObject response) {
        JsonArray jsonResults = response.getJsonArray("sortedResults");
        for (JsonObject playerResults : jsonResults.getValuesAs(JsonObject.class)) {
            //System.out.println(playerResults);
            updateResults(new BattleResults(
                playerResults.getString("teamLeaderName"),
                playerResults.getInt("rank"),
                playerResults.getJsonNumber("score").doubleValue(),
                playerResults.getJsonNumber("survival").doubleValue(),
                playerResults.getJsonNumber("lastSurvivorBonus").doubleValue(),
                playerResults.getJsonNumber("bulletDamage").doubleValue(),
                playerResults.getJsonNumber("bulletDamageBonus").doubleValue(),
                playerResults.getJsonNumber("ramDamage").doubleValue(),
                playerResults.getJsonNumber("ramDamageBonus").doubleValue(),
                playerResults.getInt("firsts"),
                playerResults.getInt("seconds"),
                playerResults.getInt("thirds")
                // this is why javascript > java
            ));
        }
    }

    private void updateResults(BattleResults results) {
        //System.out.println(results);

        for (Entry entry : entries) {
            if (entry.id.equals(results.getTeamLeaderName())) {
                entry.aggregateStats(results);
                entry.showStats();
                break;
            }
        }
    }

    private List<List<ScheduledGame>> partitionGames(int partitionCount) {
        if (partitionCount > games.size())
            throw new IllegalArgumentException(String.format("Cannot make %d partitions with %d total games", partitionCount, games.size()));

        List<List<ScheduledGame>> partitions = new ArrayList<>();
        for (int i = 0; i < partitionCount; i++) {

            int start = (int)(((long)games.size() * i) / partitionCount);
            int end = (int)(((long)games.size() * (i + 1)) / partitionCount);

            List<ScheduledGame> partition = games.subList(start,end);
            System.out.printf("partition #%d:%n", i);
            for (ScheduledGame game : partition)
                System.out.println(game);
            partitions.add(partition);
        }

        return partitions;
    }

    private void streamOutput(Process process, int gameId) {
        File logFile = new File(logDirectory, "game-" + gameId + ".log");
        try (
            BufferedReader reader = process.inputReader(StandardCharsets.UTF_8);
            BufferedWriter writer = Files.newBufferedWriter(
                    logFile.toPath(),
                    StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE,
                    StandardOpenOption.TRUNCATE_EXISTING,
                    StandardOpenOption.WRITE)
        ) {
            String line;
            while ((line = reader.readLine()) != null) {

                if (line.trim().startsWith("{")) {         
                    try (JsonReader jsonReader = Json.createReader(new StringReader(line))) {
                        recieveResponseFromGameDriver(jsonReader.readObject());
                    } catch (JsonException e) {
                        System.err.println("Could not parse game " + gameId + " JSON: " + e.getMessage());
                    }
                } else {
                    System.out.printf("[Game %d] %s%n", gameId, line);
                    writer.write(line);
                    writer.newLine();
                    writer.flush();
                }
            }
        } catch (IOException e) {
            process.destroyForcibly();
            throw new UncheckedIOException("Failed to stream output for game " + gameId, e);
        }
    }

    private void scheduleGame(List<Entry> entries) {
        games.add(new ScheduledGame(entries));
        System.out.printf("Scheduling game: %s%n", entries);
    }

    private int getGameCount() {
        return games.size();
    }

    public void showSchedule() {
       for (int i = 0; i < games.size(); i++) {
        System.out.printf("%d. %s%n", i, games.get(i));
       }
    }

    public void printStatsTable() {
        List<Entry> sortedEntries = new ArrayList<>(entries);
        sortedEntries.sort((first, second) -> Integer.compare(
                second.getStats().getScore(),
                first.getStats().getScore()));

        System.out.printf("%-30s %10s %10s %18s %20s %15s %20s %15s %8s %8s %8s%n",
                "Robot Name", "Score", "Survival", "Last survivor bonus",
                "Bullet damage", "Bullet damage bonus", "Ram damage",
                "Ram damage bonus", "Firsts", "Seconds", "Thirds");
        System.out.println("=".repeat(172));

        for (Entry entry : sortedEntries) {
            BattleResults stats = entry.getStats();
            System.out.printf("%-30s %10d %10d %18d %20d %15d %20d %15d %8d %8d %8d%n",
                    stats.getTeamLeaderName(), stats.getScore(), stats.getSurvival(),
                    stats.getLastSurvivorBonus(), stats.getBulletDamage(),
                    stats.getBulletDamageBonus(), stats.getRamDamage(),
                    stats.getRamDamageBonus(), stats.getFirsts(),
                    stats.getSeconds(), stats.getThirds());
        }
    }

    publiv JsonArray
  
}

class ScheduledGame {

    public static int ScheduledGameCount = 0;

    public ArrayList<Entry> entries;

    public ScheduledGame(List<Entry> entries) {


        ScheduledGameCount++;
        this.entries = new ArrayList<>();
        this.entries.addAll(entries);

        //if (entries.size() != League.GAME_SIZE) System.out.printf("Bad game generated: %s%n",this);
    }

    public String toString() {
        return entries.toString();
    }

    public static void PrintSchedule(ArrayList<ScheduledGame> games) {
        for (int i = 0; i < games.size(); i++) {
            System.out.printf("%d: %s%n", i, games.get(i));
        }
    }

    public static void ShowCollisions(ArrayList<ScheduledGame> games, ArrayList<Entry> entries) {
        for (Entry entry : entries) {
            // how many times each opponent shows up in my matches
            HashMap<Entry, Integer> opponentsCount = new HashMap<>();

            for (Entry opponent : entry.findPastOpponents(games)) {
                opponentsCount.put(opponent, 0);
                for (ScheduledGame game : entry.findMyGames(games)) {
                    if (game.entries.contains(opponent)) {
                        opponentsCount.put(opponent, opponentsCount.get(opponent) + 1);
                    }
                }
            }

            if (opponentsCount.values().contains(2)) {
                System.out.printf("Opponent Count for entry %s:%n", entry);
                for (Entry opponent : opponentsCount.keySet()) {
                    System.out.printf("\t%s: %d%n", opponent, opponentsCount.get(opponent));
                }
            }
        }
    }

    public static List<String> insertRobotIds(ScheduledGame game, List<String> entrants) {
        List<String> robots = new ArrayList<>();
        for (Entry entry : game.entries) {
            try {
                robots.add(entrants.get(entry.getIndex()));
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Entry %s in game does not have integer id");
            } catch (IndexOutOfBoundsException e) {
                throw new IllegalArgumentException("Entry %s in game has integer id not covered in list of entries");
            } 
        }
        return robots;
    }
}

class Entry {

    static int EntryCount = 0;

    public int gamesScheduled = 0;
    public String id;
    private int index = 0;

    private BattleResults stats;

    public Entry(String id) {
        this.index = EntryCount++;
        this.id = id;

        // use BattleResults to record an entries stats accross the league, despite the class name indicating 
        // this be the results for one singular battle.
        stats = new BattleResults(id, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0);
    }


    public String toString() {
        return Integer.toString(index);
    }

    public void showInfo() {
        System.out.printf("%s. gamesScheduled: %d%n", id, gamesScheduled);
    }

    public ArrayList<ScheduledGame> findMyGames(ArrayList<ScheduledGame> games) {
        ArrayList<ScheduledGame> myGames = new ArrayList<>();
        for (ScheduledGame game : games) {
            if (game.entries.contains(this)) {
                myGames.add(game);
            }
        }
        return myGames;
    }

    public HashSet<Entry> findPastOpponents(ArrayList<ScheduledGame> games) {
        HashSet<Entry> pastOpponents = new HashSet<>();
        for (ScheduledGame game : findMyGames(games)) {
            for (Entry opponent : game.entries) {
                if (opponent != this) {
                    pastOpponents.add(opponent);
                }
            }
        }
        return pastOpponents;
    }

    public void showGames(ArrayList<ScheduledGame> games) {
        System.out.printf("Games with %S%n", id);
        for (ScheduledGame game : findMyGames(games)) {
            System.out.println(game.toString());
        }
    }

    public int countCollisions(ArrayList<ScheduledGame> games) {
        HashMap<Entry, Integer> opponentsCount = new HashMap<>();
        
        for (Entry opponent : findPastOpponents(games)) {
            opponentsCount.put(opponent, 0);
            for (ScheduledGame game : findMyGames(games)) {
                if (game.entries.contains(opponent)) {
                    opponentsCount.put(opponent, opponentsCount.get(opponent) + 1);
                }
            }
        }
        System.out.printf("opponentsCount for entry %s: %n",id);
        for (Entry opponent : opponentsCount.keySet()) {
            System.out.printf("\t%s: %d%n", opponent, opponentsCount.get(opponent));
        }
        // for (Integer opCount : opponentsCount.values()) {
        //     opponentsCount += opCount - 1;
        // }
        return 0;
    }

    public void aggregateStats(BattleResults battleResults) {
        stats = new BattleResults(
                id, 0,
                (double)(battleResults.getScore() + stats.getScore()),
                (double)(battleResults.getSurvival() + stats.getSurvival()),
                (double)(battleResults.getLastSurvivorBonus() + stats.getLastSurvivorBonus()),
                (double)(battleResults.getBulletDamage() + stats.getBulletDamage()),
                (double)(battleResults.getBulletDamageBonus() + stats.getBulletDamageBonus()),
                (double)(battleResults.getRamDamage() + stats.getRamDamage()),
                (double)(battleResults.getRamDamageBonus() + stats.getRamDamageBonus()),
                battleResults.getFirsts() + stats.getFirsts(),
                battleResults.getSeconds() + stats.getSeconds(),
                battleResults.getThirds() + stats.getThirds()
        );
    }

    public void showStats() {
        System.out.printf("%d. name: %s | score: %d | firsts: %d | seconds: %d | thirds: %d | bullet damage: %d | ram damage: %d%n",
                            index, id, stats.getScore(), stats.getFirsts(), stats.getSeconds(), stats.getThirds(), stats.getBulletDamage(), stats.getRamDamage());
    }

    public int getIndex() {
        return this.index;
    }

    public BattleResults getStats() {
        return stats;
    }
}