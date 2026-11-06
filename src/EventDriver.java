import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import java.lang.Process;
import java.lang.ProcessBuilder;
import java.lang.IllegalStateException;

import robocode.BattleResults;
import robocode.control.BattleSpecification;
import robocode.control.BattlefieldSpecification;
import robocode.control.RobocodeEngine;
import robocode.control.RobotSpecification;
import robocode.control.events.BattleAdaptor;
import robocode.control.events.BattleCompletedEvent;

public class EventDriver {

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
        "sample.Walls"
    ));

    private final int BROADCAST_COUNT = 4;
    private List<Process> processes;
    private String java;
    private File robocodeHome;
    private File logDirectory;

    private ArrayList<String> entrants;
    private League leaguePhase;

    public static void main(String[] args) {

        EventDriver myDriver = new EventDriver(SAMPLE_ENTRANTS);
        myDriver.runLeague();
    }

    public EventDriver(ArrayList<String> entrants) {
        robocodeHome = new File("robocode");
        logDirectory = new File("logs");
        logDirectory.mkdirs();

        java = Path.of(
                System.getProperty("java.home"),
                "bin",
                "java.exe"
        ).toString();

        processes = new ArrayList<>();

        this.entrants = entrants; 
        leaguePhase = League.Generate(entrants.size(), 4, 4); 
    }

    private void runLeague() {

        List<List<League.ScheduledGame>> partitionedGames = leaguePhase.partitionGames(BROADCAST_COUNT);


        for (int id = 1; id <= BROADCAST_COUNT; id++) {

           List<List<String>> partition = new ArrayList<>();
           for (League.ScheduledGame game : partitionedGames.get(id-1)) {
                partition.add(League.ScheduledGame.insertRobotIds(game, entrants));
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
            builder.redirectOutput(new File(logDirectory, "game-" + id + ".log"));
            
            try {
                processes.add(builder.start());
            } catch (IOException e) {}

        }
    }
}
