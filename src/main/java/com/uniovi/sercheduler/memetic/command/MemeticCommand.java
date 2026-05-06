package com.uniovi.sercheduler.memetic.command;

import com.uniovi.sercheduler.dao.Objective;
import com.uniovi.sercheduler.dao.experiment.ExperimentConfig;
import com.uniovi.sercheduler.jmetal.operator.ScheduleCrossover;
import com.uniovi.sercheduler.jmetal.operator.ScheduleMutation;
import com.uniovi.sercheduler.jmetal.problem.SchedulePermutationSolution;
import com.uniovi.sercheduler.jmetal.problem.SchedulingProblem;
import com.uniovi.sercheduler.localsearch.export.CSVExporter;
import com.uniovi.sercheduler.localsearch.operator.NeighborhoodOperatorLazy;
import com.uniovi.sercheduler.memetic.observer.MemeticObserver;
import com.uniovi.sercheduler.parser.experiment.ExperimentConfigLoader;
import com.uniovi.sercheduler.service.Operators;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.shell.command.annotation.Command;
import org.springframework.shell.command.annotation.Option;
import org.uma.jmetal.algorithm.Algorithm;
import org.uma.jmetal.component.catalogue.common.termination.Termination;
import org.uma.jmetal.component.catalogue.common.termination.impl.TerminationByComputingTime;
import org.uma.jmetal.lab.experiment.Experiment;
import org.uma.jmetal.lab.experiment.component.impl.ExecuteAlgorithms;
import org.uma.jmetal.lab.experiment.util.ExperimentAlgorithm;
import org.uma.jmetal.lab.experiment.util.ExperimentProblem;
import org.uma.jmetal.operator.crossover.CrossoverOperator;
import org.uma.jmetal.operator.mutation.MutationOperator;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static com.uniovi.sercheduler.memetic.command.CommandUtils.*;

@Command
public class MemeticCommand {

    static final Logger LOG = LoggerFactory.getLogger(GeneticCommand.class);

    //java -jar sercheduler-0.0.1-SNAPSHOT.jar memetic -W workflows/ -H hosts/ -T scenario1 -S 1 -L 120000 -C experimentConfig.json -E 100 -N pelayo -O n1

    @Command(command = "memetic")
    public String experiment(
            @Option(shortNames = 'W') String workflowsPath,
            @Option(shortNames = 'H') String hostsPath,
            @Option(shortNames = 'T') String type,
            @Option(shortNames = 'L', defaultValue = "100000") Long limitTime,
            @Option(shortNames ='J', defaultValue = "3") int lsaIterationsLimit,
            @Option(shortNames = 'S', defaultValue = "0") Long seed,
            @Option(shortNames = 'X', defaultValue = ".") String experimentPath,
            @Option(shortNames = 'C') String experimentConfigFile,
            @Option(shortNames = 'E', defaultValue = "-1") long periodicTimeForMakespanEvolution,
            @Option(shortNames = 'N', defaultValue = CommandUtils.DEFAULT_FILE_NAME) String fileName,
            @Option(shortNames = 'O', defaultValue = "n1") String operatorConfig,
            @Option(shortNames = 'A', defaultValue = "e") String lsaApplierName,
            @Option(shortNames = 'P', defaultValue = "100") int populationSize,
            @Option(shortNames = 'V', defaultValue = "-1") int neighborsLimit) {

        return executeMemetic(workflowsPath, hostsPath, type, limitTime, seed, experimentPath, experimentConfigFile,
                periodicTimeForMakespanEvolution, fileName, operatorConfig, lsaIterationsLimit, lsaApplierName,
                populationSize, neighborsLimit);
    }

