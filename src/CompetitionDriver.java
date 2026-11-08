import java.io.File;
import java.util.ArrayList;
import java.util.List;


public class CompetitionDriver {

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
        "sample.VelociRobot"
        // "sample.VelociRobot"
    ));

    private final int BROADCAST_COUNT = 4;
    private final File robocodeHome;
    private final League leaguePhase;

    public static void main(String[] args) {

        CompetitionDriver myDriver = new CompetitionDriver(SAMPLE_ENTRANTS);

        System.out.printf("Competition Over%n");
    }

    public CompetitionDriver(ArrayList<String> entrants) {
        robocodeHome = new File("robocode");
        leaguePhase = League.Generate(entrants, 4, 24); 
        leaguePhase.start(BROADCAST_COUNT, robocodeHome);
        leaguePhase.printStatsTable();
    }
}
