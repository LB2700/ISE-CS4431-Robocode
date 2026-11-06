import java.io.File;

import robocode.BattleResults;
import robocode.control.BattleSpecification;
import robocode.control.BattlefieldSpecification;
import robocode.control.RobocodeEngine;
import robocode.control.RobotSpecification;
import robocode.control.events.BattleAdaptor;
import robocode.control.events.BattleCompletedEvent;

public class EventDriver {
    private static final String[] SAMPLE_BOTS = {
            "sample.Corners",
            "sample.Fire",
            "sample.SpinBot",
            "sample.SittingDuck"
    };

    public static void main(String[] args) {
        File robocodeHome = new File("robocode");
        if (!robocodeHome.isDirectory()) {
            throw new IllegalStateException(
                    "Run this program from the project root; Robocode was not found at "
                            + robocodeHome.getAbsolutePath());
        }

        RobocodeEngine engine = new RobocodeEngine(robocodeHome);
        try {
            engine.setVisible(true);
            engine.addBattleListener(new BattleAdaptor() {
                @Override
                public void onBattleCompleted(BattleCompletedEvent event) {
                    System.out.println("Battle results:");
                    for (BattleResults result : event.getSortedResults()) {
                        System.out.printf(
                                "%d. %s - score: %d, bullet damage: %d, ram damage: %d%n",
                                result.getRank(),
                                result.getTeamLeaderName(),
                                result.getScore(),
                                result.getBulletDamage(),
                                result.getRamDamage());
                    }
                }

                @Override
                public void onBattleError(
                        robocode.control.events.BattleErrorEvent event) {
                    System.err.println("Battle error: " + event.getError());
                }
            });

            RobotSpecification[] robots =
                    engine.getLocalRepository(String.join(",", SAMPLE_BOTS));
            if (robots.length != SAMPLE_BOTS.length) {
                throw new IllegalStateException(
                        "Expected " + SAMPLE_BOTS.length + " sample robots, but found "
                                + robots.length);
            }

            BattleSpecification battle = new BattleSpecification(
                    1,
                    new BattlefieldSpecification(800, 600),
                    robots);
            engine.runBattle(battle);
            engine.waitTillBattleOver();
        } finally {
            engine.close();
        }
    }
}