    public static String executeMemetic(String workflowsPath, String hostsPath, String type, Long limitTime, Long seed,
                                        String experimentPath, String experimentConfigFile, long periodicTimeForMakespanEvolution,
                                        String fileName, String operatorConfig, int lsaIterationsLimit, String lsaApplierName,
                                        int populationSize, int neighborsLimit) {

        ExperimentConfig experimentConfig = new ExperimentConfigLoader().readFromFile(new File(experimentConfigFile));

        int id = CommandUtils.generateRunId();

        String memeticAlgorithmName = CommandUtils.generateMAName(lsaApplierName);

        fileName = CommandUtils.createFileName(fileName, memeticAlgorithmName, limitTime, periodicTimeForMakespanEvolution,
                experimentConfig, operatorConfig, "HC", String.valueOf(lsaIterationsLimit), id, populationSize,
                neighborsLimit);

        var benchmarks = experimentConfig.workflows();

        Random random = CommandUtils.generateRandom(seed);

        var fitness = experimentConfig.fitness();

        var experimentBaseDirectory = experimentPath + "/executions-" + memeticAlgorithmName + "-" + id;
        double mutationProbability = 0.1;
        int offspringPopulationSize = populationSize;
        Termination termination = new TerminationByComputingTime(limitTime);
        List<ExperimentProblem<SchedulePermutationSolution>> problemList = new ArrayList<>();
        List<ExperimentAlgorithm<SchedulePermutationSolution, List<SchedulePermutationSolution>>>
                algorithmList = new ArrayList<>();
        List<SchedulingProblem> schedulingProblemList = new ArrayList<>();

        var objectives = experimentConfig.objectives().stream().map(Objective::of).toList();

        MemeticObserver observer;

        //XLSXTableExporter.createMemeticWorkbook(fileName);
        CSVExporter.createMemeticCSV(fileName);


        for (var benchmark : benchmarks) {

            for (int i = experimentConfig.minHosts();
                 i <= experimentConfig.maxHosts();
                 i = i * experimentConfig.hostIncrement()) {

                var baseProblem =
                        createBaseProblem(workflowsPath, hostsPath, type, seed, benchmark, i, objectives);

                var experimentProblem = new ExperimentProblem<>(baseProblem);
                problemList.add(experimentProblem);

                for (var f : fitness) {

                    var problem =
                            createSpecificProblem(workflowsPath, hostsPath, type, seed, benchmark, f, i, experimentConfig, objectives);
                    schedulingProblemList.add(problem);


                    Operators operators = new Operators(problem.getInstanceData(), random);

                    CrossoverOperator<SchedulePermutationSolution> crossover =
                            new ScheduleCrossover(1, operators);

                    MutationOperator<SchedulePermutationSolution> mutation =
                            new ScheduleMutation(mutationProbability, operators);

                    for (int run = 0; run < experimentConfig.independentRuns(); run++) {

                        Algorithm<List<SchedulePermutationSolution>> algorithm;
                        observer = new MemeticObserver("HC", operatorConfig, periodicTimeForMakespanEvolution);

                        List<NeighborhoodOperatorLazy> operatorList = getOperatorsList(operatorConfig, problem);

                        algorithm =
                                createMA(problem, populationSize, offspringPopulationSize, crossover, mutation,
                                        termination, random, objectives, limitTime, operatorList, lsaIterationsLimit,
                                        observer, fileName, memeticAlgorithmName, neighborsLimit);


                        algorithmList.add(new ExperimentAlgorithm<>(algorithm, f, experimentProblem, run));
                    }

                    LOG.info("Done benchmark {} with {} hosts and fitness {}", benchmark, i, f);
                }
            }
        }

        Experiment<SchedulePermutationSolution, List<SchedulePermutationSolution>> experiment =
                CommandUtils.createExperiment(algorithmList, problemList, experimentBaseDirectory, experimentConfig);


        long start = System.currentTimeMillis();

        new ExecuteAlgorithms<>(experiment).run();

        long end = System.currentTimeMillis();

        try {

            CommandUtils.doJmetalAnalysis(experimentConfig, experiment);

            CommandUtils.computeStatistics(experiment, objectives);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        String result = "All experiments done Execution time: " + (end - start) + " ms";

        System.out.println( result );

        return result;
    }
}
