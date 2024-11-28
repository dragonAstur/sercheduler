package com.uniovi.sercheduler.commands;

import com.uniovi.sercheduler.dao.Objective;
import com.uniovi.sercheduler.jmetal.evaluation.MultiThreadEvaluationMulti;
import com.uniovi.sercheduler.jmetal.operator.ScheduleCrossover;
import com.uniovi.sercheduler.jmetal.operator.ScheduleMutation;
import com.uniovi.sercheduler.jmetal.operator.ScheduleReplacement;
import com.uniovi.sercheduler.jmetal.operator.ScheduleSelection;
import com.uniovi.sercheduler.jmetal.problem.SchedulePermutationSolution;
import com.uniovi.sercheduler.jmetal.problem.SchedulingProblem;
import com.uniovi.sercheduler.parser.HostLoader;
import com.uniovi.sercheduler.parser.WorkflowLoader;
import com.uniovi.sercheduler.parser.experiment.ExperimentConfigLoader;
import com.uniovi.sercheduler.service.Operators;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.shell.command.annotation.Command;
import org.springframework.shell.command.annotation.Option;
import org.uma.jmetal.algorithm.Algorithm;
import org.uma.jmetal.algorithm.multiobjective.pesa2.PESA2Builder;
import org.uma.jmetal.algorithm.multiobjective.spea2.SPEA2Builder;
import org.uma.jmetal.component.algorithm.multiobjective.NSGAIIBuilder;
import org.uma.jmetal.component.algorithm.singleobjective.GeneticAlgorithmBuilder;
import org.uma.jmetal.component.catalogue.common.evaluation.Evaluation;
import org.uma.jmetal.component.catalogue.common.evaluation.impl.MultiThreadedEvaluation;
import org.uma.jmetal.component.catalogue.common.termination.Termination;
import org.uma.jmetal.component.catalogue.common.termination.impl.TerminationByEvaluations;
import org.uma.jmetal.lab.experiment.Experiment;
import org.uma.jmetal.lab.experiment.ExperimentBuilder;
import org.uma.jmetal.lab.experiment.component.impl.ComputeQualityIndicators;
import org.uma.jmetal.lab.experiment.component.impl.ExecuteAlgorithms;
import org.uma.jmetal.lab.experiment.component.impl.GenerateBoxplotsWithR;
import org.uma.jmetal.lab.experiment.component.impl.GenerateFriedmanHolmTestTables;
import org.uma.jmetal.lab.experiment.component.impl.GenerateHtmlPages;
import org.uma.jmetal.lab.experiment.component.impl.GenerateLatexTablesWithStatistics;
import org.uma.jmetal.lab.experiment.component.impl.GenerateReferenceParetoFront;
import org.uma.jmetal.lab.experiment.component.impl.GenerateWilcoxonTestTablesWithR;
import org.uma.jmetal.lab.experiment.util.ExperimentAlgorithm;
import org.uma.jmetal.lab.experiment.util.ExperimentProblem;
import org.uma.jmetal.operator.crossover.CrossoverOperator;
import org.uma.jmetal.operator.mutation.MutationOperator;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.qualityindicator.impl.Epsilon;
import org.uma.jmetal.qualityindicator.impl.GenerationalDistance;
import org.uma.jmetal.qualityindicator.impl.InvertedGenerationalDistance;
import org.uma.jmetal.qualityindicator.impl.InvertedGenerationalDistancePlus;
import org.uma.jmetal.qualityindicator.impl.NormalizedHypervolume;
import org.uma.jmetal.qualityindicator.impl.Spread;
import org.uma.jmetal.qualityindicator.impl.hypervolume.impl.PISAHypervolume;

/** Class for running experiments using JMetal experiment tools. */
@Command
public class ExperimentJmetalCommand {

  static final Logger LOG = LoggerFactory.getLogger(ExperimentJmetalCommand.class);

  final WorkflowLoader workflowLoader;
  final HostLoader hostLoader;
  final ExperimentConfigLoader experimentConfigLoader;

  public ExperimentJmetalCommand(
      WorkflowLoader workflowLoader,
      HostLoader hostLoader,
      ExperimentConfigLoader experimentConfigLoader) {
    this.workflowLoader = workflowLoader;
    this.hostLoader = hostLoader;
    this.experimentConfigLoader = experimentConfigLoader;
  }

