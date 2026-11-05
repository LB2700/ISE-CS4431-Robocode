# ISE-CS4431-Robocode

## Competition Details

### Overview

Each student may enter one robot. The event has two phases:

1. **League:** Each entrant will play 7[*](#num-games-footnote) games of 4-way Free For All (FFA). Points will be awarded for surviving, dealing damage and getting kills. League points determine whether or not you proceed to the playoffs.
2. **Playoffs:** a seeded, 16-player, single-elimination bracket. Each duel is best of three. Placement matches will determine top 10.

_More information below_

Arena Dimensions: **1000 × 1000** for FFA and **800 × 800** for duels.[**](#subject-to-change)

### Eligibility and Code Rules

- One entry per student. No teams (individual work only).
- Submissions must be written in Java and must extend `Robot`.
- No external libraries or dependencies. Use only the APIs available in the announced Robocode and Java versions.
- **No AI-generated code**, even if declared. Entrants must be able to explain and demonstrate their submitted code. Organisers may inspect source and ask entrants to explain design choices.
- Entrants may not copy another student's solution or submit code they did not write.
- The submission must compile and run in the official competition environment. The robot's source must be included with the entry for rule and authorship checks.
- **All instances of plagarism will be disqualified.** Winning bots' source code will be reviewed.

### Event settings and conduct

- The same Robocode version, Java runtime, game settings, and approved arena dimensions will be used for every entrant in a given phase. Settings will be frozen before the first match: no gameplay changes once competition begins.
- Organisers will launch all matches from the official event setup. Entrants may not interfere with a running match, the engine, another entrant's files, or another robot.
- A robot that fails to compile or start will be disqualified.
- No code changes between league heats or playoff rounds. Keep the original submission and its checksum for verification.
- The Rule Keeper (**Anastasia**) decides rule questions and appeals, documents the reason, and applies decisions consistently.

### League Format and Scoring

- 98[*](#num-games-footnote) games of 4 randomly picked robots will fight in a free-for-all type game
- No entrant will play another entrant more than once (if possible)

#### Points Awarded
- Points will be awarded in each four-player match according to the [default rules](https://robowiki.net/wiki/Robocode/Scoring)
- Tied league totals will be ranked by, in order: most 1st-place finishes; most total damage dealt; most bots killed. If no decision can be reached, a playoff FFA match will be held among the tied entrants.
- The top 16 advance. If entrants withdraw or are disqualified, the next ranked player takes the vacant place.

### Playoff format and ranking

- Playoff matches will be 1v1 with a sentrybot
- The sentrybot border will be **175*175**[**](#subject-to-change)
- Players 1–16 by league standing will be entered in the playoffs.  
- Every playoff tie is a best-of-three: the first robot to win two rounds advances.
- A Robocode-determined draw is replayed; it does not count as a win. If the engine cannot distinguish a winner after a replay, the Rule Keeper (**Anastasia**) records the result and resolves it using a pre-announced, consistently applied tiebreak procedure.



### Submission Process

[**](#subject-to-change)

The Submission Lead (**Omar**) will publish the upload location, required file/package format, naming convention, and deadline.  
Each entrant should provide their name, student ID, robot name, source file(s), and a short authorship declaration.  
Changes will not be accepted after the deadline except to correct a verified organiser-side issue.


-----------
<a id="num-games-footnote"></a>
*subject to number of submissions
<a id="subject-to-change"></a>
**subject to change
