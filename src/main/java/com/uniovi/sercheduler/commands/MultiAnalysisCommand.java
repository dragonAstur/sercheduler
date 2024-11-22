//package com.uniovi.sercheduler.commands;
//
//import com.uniovi.sercheduler.dto.BenchmarkData;
//import com.uniovi.sercheduler.jmetal.operator.ScheduleCrossover;
//import com.uniovi.sercheduler.jmetal.operator.ScheduleMutation;
//import com.uniovi.sercheduler.jmetal.operator.ScheduleReplacement;
//import com.uniovi.sercheduler.jmetal.operator.ScheduleSelection;
//import com.uniovi.sercheduler.jmetal.problem.SchedulePermutationSolution;
//import com.uniovi.sercheduler.jmetal.problem.SchedulingProblem;
//import com.uniovi.sercheduler.parser.HostLoader;
//import com.uniovi.sercheduler.parser.WorkflowLoader;
//import com.uniovi.sercheduler.service.Operators;
//import com.uniovi.sercheduler.service.ScheduleExporter;
//import com.uniovi.sercheduler.util.CsvUtils;
//import com.uniovi.sercheduler.util.ThreadSafeStringArray;
//import java.io.File;
//import java.io.FileOutputStream;
//import java.io.IOException;
//import java.nio.file.Files;
//import java.nio.file.Paths;
//import java.time.Duration;
//import java.time.Instant;
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.Comparator;
//import java.util.List;
//import java.util.Map;
//import java.util.Random;
//import org.apache.poi.ss.usermodel.Row;
//import org.apache.poi.ss.usermodel.Sheet;
//import org.apache.poi.ss.usermodel.Workbook;
//import org.apache.poi.xssf.usermodel.XSSFWorkbook;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.shell.command.annotation.Command;
//import org.springframework.shell.command.annotation.Option;
//import org.uma.jmetal.component.algorithm.EvolutionaryAlgorithm;
//import org.uma.jmetal.component.algorithm.singleobjective.GeneticAlgorithmBuilder;
//import org.uma.jmetal.component.catalogue.common.evaluation.impl.MultiThreadedEvaluation;
//import org.uma.jmetal.component.catalogue.common.termination.Termination;
//import org.uma.jmetal.component.catalogue.common.termination.impl.TerminationByEvaluations;
//import org.uma.jmetal.operator.crossover.CrossoverOperator;
//import org.uma.jmetal.operator.mutation.MutationOperator;
//
///** A command for analyzing the performance of multi fitness */
//@Command
//public class MultiAnalysisCommand {
//
//  static final Logger LOG = LoggerFactory.getLogger(MultiAnalysisCommand.class);
//  private static final int NUMBER_OF_REPEATS = 10;
//
//  final WorkflowLoader workflowLoader;
//  final HostLoader hostLoader;
//
//  final ScheduleExporter scheduleExporter;
//
//  final ThreadSafeStringArray fitnessUsage = ThreadSafeStringArray.getInstance();
//  ArrayList<String> rows = new ArrayList<>();
//
//  /**
//   * Full constructor.
//   *
//   * @param workflowLoader Loads workflows.
//   * @param hostLoader Loads hosts.
//   * @param scheduleExporter Exports the schedules.
//   */
//  public MultiAnalysisCommand(
//      WorkflowLoader workflowLoader, HostLoader hostLoader, ScheduleExporter scheduleExporter) {
//    this.workflowLoader = workflowLoader;
//    this.hostLoader = hostLoader;
//    this.scheduleExporter = scheduleExporter;
//  }
//
//  /**
//   * Command to run the GA in multiFitness and generate information.
//   *
//   * @param executions Number of schedule to generate
//   * @param seed Random seed to choose.
//   * @return The text to print at the end.
//   */
//  @Command(command = "analyze")
//  public String analyze(
//      @Option(shortNames = 'W') String workflowsPath,
//      @Option(shortNames = 'H') String hostsPath,
//      @Option(shortNames = 'T') String type,
//      @Option(shortNames = 'E', defaultValue = "100000") Integer executions,
//      @Option(shortNames = 'S', defaultValue = "1") Long seed) {
//    var benchmarks =
//        List.of(
//            "cycles-chameleon-1l-1c-9p-001"
//         );
//    Random random = new Random(seed);
//    var benchmarksResult = new ArrayList<BenchmarkData>();
//
//    for (var benchmark : benchmarks) {
//      var fitness = "multi";
//      for (int i = 1; i <= 16; i = i * 2) {
//
//        var benchmarkData =
//            doExperiment(
//                executions, seed, benchmark, type, i, fitness, random, workflowsPath, hostsPath);
//
//        benchmarksResult.add(benchmarkData);
//        LOG.info("Done benchmark {} with {} hosts and fitness {}", benchmark, i, fitness);
//      }
//    }
//
//    // Writing the solution to excel
//    try (Workbook workbook = new XSSFWorkbook()) {
//      Sheet sheet = workbook.createSheet("Data");
//
//      // Create header row
//      Row headerRow = sheet.createRow(0);
//      headerRow.createCell(0).setCellValue("Benchmark");
//      headerRow.createCell(1).setCellValue("Hosts");
//      headerRow.createCell(2).setCellValue("Fitness");
//      headerRow.createCell(3).setCellValue("Makespan");
//      headerRow.createCell(4).setCellValue("Time");
//
//      // Populate data
//      int rowNum = 1;
//
//      for (var result : benchmarksResult) {
//
//        Row row = sheet.createRow(rowNum++);
//        row.createCell(0).setCellValue(result.workflow());
//        row.createCell(1).setCellValue(result.hosts());
//        row.createCell(2).setCellValue(result.fitness());
//        row.createCell(3).setCellValue(result.makespan());
//        row.createCell(4).setCellValue(result.time());
//
//        // Write the workbook to a file
//        try (FileOutputStream outputStream =
//            new FileOutputStream(String.format("results-%s.xlsx", type))) {
//          workbook.write(outputStream);
//        }
//      }
//
//    } catch (IOException e) {
//      throw new RuntimeException(e);
//    }
//    return "All experiments done";
//  }
//
//  private BenchmarkData doExperiment(
//      Integer executions,
//      Long seed,
//      String benchmark,
//      String type,
//      int hosts,
//      String fitness,
//      Random random,
//      String workflowsPath,
//      String hostsPath) {
//    final Instant start = Instant.now();
//
//    var problem =
//        new SchedulingProblem(
//            new File(workflowsPath + benchmark + ".json"),
//            new File(hostsPath + type + "/hosts-" + hosts + ".json"),
//            "441Gf",
//            fitness,
//            seed);
//
//    Operators operators = new Operators(problem.getInstanceData(), random);
//    CrossoverOperator<SchedulePermutationSolution> crossover = new ScheduleCrossover(1, operators);
//
//    double mutationProbability = 0.1;
//    MutationOperator<SchedulePermutationSolution> mutation =
//        new ScheduleMutation(mutationProbability, operators);
//
//    int populationSize = 100;
//    int offspringPopulationSize = 100;
//    Termination termination = new TerminationByEvaluations(executions);
//
//    var makespans = new ArrayList<Double>();
//
//    fitnessUsage.recreateArray(executions);
//
//    for (int i = 0; i < NUMBER_OF_REPEATS; i++) {
//      EvolutionaryAlgorithm<SchedulePermutationSolution> gaAlgo =
//          new GeneticAlgorithmBuilder<>(
//                  "GGA", problem, populationSize, offspringPopulationSize, crossover, mutation)
//              .setTermination(termination)
//              .setEvaluation(new MultiThreadedEvaluation<>(16, problem))
//              .setSelection(new ScheduleSelection(random))
//              .setReplacement(new ScheduleReplacement(random))
//              .build();
//
//      gaAlgo.run();
//      var bestSolution =
//          gaAlgo.result().stream()
//              .min(Comparator.comparing(s -> s.getFitnessInfo().fitness().get("makespan")))
//              .orElseThrow();
//      makespans.add(bestSolution.objectives()[0]);
//      fitnessUsage.restartIndex();
//    }
//
//
//    // Export the single analysis
//    try {
//      Files.createDirectories(Paths.get("analysis"));
//      String fileName = String.format("analysis/occurences-%s-%s-%d.csv", type, benchmark, hosts);
//      CsvUtils.exportToCsv(fileName, Arrays.asList(fitnessUsage.getArray()));
//    } catch (Exception e) {
//      e.printStackTrace();
//    }
//
//    rows.add(benchmark + "-" + hosts);
//
//    var mean = makespans.stream().mapToDouble(Double::doubleValue).average().orElse(0.0);
//    Instant finish = Instant.now();
//
//    var timeElapsed = Duration.between(start, finish);
//    return new BenchmarkData(benchmark, hosts, fitness, mean, timeElapsed.toSeconds());
//  }
//}
