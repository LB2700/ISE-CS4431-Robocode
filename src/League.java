import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;

public class League {

    static final int ENTRY_COUNT = 56;
    static final int GAME_SIZE = 4;
    static final int GAME_COUNT_PER_ENTRY = 7;
    private ArrayList<Entry> entries = new ArrayList<>();
    private ArrayList<ScheduledGame> games =  new ArrayList<>();

    public League() {

        Random random = new Random();

        for (int i = 0; i < ENTRY_COUNT; i++) {
            entries.add(new Entry(Integer.toString(i)));
        }

        int lowestGamesScheduled = 0;
        int collisionsAllowed = 0;

        while(lowestGamesScheduled < GAME_COUNT_PER_ENTRY) {
        //for (int j = 0; j < 98; j++) {
            ArrayList<Entry> onlyEntriesWithLowestGames = getOnlyEntriesWithLowestGames(lowestGamesScheduled);
            
            ArrayList<Entry> nextGame = new ArrayList<>();

            boolean abortGame = false;

            int i = 0;
            int randOffset = random.nextInt(ENTRY_COUNT);

            while (nextGame.size() < GAME_SIZE) {
                //System.out.println(onlyEntriesWithLowestGames);
                if (i < onlyEntriesWithLowestGames.size()) {
                    int index = (i++ + randOffset) % onlyEntriesWithLowestGames.size();
                    System.out.printf("index %d%n", index);
                    Entry entry = onlyEntriesWithLowestGames.get(index);

                    if (nextGame.contains(entry)) continue;

                    int entryCollisions = 0;

                    for (Entry otherEntry : nextGame) {
                        if (otherEntry.findPastOpponents(games).contains(entry)) {
                            // skip this entry from entering this game because the other entry has seen me before
                            entryCollisions++;
                        }
                    }

                    if (entryCollisions > collisionsAllowed) {
                        continue;
                    }

                    if (collisionsAllowed > 0) {
                        //System.out.printf("Allowing %")
                    }
 
                    if (entry.gamesScheduled >= GAME_COUNT_PER_ENTRY) {
                        continue;
                    }
                    //System.out.printf("Selected Entry %s, for game %s%n", entry, nextGame);
                    entry.gamesScheduled++;
                    nextGame.add(entry);
                } else {
                    if (lowestGamesScheduled >= GAME_COUNT_PER_ENTRY) {
                        if (collisionsAllowed == GAME_SIZE) {
                            break;
                        }
                        collisionsAllowed++;
                        //System.out.printf("Increasing Collisions Allowed %n");
                    } else  {
                        onlyEntriesWithLowestGames = getOnlyEntriesWithLowestGames(++lowestGamesScheduled);
                        //System.out.printf("Reached end of valid entry set. Start allowing entries to have %d games scheduled%n", lowestGamesScheduled);
                    }

                    i = 0;
                }

                if (nextGame.size() == GAME_SIZE) {
                    break;
                }
            }

            if (nextGame.size() < GAME_SIZE) {
                //System.out.printf("Underpopulated Game: %s%n", nextGame);
            }

            if (!abortGame) {
                //System.out.printf("Game Scheduled: %s%n", nextGame);
                games.add(new ScheduledGame(nextGame));
            } 
        }

       // System.out.println(onlyEntriesWithLowestGames);
       //  System.out.println(nextGame);
        System.out.printf("Game count: %d%n%n", games.size());

        for (Entry entry : entries) {
            entry.showInfo();
        }

        //entries.get(0).showGames(games);
        //System.out.println(entries.get(0).findPastOpponents(games));

        //System.out.println(games);
        //ScheduledGame.PrintSchedule(games);
        ScheduledGame.ShowCollisions(games,entries);
    }

    public static void main() {
       League myLeague = new League();
    }

    private ArrayList<Entry> getOnlyEntriesWithLowestGames(int lowestGamesScheduled) {
        ArrayList<Entry> onlyEntriesWithLowestGames = new ArrayList<>();
        for (Entry entry : entries) {
            if (entry.gamesScheduled <= lowestGamesScheduled) {
                onlyEntriesWithLowestGames.add(entry);
            }
        }
        return onlyEntriesWithLowestGames;
    }

    private void showWhereEntryRepeatsOpponents() {

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
        System.out.printf("\t %s. gamesScheduled: %d%n", id, gamesScheduled);
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
}

class ScheduledGame {

    public static int ScheduledGameCount = 0;

    public ArrayList<Entry> entries;

    public ScheduledGame(ArrayList<Entry> entries) {


        ScheduledGameCount++;
        this.entries = new ArrayList<>();
        this.entries.addAll(entries);

        if (entries.size() != League.GAME_SIZE) System.out.printf("Bad game generated: %s%n",this);
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
}