# ISE-CS4431-Robocode

## Competition Details

### Overview

Each student may enter one robot. The event has two phases:

1. **League:** 98 matches[*](#num-games-footnote) will be played accross the league. Each entrant will play 7 games of 4-way Free For All (FFA). Points will be awarded for surviving, dealing damage and getting kills. League points determine whether or not you proceed to the playoffs.
2. **Playoffs:** a seeded, 16-player, single-elimination bracket. Each duel is best of three. Placement matches determine places 3–10, so the prizes can be awarded to the top 10.

More information below

The FFA arena will be larger than the duel arena. Dimensions TBC, but recommended starting settings are **1000 × 1000** for FFA and **800 × 800** for duels. The committee will publish the final arena dimensions and all engine settings before submissions close.

### League Format and Scoring

- 98 games of 4 randomly picked robots will fight in a free-for-all type game
- No entrant will play another entrant more than once (if possible)

#### Points Awarded
- Points will be awarded in each four-player match according to the [default rules](https://robowiki.net/wiki/Robocode/Scoring)

- Damage points are calculated from the Robocode result's damage-dealt value, to one decimal place. Damage dealt to a robot after it has been eliminated does not count.
- A kill will be credited to the robot responsible for the opponent's elimination, as recorded by the official battle results. A robot that survives until the end of the match is not counted as killed.
- A player's match total: ``placement points + damage points + kill bonuses``. League total: sum of their match totals over all seven games.
- Tied league totals will be ranked by, in order: most 1st-place finishes; most total damage dealt; most bots killed. If no decision can be reached, a playoff FFA match will be held among the tied entrants.
- The top 16 advance. If entrants withdraw or are disqualified, the next ranked eligible player takes the vacant place.

### Playoff format and ranking

*Subject to change*

- Players 1–16 by league standing will be entered in the playoffs.  
- Every playoff tie is a best-of-three: the first robot to win two rounds advances.
- A Robocode-determined draw is replayed; it does not count as a win. If the engine cannot distinguish a winner after a replay, the Rule Keeper (**Anastasia**) records the result and resolves it using a pre-announced, consistently applied tiebreak procedure.
- All placement ties use the same best-of-three rules.

### Event settings and conduct

- The same Robocode version, Java runtime, game settings, and approved arena dimensions will be used for every entrant in a given phase. Settings will be frozen before the first match: no gameplay changes once competition begins.
- Organisers will launch all matches from the official event setup. Entrants may not interfere with a running match, the engine, another entrant's files, or another robot.
- A robot that fails to compile or start will be disqualified.
- No code changes between league heats or playoff rounds. Keep the original submission and its checksum for verification.
- The Rule Keeper (**Anastasia**) decides rule questions and appeals, documents the reason, and applies decisions consistently.

### Eligibility and code rules

- One entry per student. No teams (individual work only).
- Submissions must be written in Java and must extend `Robot`.
- No external libraries or dependencies. Use only the APIs available in the announced Robocode and Java versions.
- **No AI-generated code**, even if declared. Entrants must be able to explain and demonstrate their submitted code. Organisers may inspect source and ask entrants to explain design choices.
- Entrants may not copy another student's solution or submit code they did not write.
- The submission must compile and run in the official competition environment. The robot's source must be included with the entry for rule and authorship checks.
- **All instances of plagarism will be disqualified.** Winning bots' source code will be reviewed.

### Submission process

#### Deadline
- A Google Form will be sent to the ISE group chat and by email when the rules are published.
- The deadline is **11:59 pm on Friday 11th December** (end of Week 5).
- There is **no grace period** and *usually* no exceptions for being late.
- A reminder email with the form will be sent on the day of the deadline.

#### What to Submit
On the form, you must enter your:
- **Full Name**
- **Student ID**
- **Robot Name** (preferably the same as your bot's name in the files)
- **Robot's Java file**

#### Submission Rules
- Make sure your Java file compiles before submitting (see Eligibility and code rules).
- If there are two submissions with the same name or Student ID, that person(s) will be notified and asked to pick one to keep. If they don't, both are removed.
- In the case of an accidental submission, you may ask any one of the organisers for help.
- Inappropriate entries and entries with the wrong file type will be **removed**.

#### After the Deadline
- Submissions will be checked for entries that don't follow the guidelines.
- If there are any issues with your code, you will be notified and given a chance to resubmit by **11:59 pm on Sunday 13th December**. *(date subject to change)*
- A resubmission must contain **only the fix**. Anything else **won't be accepted**.
- A robot that still fails to compile after resubmission is **disqualified**.

-----------
<a id="num-games-footnote"></a>
*subject to number of submissions
