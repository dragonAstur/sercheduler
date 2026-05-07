package com.uniovi.sercheduler.memetic.command;

import com.uniovi.sercheduler.dao.Objective;
import com.uniovi.sercheduler.dao.experiment.ExperimentConfig;
import com.uniovi.sercheduler.jmetal.operator.ScheduleReplacement;
import com.uniovi.sercheduler.jmetal.operator.ScheduleSelection;
import com.uniovi.sercheduler.jmetal.problem.SchedulePermutationSolution;
import com.uniovi.sercheduler.jmetal.problem.SchedulingProblem;
import com.uniovi.sercheduler.localsearch.algorithms.localsearchcomponents.NeighborLimiterImpl;
import com.uniovi.sercheduler.localsearch.observer.Observer;
import com.uniovi.sercheduler.localsearch.operator.*;
import com.uniovi.sercheduler.memetic.algorithm.EvolutionaryAlgorithm;
import com.uniovi.sercheduler.memetic.algorithm.GeneticAlgorithmBuilder;
import com.uniovi.sercheduler.memetic.algorithm.MemeticAlgorithm;
import com.uniovi.sercheduler.memetic.algorithm.MemeticAlgorithmBuilder;
import com.uniovi.sercheduler.memetic.algorithm.components.*;
import com.uniovi.sercheduler.memetic.observer.MemeticObserver;

