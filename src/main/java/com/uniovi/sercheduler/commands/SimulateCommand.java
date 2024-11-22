package com.uniovi.sercheduler.commands;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.uniovi.sercheduler.dto.wrench.HostWrench;
import com.uniovi.sercheduler.dto.wrench.ScheduleWrench;
import com.uniovi.sercheduler.dto.wrench.TaskWrench;
import com.uniovi.sercheduler.parser.HostLoader;
import com.uniovi.sercheduler.parser.experiment.ExperimentConfigLoader;
import com.uniovi.sercheduler.parser.experiment.ExperimentParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.shell.command.annotation.Command;
import org.springframework.shell.command.annotation.Option;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@Command
public class SimulateCommand {
  static final Logger LOG = LoggerFactory.getLogger(SimulateCommand.class);
  private final ExperimentParser experimentParser;
  private final HostLoader hostLoader;
  private final ExperimentConfigLoader experimentConfigLoader;

  public SimulateCommand(
      ExperimentParser experimentParser,
      HostLoader hostLoader,
      ExperimentConfigLoader experimentConfigLoader) {
    this.experimentParser = experimentParser;
    this.hostLoader = hostLoader;
    this.experimentConfigLoader = experimentConfigLoader;
  }

  @Command(command = "simulate")
  public String simulate(@Option(shortNames = 'C') String experimentConfigFile) {

    var experimentConfig = experimentConfigLoader.readFromFile(new File(experimentConfigFile));
    var benchmarks = experimentConfig.workflows();

    var hostsWrench = loadHostsWrench();

    for (var benchmark : benchmarks) {

      // Print the current user the Java process is running as
      String currentUser = System.getProperty("user.name");
      System.out.println("Current User: " + currentUser);
      for (var fitness : experimentConfig.fitness()) {

        for (int i = 0; i < experimentConfig.independentRuns(); i++) {
          SimulateExperiment(benchmark, fitness, i, hostsWrench);
        }
      }
    }

    return "simulated";
  }

  private void SimulateExperiment(
      String benchmark, String fitness, int run, List<HostWrench> hostsWrench) {
    var tasks =
        experimentParser.readVar(
            "experiments/Scheduling/data/"
                + fitness
                + "/"
                + benchmark
                + "-hosts-16"
                + "/VAR"
                + run
                + ".csv");
    String scheduleFilePath = "wrench/schedule.json";

    var simulations =
        tasks.stream()
            .map(task -> callWrench(hostsWrench, task, scheduleFilePath, "441Gf", benchmark))
            .toList();
    try {
      var path = Paths.get("wrench/experiments/" + fitness + "/" + benchmark + "-hosts-16");

      if (!Files.exists(path)) {
        Files.createDirectories(path); // Create the directories if they don't exist
      }
      FileWriter fileWriter =
          new FileWriter(
              "wrench/experiments/" + fitness + "/" + benchmark + "-hosts-16/FUN" + run + ".csv");
      for (String str : simulations) {

        fileWriter.write(str + System.lineSeparator());
      }
      fileWriter.close();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  private List<HostWrench> loadHostsWrench() {
    var hosts = hostLoader.readFromFile(new File("wrench/hosts.json"));
    return hosts.stream()
        .map(
            h ->
                new HostWrench(
                    h.name(),
                    h.cores().toString(),
                    h.cpuSpeed(),
                    h.diskSpeed(),
                    h.networkSpeed(),
                    h.energyCost().toString()))
        .toList();
  }

  private String callWrench(
      List<HostWrench> hostsWrench,
      List<TaskWrench> schedule,
      String scheduleFilePath,
      String referenceSpeed,
      String benchmark) {

    String lastLine;
    try {

      var scheduleWrench = new ScheduleWrench(hostsWrench, schedule, referenceSpeed);

      // Create the directory if it doesn't exist
      Path outputPath = Paths.get(scheduleFilePath);
      Path parentDir = outputPath.getParent();
      if (parentDir != null && !Files.exists(parentDir)) {
        Files.createDirectories(parentDir); // Create the directories if they don't exist
      }

      ObjectMapper objectMapper = new ObjectMapper();
      objectMapper
          .writerWithDefaultPrettyPrinter()
          .writeValue(new File(scheduleFilePath), scheduleWrench);

      // We need to read the schedule

      // Use ProcessBuilder to execute the file
      String workflow = "wrench/workflows/" + benchmark + ".json";
      // "wrench/workflows/1000genome-chameleon-2ch-250k"+"-001"+".json",
      String command = "/usr/local/bin/basic-simulation";
      String scheduleJson = "wrench/schedule.json";
      String options = "--wrench-energy-simulation";
      ProcessBuilder processBuilder = new ProcessBuilder(command, workflow, scheduleJson, options);

      processBuilder.redirectErrorStream(true); // Merge stdout and stderr
      Process process = processBuilder.start();

      // Read the output
      try (BufferedReader reader =
          new BufferedReader(new InputStreamReader(process.getInputStream()))) {
        // Stream the output and find the last line
        // reader.lines().forEach(System.out::println);
        lastLine = reader.lines().reduce((first, second) -> second).orElse(null);
      }
    } catch (IOException e) {
      throw new RuntimeException(e);
    }

    return lastLine;
  }
}
