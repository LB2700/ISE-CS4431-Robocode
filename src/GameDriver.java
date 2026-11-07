
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

import robocode.BattleResults;
import robocode.BattleRules;
import robocode.control.BattleSpecification;
import robocode.control.BattlefieldSpecification;
import robocode.control.RobocodeEngine;
import robocode.control.RobotSpecification;
import robocode.control.events.BattleAdaptor;
import robocode.control.events.BattleCompletedEvent;

public class GameDriver {

    private static final String[] SAMPLE_BOTS = {
        "sample.Corners",
        "sample.Fire",
        "sample.SpinBot",
        "sample.SittingDuck"
    };


    private List<List<String>> gameSchedule;

    //private static List<List<League.

    public static void main(String[] args) {// args: [id, robocodehome, schedule]
        int gameId = Integer.parseInt(args[0]);
        File robocodeHome = new File(args[1]);

        System.out.println(args[2]);

        if (!robocodeHome.isDirectory()) {
            throw new IllegalStateException(
                    "Robocode not found: " + robocodeHome.getAbsolutePath());
        }

        System.out.println("Starting game " + gameId);
        new GameDriver(robocodeHome, parseSchedule(args[2]));
        System.out.println("Game " + gameId + " finished");
    }

    private static List<List<String>> parseSchedule(String arg) {
        String[] splitIntoGames = arg.split("\\]\\, \\[");
        //List<List<String>> schedule = new ArrayList<>();

        List<List<String>> parsedSchedule = new ArrayList<>();

        for (String game: splitIntoGames) {
            System.out.println(game);

            String[] entrantIds = game.split(",");
            List<String> parsedIds = new ArrayList<>();

            for (String entrantId : entrantIds) {

                if (entrantId.substring(0,2).equals("[["))
                    entrantId = entrantId.substring(2);
                if (entrantId.substring(entrantId.length()-2,entrantId.length()).equals("]]"))
                    entrantId = entrantId.substring(0, entrantId.length()-2);

                parsedIds.add(entrantId.trim());
            }
            parsedSchedule.add(parsedIds);
        }
        return  parsedSchedule;
    }

    private enum GameDriverState {
        READY,
        RUNNING,
        OVER
    }

    private RobocodeEngine engine;

    public GameDriver(File robocodeHome, List<List<String>> gameSchedule) {
        engine = new RobocodeEngine(robocodeHome);
        this.gameSchedule = gameSchedule;
        //System.out.println("hello world");
        //System.out.println(gameSchedule);

        engine.setVisible(true);
        engine.addBattleListener(new BattleAdaptor() {
            @Override
            public void onBattleCompleted(BattleCompletedEvent event) {
                System.out.println(battleCompletedEventToJson(event));
            }

            @Override
            public void onBattleError(
                    robocode.control.events.BattleErrorEvent event) {
                System.err.println("Battle error: " + event.getError());
            }
        });

        for (int i = 0; i < gameSchedule.size(); i++)
            runGame(i);

        engine.close();
    }

    public void runGame(int gameIndex) {
        try {

            RobotSpecification[] robots =
                    engine.getLocalRepository(String.join(",", gameSchedule.get(gameIndex)));

            BattleSpecification battle = new BattleSpecification(
                    1,
                    new BattlefieldSpecification(800, 600),
                    robots);

            engine.runBattle(battle);
            engine.waitTillBattleOver();

        } catch (Exception err) {
            System.out.println(err);
        } finally {
            
        }
    }

    private static String battleCompletedEventToJson(BattleCompletedEvent event) {
        BattleRules rules = event.getBattleRules();
        JsonObject battleRules = Json.createObjectBuilder()
                .add("battlefieldWidth", rules.getBattlefieldWidth())
                .add("battlefieldHeight", rules.getBattlefieldHeight())
                .add("numRounds", rules.getNumRounds())
                .add("gunCoolingRate", rules.getGunCoolingRate())
                .add("inactivityTime", rules.getInactivityTime())
                .add("hideEnemyNames", rules.getHideEnemyNames())
                .add("sentryBorderSize", rules.getSentryBorderSize())
                .build();

        return Json.createObjectBuilder()
                .add("responseType", "battleResults")
                .add("battleRules", battleRules)
                .add("sortedResults", battleResultsToJson(event.getSortedResults()))
                .build()
                .toString();
    }

    private static JsonArrayBuilder battleResultsToJson(BattleResults[] results) {
        JsonArrayBuilder jsonResults = Json.createArrayBuilder();
        for (BattleResults result : results) {
            JsonObjectBuilder jsonResult = Json.createObjectBuilder();
            if (result.getTeamLeaderName() == null) {
                jsonResult.addNull("teamLeaderName");
            } else {
                jsonResult.add("teamLeaderName", result.getTeamLeaderName());
            }
            jsonResults.add(jsonResult
                    .add("rank", result.getRank())
                    .add("score", result.getScore())
                    .add("survival", result.getSurvival())
                    .add("lastSurvivorBonus", result.getLastSurvivorBonus())
                    .add("bulletDamage", result.getBulletDamage())
                    .add("bulletDamageBonus", result.getBulletDamageBonus())
                    .add("ramDamage", result.getRamDamage())
                    .add("ramDamageBonus", result.getRamDamageBonus())
                    .add("firsts", result.getFirsts())
                    .add("seconds", result.getSeconds())
                    .add("thirds", result.getThirds()));
        }
        return jsonResults;
    }
}
