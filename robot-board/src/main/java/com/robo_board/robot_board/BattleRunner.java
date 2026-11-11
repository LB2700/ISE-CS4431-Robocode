package com.robo_board.robot_board;

import robocode.control.*;
import robocode.control.events.*;

public class BattleRunner implements Runnable {

    private final LeaderboardService leaderboard;
    private final String robocodeHome = "../robocode";
    private final String robots;
    private final int rounds;
    private RobocodeEngine engine;

    // Check `application.properties` to change Robocode home directory.
    BattleRunner(LeaderboardService leaderboard, String robots, int rounds) {
        this.leaderboard = leaderboard;
        this.robots = robots;
        this.rounds = rounds;
    }

    @Override
    public void run() {
        RobocodeEngine.setLogMessagesEnabled(true);
        this.engine = new RobocodeEngine(new java.io.File(robocodeHome));

        BattleAdaptor battleObserver = new BattleAdaptor() {
            @Override
            public void onBattleCompleted(BattleCompletedEvent e) {
                leaderboard.updateScores(e.getSortedResults());

                System.out.println("Battle results:");
                for (robocode.BattleResults result : e.getSortedResults()) {
                    System.out.println(
                        "  " + result.getTeamLeaderName() + ": " + result.getScore()
                    );
                }
                IO.println("Battle completed. Leaderboard updated");
            }
        };

        try {
            this.engine.addBattleListener(battleObserver);

            this.engine.setVisible(true);

            BattlefieldSpecification battlefield = new BattlefieldSpecification(800, 800);
            RobotSpecification[] selectedRobots = this.engine.getLocalRepository(this.robots);
            BattleSpecification spec = new BattleSpecification(
                this.rounds,
                battlefield,
                selectedRobots
            );

            this.engine.runBattle(spec, true);
        } finally {
            this.engine.removeBattleListener(battleObserver);
        }
    }
}
