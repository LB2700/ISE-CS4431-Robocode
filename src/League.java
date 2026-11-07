import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;
import java.util.List;
import java.lang.IllegalArgumentException;
import java.lang.IndexOutOfBoundsException;

import robocode.BattleResults;

public class League {

    static class ScheduledGame {

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

        public static void PrintSchedule(ArrayList<League.ScheduledGame> games) {
            for (int i = 0; i < games.size(); i++) {
                System.out.printf("%d: %s%n", i, games.get(i));
            }
        }

        public static void ShowCollisions(ArrayList<League.ScheduledGame> games, ArrayList<Entry> entries) {
            for (Entry entry : entries) {
                // how many times each opponent shows up in my matches
                HashMap<Entry, Integer> opponentsCount = new HashMap<>();

                for (Entry opponent : entry.findPastOpponents(games)) {
                    opponentsCount.put(opponent, 0);
                    for (League.ScheduledGame game : entry.findMyGames(games)) {
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
                    robots.add(entrants.get(Integer.parseInt(entry.id)));
                } catch (NumberFormatException e) {
                    throw new IllegalArgumentException("Entry %s in game does not have integer id");
                } catch (IndexOutOfBoundsException e) {
                    throw new IllegalArgumentException("Entry %s in game has integer id not covered in list of entries");
                } 
            }
            return robots;
        }

    }

    private ArrayList<Entry> entries = new ArrayList<>();
    private ArrayList<League.ScheduledGame> games =  new ArrayList<>();

    public static League Generate(final int ENTRY_COUNT, final int GAME_SIZE, final int GAME_COUNT_PER_ENTRY) {
        League newLeague = new League();
        Random random = new Random();

        for (int i = 0; i < ENTRY_COUNT; i++) {
            newLeague.entries.add(new Entry(Integer.toString(i)));
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
        
    public League() {

    }

    public static void main() {
       League myLeague = League.Generate(8, 4, 2);

       myLeague.showSchedule();
    //    for (Entry entry: myLeague.entries) {
    //     entry.showInfo();
    //         entry.countCollisions(myLeague.games);
    //    }

      // myLeague.entries
    }

    public List<List<League.ScheduledGame>> partitionGames(int partitionCount) {
        if (partitionCount > games.size())
            throw new IllegalArgumentException(String.format("Cannot make %d partitions with %d total games", partitionCount, games.size()));

        List<List<League.ScheduledGame>> partitions = new ArrayList<>();
        for (int i = 0; i < partitionCount; i++) {

            int start = (int)(((long)games.size() * i) / partitionCount);
            int end = (int)(((long)games.size() * (i + 1)) / partitionCount);

            List<League.ScheduledGame> partition = games.subList(start,end);
            System.out.printf("partition #%d:%n", i);
            for (ScheduledGame game : partition)
                System.out.println(game);
            partitions.add(partition);
        }

        return partitions;
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

    private static int getLowestGameCount(ArrayList<Entry> entries) {
        ArrayList<Entry> sortedEntries = new ArrayList<>(entries);
        sortedEntries.sort((a, b) -> Integer.compare(a.gamesScheduled, b.gamesScheduled));

        return sortedEntries.get(0).gamesScheduled;
    }

    private void scheduleGame(List<Entry> entries) {
        games.add(new League.ScheduledGame(entries));
        System.out.printf("Scheduling game: %s%n", entries);
    }

    private void showSchedule() {
       for (int i = 0; i < games.size(); i++) {
        System.out.printf("%d. %s%n", i, games.get(i));
       }
    }

    public int getGameCount() {
        return games.size();
    }

    public void updateResults(BattleResults results) {
        System.out.println(results);
    }

    public League.ScheduledGame getGame(int i) {
        return games.get(i);
    } 
}

class Entry {
    public int gamesScheduled = 0;
    public String id;

    public Entry(String id) {
        this.id = id;
    }

    public String toString() {
        return id;
    }

    public void showInfo() {
        System.out.printf("%s. gamesScheduled: %d%n", id, gamesScheduled);
    }

    public ArrayList<League.ScheduledGame> findMyGames(ArrayList<League.ScheduledGame> games) {
        ArrayList<League.ScheduledGame> myGames = new ArrayList<>();
        for (League.ScheduledGame game : games) {
            if (game.entries.contains(this)) {
                myGames.add(game);
            }
        }
        return myGames;
    }

    public HashSet<Entry> findPastOpponents(ArrayList<League.ScheduledGame> games) {
        HashSet<Entry> pastOpponents = new HashSet<>();
        for (League.ScheduledGame game : findMyGames(games)) {
            for (Entry opponent : game.entries) {
                if (opponent != this) {
                    pastOpponents.add(opponent);
                }
            }
        }
        return pastOpponents;
    }

    public void showGames(ArrayList<League.ScheduledGame> games) {
        System.out.printf("Games with %S%n", id);
        for (League.ScheduledGame game : findMyGames(games)) {
            System.out.println(game.toString());
        }
    }

    public int countCollisions(ArrayList<League.ScheduledGame> games) {
        HashMap<Entry, Integer> opponentsCount = new HashMap<>();
        
        for (Entry opponent : findPastOpponents(games)) {
            opponentsCount.put(opponent, 0);
            for (League.ScheduledGame game : findMyGames(games)) {
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
}
