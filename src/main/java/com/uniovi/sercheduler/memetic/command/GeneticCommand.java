package com.uniovi.sercheduler.memetic.command;

import com.uniovi.sercheduler.dao.Objective;
import com.uniovi.sercheduler.dao.experiment.ExperimentConfig;
import com.uniovi.sercheduler.jmetal.operator.ScheduleCrossover;
import com.uniovi.sercheduler.jmetal.operator.ScheduleMutation;
import com.uniovi.sercheduler.jmetal.operator.ScheduleReplacement;
import com.uniovi.sercheduler.jmetal.operator.ScheduleSelection;
import com.uniovi.sercheduler.jmetal.problem.SchedulePermutationSolution;
import com.uniovi.sercheduler.jmetal.problem.SchedulingProblem;
import com.uniovi.sercheduler.parser.experiment.ExperimentConfigLoader;
import com.uniovi.sercheduler.service.Operators;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.shell.command.annotation.Command;
import org.springframework.shell.command.annotation.Option;
import org.uma.jmetal.algorithm.Algorithm;
import org.uma.jmetal.component.algorithm.EvolutionaryAlgorithm;
import org.uma.jmetal.component.algorithm.singleobjective.GeneticAlgorithmBuilder;
import org.uma.jmetal.component.catalogue.common.evaluation.impl.SequentialEvaluation;
import org.uma.jmetal.component.catalogue.common.termination.Termination;
import org.uma.jmetal.component.catalogue.common.termination.impl.TerminationByComputingTime;
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
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.uniovi.sercheduler.memetic.command.CommandUtils.*;

/** Class for running experiments using JMetal experiment tools. */
@Command
public class GeneticCommand {

  static final Logger LOG = LoggerFactory.getLogger(GeneticCommand.class);


  @Command(command = "genetic")
  public String experiment(
      @Option(shortNames = 'W') String workflowsPath,
      @Option(shortNames = 'H') String hostsPath,
      @Option(shortNames = 'T') String type,
      @Option(shortNames = 'L', defaultValue = "10000") Long limitTime,
      @Option(shortNames = 'S', defaultValue = "1") Long seed,
      @Option(shortNames = 'X', defaultValue = ".") String experimentPath,
      @Option(shortNames = 'C') String experimentConfigFile) {

    return executeGenetic(workflowsPath, hostsPath, type, limitTime, seed, experimentPath, experimentConfigFile);
  }

  public static String executeGenetic(String workflowsPath, String hostsPath, String type, Long limitTime, Long seed,
                                      String experimentPath, String experimentConfigFile) {

    var experimentConfig = new ExperimentConfigLoader().readFromFile(new File(experimentConfigFile));

    var benchmarks = experimentConfig.workflows();

    Random random = new Random(seed);

    var fitness = experimentConfig.fitness();

    var experimentBaseDirectory = experimentPath + "/executions";
    double mutationProbability = 0.1;
    int populationSize = 100;
    int offspringPopulationSize = 100;
    Termination termination = new TerminationByComputingTime(limitTime);
    List<ExperimentProblem<SchedulePermutationSolution>> problemList = new ArrayList<>();
    List<ExperimentAlgorithm<SchedulePermutationSolution, List<SchedulePermutationSolution>>>
        algorithmList = new ArrayList<>();
    List<SchedulingProblem> schedulingProblemList = new ArrayList<>();

    var objectives = experimentConfig.objectives().stream().map(Objective::of).toList();

    for (var benchmark : benchmarks) {

      for (int i = experimentConfig.minHosts();
          i <= experimentConfig.maxHosts();
          i = i * experimentConfig.hostIncrement()) {
        var baseProblem =
                createBaseProblem(workflowsPath, hostsPath, type, seed, benchmark, i, objectives); //TODO: aquí ponía "executions"

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

            //AlgoFlag flag = AlgoFlag.MONO;
            //TODO: aquí se examinaba la flag concreta especificada
            algorithm =
                    createGga(problem, populationSize, offspringPopulationSize, crossover, mutation, termination, random, objectives);


            algorithmList.add(new ExperimentAlgorithm<>(algorithm, f, experimentProblem, run));
          }

          LOG.info("Done benchmark {} with {} hosts and fitness {}", benchmark, i, f);
        }
      }
    }

    Experiment<SchedulePermutationSolution, List<SchedulePermutationSolution>> experiment =
            createExperiment(algorithmList, problemList, experimentBaseDirectory, experimentConfig);


    long start = System.currentTimeMillis();

    new ExecuteAlgorithms<>(experiment).run();

    long end = System.currentTimeMillis();

    try {

      doJmetalAnalysis(experimentConfig, experiment);

      CommandUtils.computeStatistics(experiment, objectives);

    } catch (IOException e) {
      throw new RuntimeException(e);
    }

    String result = "All experiments done Execution time: " + (end - start) + " ms";

    System.out.println( result );

    return result;
  }




}
