# ISE-CS4431-Robocode

This program will be used to run engaging Robocode competitions. Flexible competition formats are feasibly configured and ran all from one user's machine. The program will take in entrant submission and competition format configuration as input. It leverages the classic robocode engine to run games and record the results to define clear winners.

## How to run

Run commands from root directory

EventDriver: `java -cp ".\robocode\libs\*" src/EventDriver.java`

## (Proposed) Project Structure

```text
ISE-CS4431-Robocode/
├── README.md
├── rules.md
├── build.gradle
├── settings.gradle
├── gradlew
├── gradlew.bat
├── .gitignore
├── .gitattributes
├── gradle/
│   └── wrapper/
│       ├── gradle-wrapper.jar
│       └── gradle-wrapper.properties
└── src/
    ├── main/
    │   ├── java/
    │   │   └── com/Robocomp/
    │   │       └── RobotBoardApplication.java
    │   │       └── EventDriver.java
    │   │       └── League.java
    │   │       └── All other .java files
    │   └── resources/
    │       └── application.properties
    └── test/
        └── java/
            └── com/Robocomp/
                └── RobotBoardApplicationTests.java
```
