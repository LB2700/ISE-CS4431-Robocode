import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;
import java.util.List;

public class League {

    ArrayList<Entry> entries = new ArrayList<>();
    ArrayList<ScheduledGame> games =  new ArrayList<>();

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

                    System.out.println("Exhausted Entry Pool ");
                    System.out.printf("Entry Pool: %s%n", onlyEntriesWithLowestGames);
                    System.out.printf("Lowest Game Count: %d%n", getLowestGameCount(newLeague.entries));

                    if (collisionsAllowed == GAME_SIZE) { //increase collision allowed first, if reaches maxium increment targetScheduledCount
                        System.out.printf("collisionsAllowed is maxed out, increment targetScheduledCount");
                        System.out.printf("reset collisionsAllowed");
                        onlyEntriesWithLowestGames = getEntriesWithLessGamesThan(++targetScheduledCount, newLeague.entries);
                        collisionsAllowed = 0;
                    } else {
                        System.out.printf("Increment collisionsAllowed: %d%n", ++collisionsAllowed);
                    }

                    // if (targetScheduledCount >= GAME_COUNT_PER_ENTRY) { 
                    //     // the maximum number of games per entry has been reached
                    //     System.out.printf("Lowest Games Scheduled: %d%n", targetScheduledCount);
                    //     System.out.printf("Collisions allowed: %d%n", collisionsAllowed);
                    //     System.out.printf("Lowest Game Count: %d%n", getLowestGameCount(newLeague.entries));
                    //     if (collisionsAllowed == GAME_SIZE) {
                    //         System.out.printf("ABORTING%n");
                    //         break;
                    //     }
                    //     //System.out.printf("Increasing Collisions Allowed %n");
                    // } else {
                    //     //System.out.printf("In")
                    //     onlyEntriesWithLowestGames = getEntriesWithLessGamesThan(++targetScheduledCount, newLeague.entries);
                        
                    //     System.out.printf("Reached end of valid entry set. Start allowing entries to have %d games scheduled%n", targetScheduledCount);
                    //     System.out.printf("Entry Pool: %s%n", onlyEntriesWithLowestGames);
                   
                    // }

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

        for (Entry entry : newLeague.entries) {
            entry.showInfo();
        }

        return newLeague;
       // System.out.println(onlyEntriesWithLowestGames);
       //  System.out.println(nextGame);
        //System.out.printf("Game count: %d%n%n", games.size());



        //entries.get(0).showGames(games);
        //System.out.println(entries.get(0).findPastOpponents(games));

        //System.out.println(games);
        //ScheduledGame.PrintSchedule(games);
        //ScheduledGame.ShowCollisions(games,entries);
    }


    public League() {

    }

    public static void main() {
       League myLeague = League.Generate(56, 4, 48);

       myLeague.showSchedule();
       for (Entry entry: myLeague.entries) {
        entry.showInfo();
        //entry.countCollisions(myLeague.games);
       }
       
      // myLeague.entries
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
        games.add(new ScheduledGame(entries));
        System.out.printf("Scheduling game: %s%n", entries);
    }

    private void showSchedule() {
       for (int i = 0; i < games.size(); i++) {
        System.out.printf("%d. %s%n", i, games.get(i));
       }
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

    // public int countCollisions(ArrayList<ScheduledGame> games) {
    //     HashMap<Entry, Integer> opponentsCount = new HashMap<>();

    //     for (Entry opponent : findPastOpponents(games)) {
    //         opponentsCount.put(opponent, 0);
    //         for (ScheduledGame game : findMyGames(games)) {
    //             if (game.entries.contains(opponent)) {
    //                 opponentsCount.put(opponent, opponentsCount.get(opponent) + 1);
    //             }
    //         }
    //     }
    //     System.out.printf("opponentsCount for entry %s: %s%n",id, opponentsCount.values());
    //     for (Integer opCount : opponentsCount.values()) {
    //         opponentsCount += opCount - 1;
    //     }

    // }
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
}