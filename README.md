# ISE-CS4431-Robocode

## Competition Details

### Overview

This is an individual Java programming competition for the 55 students in ISE-CS4431. Each student may enter one robot. The event has two phases:

1. **League:** 98 matches will be played accross the league where each entrant will play 7 games of 4-way Free For All (FFA). Points will be awarded for surviving, dealing damage and getting kills. League points determine whether or not you proceed to the playoffs.
2. **Playoffs:** a seeded, 16-player, single-elimination bracket. Each duel is best of three. Placement matches determine places 3–10, so the prizes can be awarded to the top 10.

More information about the league FFA games and playoff 1v1s can be found below

The FFA arena should be larger than the duel arena. Recommended starting settings are **1000 × 1000** for FFA and **800 × 800** for duels. The organisers must test and publish the final arena dimensions and all engine settings before submissions close.

### League format and scoring

- 98 games of 4 randomly picked robots will fight in a free-for-all type game
- No entrant will play the another entrant more than once (if possible)

#### Points Awarded
- Award points in each four-player match as follows:

| Match result | Points |
|---|---:|
| 1st place | 6 |
| 2nd place | 4 |
| 3rd place | 2 |
| 4th place | 0 |
| Damage dealt | 0.1 point per damage point dealt |
| Bot killed | 2 bonus points per bot |

- Damage points are calculated from the Robocode result's damage-dealt value, to one decimal place. Damage dealt to a robot after it has been eliminated does not count.
- Credit a kill to the robot responsible for the opponent's elimination, as recorded by the official battle results. A robot that survives until the end of the match is not counted as killed.
- A player's match total is their placement points plus damage points plus kill bonuses. Their league total is the sum of their match totals over all seven games. Publish each component and the cumulative standings after every set of matches.
- Rank tied league totals by, in order: most 1st-place finishes; most total damage dealt; most bots killed; then a playoff FFA match among the tied entrants.
- The top 16 advance. If entrants withdraw or are disqualified, the next ranked eligible player takes the vacant place.

### Playoff format and ranking

This is AI generated feel free to change it

- Seed players 1–16 by league standing. In the first round, pair **1 vs 16, 8 vs 9, 5 vs 12, 4 vs 13, 3 vs 14, 6 vs 11, 7 vs 10, and 2 vs 15**.
- Every playoff tie is a best-of-three: the first robot to win two rounds advances. Use the same duel arena and settings throughout.
- A Robocode-determined draw is replayed; it does not count as a win. If the engine cannot distinguish a winner after a replay, the Rule Keeper records the result and resolves it using a pre-announced, consistently applied tiebreak procedure.
- The two semifinal losers play for 3rd/4th.
- The four quarterfinal losers play a placement bracket for 5th–8th: two semifinals, then a 5th/6th match and a 7th/8th match.
- The eight first-round losers play a placement bracket for 9th–16th. Seed those eight by their original league seeds and pair 1 vs 8, 4 vs 5, 2 vs 7, and 3 vs 6. The four winners play two semifinals; those winners play for 9th/10th and the semifinal losers play for 11th/12th. The four opening-match losers play two matches for 13th/14th and 15th/16th. This ensures places 9 and 10 are decided on the field.
- All placement ties use the same best-of-three rules. Publish the complete bracket, including placement matches, before the playoffs.

### Event settings and conduct

- Use the same Robocode version, Java runtime, game settings, and approved arena dimensions for every entrant in a given phase. Freeze settings before the first match; do not make gameplay changes once competition begins.
- Organisers launch all matches from the official event setup. Entrants may not interfere with a running match, the engine, another entrant's files, or another robot.
- A robot that fails to compile or start will be disqualified.
- No code changes between league heats or playoff rounds. Keep the original submission and its checksum for verification.
- The Rule Keeper decides rule questions and appeals, documents the reason, and applies decisions consistently.

### Eligibility and code rules
- One entry per student; individual work only. No teams.
- Submissions must be written in Java and must extend `Robot`.
- No external libraries or dependencies. Use only the APIs available in the announced Robocode and Java versions.
- No AI-generated code. Entrants must be able to explain and demonstrate their submitted code. Organisers may inspect source and ask entrants to explain design choices.
- Entrants may not copy another student's solution or submit code they did not write.
- The submission must compile and run in the official competition environment. The robot's source must be included with the entry for rule and authorship checks.
- All instances of plagarism will be disqualified. Winning bots' source code will be reviewed. No GenAI permitted to write bot source code, even if declared.

### Submission process

This is ai generated feel free to change it

The Submission Lead (to be appointed) should publish the upload location, required file/package format, naming convention, deadline and receipt-confirmation process. Require each entrant to provide their name, student identifier, robot name, source file(s), and a short authorship declaration. Do not accept changes after the deadline except to correct a verified organiser-side issue.

After the deadline, the Submission Lead should:

1. Acknowledge and log every entry; keep an unchanged copy of each submission.
2. Check eligibility, Java source, inheritance, dependencies, and required files.
3. Compile and smoke-test each entry in the official environment, recording failures and contacting entrants before the published cutoff for any permitted correction.
4. Provide only eligible, tested entries to the League Manager, using robot names or assigned IDs for scorekeeping.
5. Restrict access to source code to authorised organisers and judges; do not distribute it to other entrants.