import org.uma.jmetal.component.catalogue.common.evaluation.impl.SequentialEvaluation;
import org.uma.jmetal.component.catalogue.common.termination.Termination;
import org.uma.jmetal.lab.experiment.Experiment;
import org.uma.jmetal.lab.experiment.ExperimentBuilder;
import org.uma.jmetal.lab.experiment.component.impl.*;
import org.uma.jmetal.lab.experiment.util.ExperimentAlgorithm;
import org.uma.jmetal.lab.experiment.util.ExperimentProblem;
import org.uma.jmetal.operator.crossover.CrossoverOperator;
import org.uma.jmetal.operator.mutation.MutationOperator;
import org.uma.jmetal.qualityindicator.impl.*;
import org.uma.jmetal.qualityindicator.impl.hypervolume.impl.PISAHypervolume;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class CommandUtils {

    public static final String DEFAULT_FILE_NAME = "experiment";

    protected static void computeStatistics(
            Experiment<SchedulePermutationSolution, List<SchedulePermutationSolution>> experiment,
            List<Objective> objectives)
            throws IOException {
        System.out.println("Computing statistics...");
        String outputDirectory = experiment.getExperimentBaseDirectory() + "/statistics/";
        new File(outputDirectory).mkdirs();

        // Iterate through all algorithms
        var algorithms =
                experiment.getAlgorithmList().stream()
                        .map(ExperimentAlgorithm::getAlgorithmTag)
                        .collect(Collectors.toSet());
        var workflows = experiment.getProblemList().stream().map(ExperimentProblem::getTag).toList();
        Map<String, List<ExecutionStat>> executionStatistics = new HashMap<>();
        for (var objective : objectives) {
            executionStatistics.put(objective.name(), new ArrayList<>());
        }

        for (String algorithm : algorithms) {

            for (var workflow : workflows) {

                Map<String, List<Double>> fitnessValues = new HashMap<>();
                for (var objective : objectives) {
                    fitnessValues.put(objective.name(), new ArrayList<>());
                }
                // Collect fitness values from independent runs
                for (int run = 0; run < experiment.getIndependentRuns(); run++) {
                    Map<String, List<Double>> fitnessValuesRun = new HashMap<>();
                    for (var objective : objectives) {
                        fitnessValuesRun.put(objective.name(), new ArrayList<>());
                    }
                    String resultFile =
                            experiment.getExperimentBaseDirectory()
                                    + "/data/"
                                    + algorithm
                                    + "/"
                                    + workflow
                                    + "/FUN"
                                    + run
                                    + ".csv";

                    // Read the best fitness from the result file
                    try (Scanner scanner = new Scanner(new File(resultFile))) {
                        while (scanner.hasNextLine()) {

                            var values = scanner.nextLine().trim().split(",");

                            int i = 0;
                            for (var objective : objectives) {
                                fitnessValuesRun.get(objective.name()).add(Double.parseDouble(values[i]));
                                i++;
                            }
                        }
                    }

                    for (var objective : objectives) {
                        Double bestOfTheRun =
                                fitnessValuesRun.get(objective.name()).stream()
                                        .mapToDouble(Double::doubleValue)
                                        .min()
                                        .orElseThrow();
                        fitnessValues.get(objective.name()).add(bestOfTheRun);
                    }
                }

                // Compute statistics for each metric
                for (var objective : objectives) {
                    DoubleSummaryStatistics stats =
                            fitnessValues.get(objective.name()).stream()
                                    .mapToDouble(Double::doubleValue)
                                    .summaryStatistics();
                    var executionName = workflow + "-" + algorithm;
                    executionStatistics
                            .get(objective.name())
                            .add(new ExecutionStat(executionName, workflow, algorithm, stats));

                    System.out.printf(
                            "Statistics for %s and objective %s: Mean = %.4f, Std. Dev. = %.4f, Min = %.4f, Max = %.4f%n",
                            executionName,
                            objective.name(),
                            stats.getAverage(),
                            Math.sqrt(
                                    fitnessValues.get(objective.name()).stream()
                                            .mapToDouble(val -> Math.pow(val - stats.getAverage(), 2))
                                            .sum()
                                            / stats.getCount()),
                            stats.getMin(),
                            stats.getMax());
                }
            }
        }

        // Write statistics to a CSV file for each algorithm
        try (FileWriter writer = new FileWriter(outputDirectory + "stats.csv")) {

            // TODO: Change to be compatible with more than two objectives
            var tableObjectives = List.of(Objective.ENERGY, Objective.MAKESPAN);
            String objective1 = tableObjectives.get(0).name();
            String objective2 = tableObjectives.get(1).name();

            writer.write(
                    String.format(
                            "Execution,Algorithm,Workflow,Hosts,Best %s,Mean %s,Min %s,Max %s,Best %s,Mean %s,Min %s,Max %s\n",
                            objective1,
                            objective1,
                            objective1,
                            objective1,
                            objective2,
                            objective2,
                            objective2,
                            objective2));
            for (int i = 0; i < executionStatistics.get(tableObjectives.get(0).name()).size(); i++) {
                // Find the hosts number with a regex
                var workflowName = executionStatistics.get(objective1).get(i).workflow();
                Pattern pattern = Pattern.compile(".*-hosts-(\\d+)$");
                Matcher matcher = pattern.matcher(workflowName);
                int hostsNumber = 0;
                if (matcher.find()) {
                    hostsNumber = Integer.parseInt(matcher.group(1));
                }

                writer.write(
                        String.format(
                                "%s,%s,%s,%d,%f,%f,%f,%f,%f,%f,%f,%f\n",
                                executionStatistics.get(objective1).get(i).executionName(),
                                executionStatistics.get(objective1).get(i).algorithm(),
                                executionStatistics.get(objective1).get(i).workflow(),
                                hostsNumber,
                                executionStatistics.get(objective1).get(i).statistics().getMin(),
                                executionStatistics.get(objective1).get(i).statistics().getAverage(),
                                executionStatistics.get(objective1).get(i).statistics().getMin(),
                                executionStatistics.get(objective1).get(i).statistics().getMax(),
                                executionStatistics.get(objective2).get(i).statistics().getMin(),
                                executionStatistics.get(objective2).get(i).statistics().getAverage(),
                                executionStatistics.get(objective2).get(i).statistics().getMin(),
                                executionStatistics.get(objective2).get(i).statistics().getMax()));
            }
        }
    }

    protected static SchedulingProblem createBaseProblem(String workflowsPath, String hostsPath, String type, Long seed,
                                                         String benchmark, int i, List<Objective> objectives) {
        return new SchedulingProblem(
                benchmark + "-hosts-" + i,
                new File(workflowsPath + benchmark + ".json"),
                new File(hostsPath + type + "/hosts-" + i + ".json"),
                "441Gf",
                "simple",
                seed,
                objectives,
                objectives.get(0).objectiveName,
                0);
    }

    protected static SchedulingProblem createSpecificProblem(String workflowsPath, String hostsPath, String type, Long seed,
                                                             String benchmark, String f, int i, ExperimentConfig experimentConfig,
                                                             List<Objective> objectives) {
        return new SchedulingProblem(
                benchmark + "-hosts-" + i,
                new File(workflowsPath + benchmark + ".json"),
                new File(hostsPath + type + "/hosts-" + i + ".json"),
                experimentConfig.referenceSpeed(),
                f,
                seed,
                objectives,
                objectives.get(0).objectiveName,
                0);
    }

    protected static EvolutionaryAlgorithm<SchedulePermutationSolution> createGga(SchedulingProblem problem,
                                                                                  int populationSize, int offspringPopulationSize,
                                                                                  CrossoverOperator<SchedulePermutationSolution> crossover,
                                                                                  MutationOperator<SchedulePermutationSolution> mutation,
                                                                                  MemeticTermination termination, Random random,
                                                                                  List<Objective> objectives, MemeticObserver observer, String fileName) {
        return new GeneticAlgorithmBuilder<>(
                "GGA",
                problem,
                populationSize,
                offspringPopulationSize,
                crossover,
                mutation,
                fileName)
                .setTermination(termination)
                .setEvaluation(new MemeticSequentialEvaluation<>(problem)) //TODO: aquí había una llamada al método privado "getEvaluator()"
                .setSelection(new MemeticScheduleSelection(random))
                .setReplacement(new MemeticScheduleReplacement(random, objectives.get(0)))
                .setObserver(observer)
                .build();
    }

    protected static MemeticAlgorithm createMA(SchedulingProblem problem, int populationSize, int offspringPopulationSize,
                                               CrossoverOperator<SchedulePermutationSolution> crossover,
                                               MutationOperator<SchedulePermutationSolution> mutation, Termination termination,
                                               Random random, List<Objective> objectives, long limitTime,
                                               List<NeighborhoodOperatorLazy> operatorList,
                                               int lsaIterationsLimit, MemeticObserver observer, String fileName,
                                               String memeticAlgorithmName, int neighborsLimit) {
        return switch (memeticAlgorithmName.toLowerCase()) {
            case "mae" -> createMAe(problem, populationSize, offspringPopulationSize, crossover, mutation, termination,
                    random, objectives, limitTime, operatorList, lsaIterationsLimit, observer, fileName, neighborsLimit);
            case "ma5" -> createMAPercentage(problem, populationSize, offspringPopulationSize, crossover, mutation, termination,
                    random, objectives, limitTime, operatorList, lsaIterationsLimit, observer, fileName, 0.05,
                    neighborsLimit);
            case "ma20" -> createMAPercentage(problem, populationSize, offspringPopulationSize, crossover, mutation, termination,
                    random, objectives, limitTime, operatorList, lsaIterationsLimit, observer, fileName, 0.2,
                    neighborsLimit);
            case "ma100" -> createMAPercentage(problem, populationSize, offspringPopulationSize, crossover, mutation, termination,
                    random, objectives, limitTime, operatorList, lsaIterationsLimit, observer, fileName, 1,
                    neighborsLimit);
            default ->
                    throw new IllegalArgumentException("Could not identify this type of memetic algorithm: " + memeticAlgorithmName);
        };
    }

    private static MemeticAlgorithm createMAPercentage(SchedulingProblem problem, int populationSize, int offspringPopulationSize,
                                              CrossoverOperator<SchedulePermutationSolution> crossover,
                                              MutationOperator<SchedulePermutationSolution> mutation, Termination termination,
                                              Random random, List<Objective> objectives, long limitTime,
                                              List<NeighborhoodOperatorLazy> operatorList,
                                              int lsaIterationsLimit, MemeticObserver observer, String fileName,
                                              double percentage, int neighborsLimit){
        return new MemeticAlgorithmBuilder(
                "Memetic",
                problem,
                populationSize,
                offspringPopulationSize,
                crossover,
                mutation,
                limitTime,
                operatorList,
                lsaIterationsLimit,
                fileName)
                .setTermination(termination)
                .setEvaluation(new MemeticSequentialEvaluation<>(problem))
                .setSelection(new ScheduleSelection(random))
                .setReplacement(new ScheduleReplacement(random, objectives.get(0)))
                .setNeighborLimiter(new NeighborLimiterImpl(neighborsLimit))
                .setLsaApplier(new PercentageLsaApplier(percentage))
                .setObserver(observer)
                .build();
    }

    private static MemeticAlgorithm createMAe(SchedulingProblem problem, int populationSize, int offspringPopulationSize,
                                              CrossoverOperator<SchedulePermutationSolution> crossover,
                                              MutationOperator<SchedulePermutationSolution> mutation, Termination termination,
                                              Random random, List<Objective> objectives, long limitTime,
                                              List<NeighborhoodOperatorLazy> operatorList,
                                              int lsaIterationsLimit, MemeticObserver observer, String fileName,
                                              int neighborsLimit){
        return new MemeticAlgorithmBuilder(
                "Memetic",
                problem,
                populationSize,
                offspringPopulationSize,
                crossover,
                mutation,
                limitTime,
                operatorList,
                lsaIterationsLimit,
                fileName)
                .setTermination(termination)
                .setEvaluation(new MemeticSequentialEvaluation<>(problem)) //TODO: aquí había una llamada al método privado "getEvaluator()"
                .setSelection(new ScheduleSelection(random))
                .setReplacement(new ScheduleReplacement(random, objectives.get(0)))
                .setNeighborLimiter(new NeighborLimiterImpl(neighborsLimit))
                .setObserver(observer)
                .build();
    }

    protected static Experiment<SchedulePermutationSolution, List<SchedulePermutationSolution>> createExperiment(List<ExperimentAlgorithm<SchedulePermutationSolution, List<SchedulePermutationSolution>>> algorithmList, List<ExperimentProblem<SchedulePermutationSolution>> problemList, String experimentBaseDirectory, ExperimentConfig experimentConfig) {
        return new ExperimentBuilder<SchedulePermutationSolution, List<SchedulePermutationSolution>>(
                "Scheduling")
                .setAlgorithmList(algorithmList)
                .setProblemList(problemList)
                .setExperimentBaseDirectory(experimentBaseDirectory)
                .setOutputParetoFrontFileName("FUN")
                .setOutputParetoSetFileName("VAR")
                .setReferenceFrontDirectory(experimentBaseDirectory + "/Scheduling/referenceFronts")
                .setIndicatorList(
                        List.of(
                                new PISAHypervolume(),
                                new InvertedGenerationalDistance(),
                                new InvertedGenerationalDistancePlus(),
                                new GenerationalDistance(),
                                new Epsilon(),
                                new Spread()))
                .setIndependentRuns(experimentConfig.independentRuns())
                .build();
    }

    protected static void doJmetalAnalysis(ExperimentConfig experimentConfig, Experiment<SchedulePermutationSolution, List<SchedulePermutationSolution>> experiment) throws IOException {
        if (experimentConfig.jmetalAnalysis()) {
            new GenerateReferenceParetoFront(experiment).run();
            new ComputeQualityIndicators<>(experiment).run();
            new GenerateLatexTablesWithStatistics(experiment).run();
            new GenerateFriedmanHolmTestTables<>(experiment).run();
            new GenerateWilcoxonTestTablesWithR<>(experiment).run();
            new GenerateBoxplotsWithR<>(experiment).setRows(3).setColumns(2).run();
            new GenerateHtmlPages<>(experiment).run();
        }
    }


    protected static List<NeighborhoodOperatorLazy> getOperatorsList(String operatorConfigName, SchedulingProblem problem){

        return
                switch (operatorConfigName.toLowerCase()) {
                    case "n1" -> new ArrayList<>(List.of(
                            new NeighborhoodChangeHostLazy(problem.getInstanceData())
                    ));
                    case "n2" -> new ArrayList<>(List.of(
                            new NeighborhoodInsertionLazy()
                    ));
                    case "n3" -> new ArrayList<>(List.of(
                            new NeighborhoodSwapLazy()
                    ));
                    case "n4" -> new ArrayList<>(List.of(
                            new NeighborhoodSwapHostLazy()
                    ));
                    case "n1un2" -> new ArrayList<>(List.of(
                            new NeighborhoodChangeHostLazy(problem.getInstanceData()),
                            new NeighborhoodInsertionLazy()
                    ));

                    case "n1un3" -> new ArrayList<>(List.of(
                            new NeighborhoodChangeHostLazy(problem.getInstanceData()),
                            new NeighborhoodSwapLazy()
                    ));
                    case "n1un4" -> new ArrayList<>(List.of(
                            new NeighborhoodChangeHostLazy(problem.getInstanceData()),
                            new NeighborhoodSwapHostLazy()
                    ));
                    case "n2un3" -> new ArrayList<>(List.of(
                            new NeighborhoodInsertionLazy(),
                            new NeighborhoodSwapLazy()
                    ));

                    case "n2un4" -> new ArrayList<>(List.of(
                            new NeighborhoodInsertionLazy(),
                            new NeighborhoodSwapHostLazy()
                    ));
                    case "n3un4" -> new ArrayList<>(List.of(
                            new NeighborhoodSwapLazy(),
                            new NeighborhoodSwapHostLazy()
                    ));
                    case "n1un2un3" -> new ArrayList<>(List.of(
                            new NeighborhoodChangeHostLazy(problem.getInstanceData()),
                            new NeighborhoodInsertionLazy(),
                            new NeighborhoodSwapLazy()
                    ));
                    case "n1un2un4" -> new ArrayList<>(List.of(
                            new NeighborhoodChangeHostLazy(problem.getInstanceData()),
                            new NeighborhoodInsertionLazy(),
                            new NeighborhoodSwapHostLazy()
                    ));
                    case "n1un3un4" -> new ArrayList<>(List.of(
                            new NeighborhoodChangeHostLazy(problem.getInstanceData()),
                            new NeighborhoodSwapLazy(),
                            new NeighborhoodSwapHostLazy()
                    ));
                    case "n2un3un4" -> new ArrayList<>(List.of(
                            new NeighborhoodInsertionLazy(),
                            new NeighborhoodSwapLazy(),
                            new NeighborhoodSwapHostLazy()
                    ));
                    case "n1un2un3un4", "vns" -> new ArrayList<>(List.of(
                            new NeighborhoodChangeHostLazy(problem.getInstanceData()),
                            new NeighborhoodInsertionLazy(),
                            new NeighborhoodSwapLazy(),
                            new NeighborhoodSwapHostLazy()
                    ));
                    default -> new ArrayList<>();
                };
    }

    public static int generateRunId(){
        return new Random().nextInt(10000000) + 1;
    }

    public static String createFileName(String originalFileName, String algorithmName, Long limitTime, long periodicTimeForMakespanEvolution,
                                        ExperimentConfig experimentConfig, String operatorConfig, String strategy,
                                        String lsaIterationsLimit, int id, int populationSize, int neighborsLimit,
                                        boolean isTagachi){

        if(!originalFileName.equals(CommandUtils.DEFAULT_FILE_NAME))
            return originalFileName;

        String date = LocalDate.now().format(DateTimeFormatter.ofPattern("dd-MM-yy"));
        String time  = LocalTime.now().format(DateTimeFormatter.ofPattern("HH-mm"));

        String fileName = algorithmName + "_" + limitTime / 1000 + "(s)_" + periodicTimeForMakespanEvolution + "(ms)_"
                + experimentConfig.maxHosts() + "_" + operatorConfig + "_" + strategy + "_" + lsaIterationsLimit
                + "it_" + neighborsLimit + "neighbors_" + experimentConfig.workflows().get(0) + "_" + populationSize
                + "pop_" + date + "_" + time + "_" + id;

        return isTagachi? "tagachi_" + fileName : fileName;
    }

    public static Random generateRandom(Long seed){
        return seed == 0 ? new Random() : new Random(seed);
    }

    public static String generateMAName(String lsaApplierName){
        return switch (lsaApplierName.toLowerCase()) {
            case "elitist", "e" -> "MAe";
            case "5percent", "5" -> "MA5";
            case "20percent", "20" -> "MA20";
            case "100percent", "100" -> "MA100";
            default -> throw new IllegalArgumentException("Could not find any LSA applier name that matches with " + lsaApplierName);
        };
    }

}
