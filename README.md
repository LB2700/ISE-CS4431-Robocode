# ISE-CS4431-Robocode

This program will be used to run engaging Robocode competitions. Flexible competition formats are feasibly configured and ran all from one user's machine. The program will take in entrant submission and competition format configuration as input. It leverages the classic robocode engine to run games and record the results to define clear winners.

## How to run

### macOS / Linux

```
./gradlew bootRun
```

### Windows

Run these commands in PowerShell from the project root. Keep the application running and use a second terminal for the API commands.

| Action | Command |
| --- | --- |
| Start the Spring Boot application | `.\gradlew.bat bootRun` |
| Start a battle | `curl.exe -X POST "http://localhost:8080/api/battle/start"` |
| View the current leaderboard | `curl.exe "http://localhost:8080/api/leaderboard"` |
| Stop the application | Press `Ctrl+C` in the terminal running the application. |



## Project Structure

```text
├── README.md
├── SentryBot.java
├── build.gradle
├── gradle/wrapper
│--- ├── gradle-wrapper.jar
│--- └── gradle-wrapper.properties
├── gradlew
├── gradlew.bat
├── robocode
├── rules.md
├── settings.gradle
└── src
    ├── main
    │--- ├── java/com..
    │--- └── resources
    │---     ├── application.properties
    │---     ├── static
    │---     └── templates
    └── test/java/com..
```

**`robocode`** is the Robocode repo.
