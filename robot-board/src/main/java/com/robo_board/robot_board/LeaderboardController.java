package com.robo_board.robot_board;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@CrossOrigin
public class LeaderboardController {

    private final LeaderboardService leaderboardService;
    private final String robocodeHome;
    private final AtomicBoolean battleRunning = new AtomicBoolean(false);

    public LeaderboardController(
        @Value("${robocode.home}") String robocodeHome,
        LeaderboardService leaderboardService
    ) {
        this.robocodeHome = robocodeHome;
        this.leaderboardService = leaderboardService;
    }

    // Spectators / Web page fetch the current rankings here
    @GetMapping("/leaderboard")
    public List<Map.Entry<String, Integer>> getLeaderboard() {
        return leaderboardService.getStandings();
    }

    // You or the admin can start a game directly from an admin panel or curl
    @PostMapping("/battle/start")
    public String startBattle(
        @RequestParam(
            defaultValue = "sample.RamFire, sample.Corners, sample.Crazy, sample.Tracker"
        ) String robots,
        @RequestParam(defaultValue = "3") int rounds
    ) {
        if (!battleRunning.compareAndSet(false, true)) {
            return "A battle is already running!";
        }

        // Thread robocodeGame = new Thread(
        //     new BattleRunner(this.robocodeHome, leaderboardService, robots, rounds)
        // );

        new Thread(() -> {
            try {
                BattleRunner runner = new BattleRunner(
                    robocodeHome,
                    leaderboardService,
                    robots,
                    rounds
                );
                runner.run();
            } finally {
                battleRunning.set(false);
            }
        }).start();

        return "Battle started on screen!";
    }
}
