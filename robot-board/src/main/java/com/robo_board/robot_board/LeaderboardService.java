package com.robo_board.robot_board;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Service;
import robocode.BattleResults;

@Service
public class LeaderboardService {

    public Map<String, Integer> totalScores = new ConcurrentHashMap<>();

    public void updateScores(BattleResults[] results) {
        for (BattleResults b : results) {
            totalScores.merge(b.getTeamLeaderName(), b.getScore(), Integer::sum);
        }
    }

    public List<Map.Entry<String, Integer>> getStandings() {
        return totalScores
            .entrySet()
            .stream()
            .sorted(Map.Entry.<String, Integer>comparingByValue())
            .toList()
            .reversed();
    }
}