  /**
   * Runs an experiment with all available fitness functions.
   *
   * @param executions Number of evaluations before stopping.
   * @param seed The random seed.
   * @return An exit string.
   */
  @Command(command = "jmetal")
  public String experiment(
      @Option(shortNames = 'W') String workflowsPath,
      @Option(shortNames = 'H') String hostsPath,
      @Option(shortNames = 'T') String type,
      @Option(shortNames = 'E', defaultValue = "100000") Integer executions,
      @Option(shortNames = 'S', defaultValue = "1") Long seed,
      @Option(shortNames = 'C') String experimentConfigFile) {

    var experimentConfig = experimentConfigLoader.readFromFile(new File(experimentConfigFile));

    var benchmarks = experimentConfig.workflows();

    Random random = new Random(seed);

    var fitness = experimentConfig.fitness();

    var experimentBaseDirectory = "experiments";
    double mutationProbability = 0.1;
    int populationSize = 100;
    int offspringPopulationSize = 100;
    Termination termination = new TerminationByEvaluations(executions);
    List<ExperimentProblem<SchedulePermutationSolution>> problemList = new ArrayList<>();
    List<ExperimentAlgorithm<SchedulePermutationSolution, List<SchedulePermutationSolution>>>
        algorithmList = new ArrayList<>();

    var objectives = experimentConfig.objectives().stream().map(Objective::of).toList();

    for (var benchmark : benchmarks) {

      for (int i = experimentConfig.minHosts();
          i <= experimentConfig.maxHosts();
          i = i * experimentConfig.hostIncrement()) {
        var baseProblem =
            new SchedulingProblem(
                benchmark + "-hosts-" + i,
                new File(workflowsPath + benchmark + ".json"),
                new File(hostsPath + type + "/hosts-" + i + ".json"),
                "441Gf",
                "simple",
                seed,
                objectives,
                objectives.get(0).objectiveName);

        var experimentProblem = new ExperimentProblem<>(baseProblem);
        problemList.add(experimentProblem);

        for (var f : fitness) {

          var problem =
              new SchedulingProblem(
                  benchmark + "-hosts-" + i,
                  new File(workflowsPath + benchmark + ".json"),
                  new File(hostsPath + type + "/hosts-" + i + ".json"),
                  experimentConfig.referenceSpeed(),
                  f,
                  seed,
                  objectives,
                  objectives.get(0).objectiveName);

          Operators operators = new Operators(problem.getInstanceData(), random);
          CrossoverOperator<SchedulePermutationSolution> crossover =
              new ScheduleCrossover(1, operators);

          MutationOperator<SchedulePermutationSolution> mutation =
              new ScheduleMutation(mutationProbability, operators);

          for (int run = 0; run < experimentConfig.independentRuns(); run++) {
            Algorithm<List<SchedulePermutationSolution>> algorithm;

            if (f.contains("mono")) {
              algorithm =
                  new GeneticAlgorithmBuilder<>(
                          "GGA",
                          problem,
                          populationSize,
                          offspringPopulationSize,
                          crossover,
                          mutation)
                      .setTermination(termination)
                      .setEvaluation(
                          getEvaluator("simple", problem, objectives))
                      .setSelection(new ScheduleSelection(random))
                      .setReplacement(new ScheduleReplacement(random, objectives.get(0)))
                      .build();

            } else if (f.contains("spea2")) {
              algorithm =
                  new SPEA2Builder<>(problem, crossover, mutation)
                      .setPopulationSize(populationSize)
                      .setMaxIterations(executions / populationSize)
                      .build();

            } else if (f.contains("pesa2")) {
              algorithm =
                  new PESA2Builder<>(problem, crossover, mutation)
                      .setPopulationSize(populationSize)
                      .setMaxEvaluations(executions)
                      .build();
            } else {

              algorithm =
                  new NSGAIIBuilder<>(
                          problem, populationSize, offspringPopulationSize, crossover, mutation)
                      .setTermination(termination)
                      .setEvaluation(
                          getEvaluator("simple", problem, objectives))
                      .build();
            }

            algorithmList.add(new ExperimentAlgorithm<>(algorithm, f, experimentProblem, run));
          }

          LOG.info("Done benchmark {} with {} hosts and fitness {}", benchmark, i, f);
        }
      }
    }

    Experiment<SchedulePermutationSolution, List<SchedulePermutationSolution>> experiment =
        new ExperimentBuilder<SchedulePermutationSolution, List<SchedulePermutationSolution>>(
                "Scheduling")
            .setAlgorithmList(algorithmList)
            .setProblemList(problemList)
            .setExperimentBaseDirectory(experimentBaseDirectory)
            .setOutputParetoFrontFileName("FUN")
            .setOutputParetoSetFileName("VAR")
            .setReferenceFrontDirectory(experimentBaseDirectory + "/Scheduling/referenceFronts")
            .setIndicatorList(
                List.of(
                    new Epsilon(),
                    new Spread(),
                    new GenerationalDistance(),
                    new PISAHypervolume(),
                    new NormalizedHypervolume(),
                    new InvertedGenerationalDistance(),
                    new InvertedGenerationalDistancePlus()))
            .setIndependentRuns(experimentConfig.independentRuns())
            .setNumberOfCores(8)
            .build();
    new ExecuteAlgorithms<>(experiment).run();

    try {
      new GenerateReferenceParetoFront(experiment).run();
      new ComputeQualityIndicators<>(experiment).run();
      new GenerateLatexTablesWithStatistics(experiment).run();
      new GenerateFriedmanHolmTestTables<>(experiment).run();
      new GenerateWilcoxonTestTablesWithR<>(experiment).run();
      new GenerateBoxplotsWithR<>(experiment).setRows(3).setColumns(2).run();
      new GenerateHtmlPages<>(experiment).run();

    } catch (IOException e) {
      throw new RuntimeException(e);
    }

    return "All experiments done";
  }

  private Evaluation<SchedulePermutationSolution> getEvaluator(
      String evaluator, Problem<SchedulePermutationSolution> problem, List<Objective> objectives) {
    return switch (evaluator) {
      case "simple" -> new MultiThreadedEvaluation<>(0, problem);
      case "multi" -> new MultiThreadEvaluationMulti(0, problem, objectives.get(1).objectiveName);
      default -> new MultiThreadedEvaluation<>(0, problem);
    };
  }
}
