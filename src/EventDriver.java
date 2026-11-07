import java.io.File;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.UncheckedIOException;
import java.io.StringReader;
import java.nio.file.Path;
import java.nio.file.Files;
import java.nio.charset.StandardCharsets;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import java.lang.Process;
import java.lang.ProcessBuilder;
import java.lang.IllegalStateException;

import javax.json.*;

import robocode.BattleResults;
import robocode.control.BattleSpecification;
import robocode.control.BattlefieldSpecification;
import robocode.control.RobocodeEngine;
import robocode.control.RobotSpecification;
import robocode.control.events.BattleAdaptor;
import robocode.control.events.BattleCompletedEvent;

public class EventDriver {

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
    ));

    private final int BROADCAST_COUNT = 1;
    private List<Process> processes;
    private String java;
    private File robocodeHome;
    private File logDirectory;

    private ArrayList<String> entrants;
    private League leaguePhase;

    public static void main(String[] args) {

        EventDriver myDriver = new EventDriver(SAMPLE_ENTRANTS);
        myDriver.runLeague();
    }

    public EventDriver(ArrayList<String> entrants) {
        robocodeHome = new File("robocode");
        logDirectory = new File("logs");
        logDirectory.mkdirs();

        java = Path.of(
                System.getProperty("java.home"),
                "bin",
                "java.exe"
        ).toString();

        processes = new ArrayList<>();

        this.entrants = entrants; 
        leaguePhase = League.Generate(entrants.size(), 4, 1); 
    }

    private void recieveResponseFromGameDriver(JsonObject response) {
        System.out.println(response);
    }

    private void runLeague() {

        List<List<League.ScheduledGame>> partitionedGames = leaguePhase.partitionGames(BROADCAST_COUNT);
        ExecutorService logReaders = Executors.newFixedThreadPool(BROADCAST_COUNT);
        List<Future<?>> logTasks = new ArrayList<>();

        try {
            for (int id = 1; id <= BROADCAST_COUNT; id++) {
                List<List<String>> partition = new ArrayList<>();
                for (League.ScheduledGame game : partitionedGames.get(id - 1)) {
                    partition.add(League.ScheduledGame.insertRobotIds(game, entrants));
                }

                System.out.println(partition);

                ProcessBuilder builder = new ProcessBuilder(
                        java,
                        "-cp",
                        System.getProperty("java.class.path"),
                        "./src/GameDriver.java",
                        Integer.toString(id),
                        robocodeHome.getAbsolutePath(),
                        partition.toString()
                );
                builder.redirectErrorStream(true);

                Process process = builder.start();
                processes.add(process);
                int gameId = id;
                logTasks.add(logReaders.submit(() -> streamOutput(process, gameId)));
            }

            int failedGames = 0;
            for (int i = 0; i < processes.size(); i++) {
                int exitCode = processes.get(i).waitFor();
                if (exitCode != 0) {
                    System.err.printf("Game %d exited with code %d%n", i + 1, exitCode);
                    failedGames++;
                }
            }

            for (Future<?> logTask : logTasks) {
                logTask.get();
            }
            if (failedGames > 0) {
                throw new IllegalStateException(failedGames + " game process(es) failed");
            }
        } catch (IOException e) {
            processes.forEach(Process::destroyForcibly);
            throw new IllegalStateException("Could not start a game process", e);
        } catch (InterruptedException e) {
            processes.forEach(Process::destroyForcibly);
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Interrupted while waiting for game processes", e);
        } catch (ExecutionException e) {
            processes.forEach(Process::destroyForcibly);
            throw new IllegalStateException("Could not read game process output", e.getCause());
        } finally {
            logReaders.shutdownNow();
        }
    }

    private void streamOutput(Process process, int gameId) {
        File logFile = new File(logDirectory, "game-" + gameId + ".log");
        try (
            BufferedReader reader = process.inputReader(StandardCharsets.UTF_8);
            BufferedWriter writer = Files.newBufferedWriter(
                    logFile.toPath(),
                    StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE,
                    StandardOpenOption.TRUNCATE_EXISTING,
                    StandardOpenOption.WRITE)
        ) {
            String line;
            while ((line = reader.readLine()) != null) {

                if (line.trim().startsWith("{")) {         
                    try (JsonReader jsonReader = Json.createReader(new StringReader(line))) {
                        recieveResponseFromGameDriver(jsonReader.readObject());
                    } catch (JsonException e) {
                        System.err.println("Could not parse game " + gameId + " JSON: " + e.getMessage());
                    }
                } else {
                    System.out.printf("[Game %d] %s%n", gameId, line);
                    writer.write(line);
                    writer.newLine();
                    writer.flush();
                }
            }
        } catch (IOException e) {
            process.destroyForcibly();
            throw new UncheckedIOException("Failed to stream output for game " + gameId, e);
        }
    }
}
