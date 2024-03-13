package com.uniovi.sercheduler.commands;

import com.uniovi.sercheduler.dto.BenchmarkData;
import com.uniovi.sercheduler.jmetal.evaluation.MultiThreadEvaluationMulti;
import com.uniovi.sercheduler.jmetal.operator.ScheduleCrossover;
import com.uniovi.sercheduler.jmetal.operator.ScheduleMutation;
import com.uniovi.sercheduler.jmetal.problem.SchedulePermutationSolution;
import com.uniovi.sercheduler.jmetal.problem.SchedulingProblem;
import com.uniovi.sercheduler.parser.HostLoader;
import com.uniovi.sercheduler.parser.WorkflowLoader;
import com.uniovi.sercheduler.service.Operators;
import java.io.File;
import java.io.IOException;
import java.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.shell.command.annotation.Command;
import org.springframework.shell.command.annotation.Option;
import org.uma.jmetal.algorithm.Algorithm;
import org.uma.jmetal.component.algorithm.multiobjective.NSGAIIBuilder;
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
import org.uma.jmetal.qualityindicator.impl.Epsilon;
import org.uma.jmetal.qualityindicator.impl.GenerationalDistance;
import org.uma.jmetal.qualityindicator.impl.InvertedGenerationalDistance;
import org.uma.jmetal.qualityindicator.impl.InvertedGenerationalDistancePlus;
import org.uma.jmetal.qualityindicator.impl.NormalizedHypervolume;
import org.uma.jmetal.qualityindicator.impl.Spread;
import org.uma.jmetal.qualityindicator.impl.hypervolume.impl.PISAHypervolume;

/** Class for running experiments. */
@Command
public class ExperimentJmetalCommand {

  static final Logger LOG = LoggerFactory.getLogger(ExperimentJmetalCommand.class);
  private static final int NUMBER_OF_REPEATS = 10;
  private static final int INDEPENDENT_RUNS = 2;

  final WorkflowLoader workflowLoader;
  final HostLoader hostLoader;

  public ExperimentJmetalCommand(WorkflowLoader workflowLoader, HostLoader hostLoader) {
    this.workflowLoader = workflowLoader;
    this.hostLoader = hostLoader;
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
      @Option(shortNames = 'S', defaultValue = "1") Long seed) {
    var benchmarksResult = new ArrayList<BenchmarkData>();
    //    var benchmarks =
    //        List.of(
    //            "1000genome-chameleon-2ch-250k-001",
    //            "1000genome-chameleon-4ch-250k-001",
    //            "1000genome-chameleon-12ch-250k-001",
    //            "1000genome-chameleon-18ch-250k-001",
    //            "cycles-chameleon-1l-1c-9p-001",
    //            "cycles-chameleon-2l-1c-9p-001",
    //            "cycles-chameleon-2l-1c-12p-001",
    //            "cycles-chameleon-5l-1c-12p-001",
    //            "epigenomics-chameleon-hep-1seq-100k-001",
    //            "epigenomics-chameleon-hep-6seq-100k-001",
    //            "epigenomics-chameleon-ilmn-1seq-100k-001",
    //            "epigenomics-chameleon-ilmn-6seq-100k-001",
    //            "montage-chameleon-2mass-01d-001",
    //            "montage-chameleon-2mass-005d-001",
    //            "montage-chameleon-dss-10d-001",
    //            "montage-chameleon-dss-125d-001",
    //            "seismology-chameleon-100p-001",
    //            "seismology-chameleon-500p-001",
    //            "seismology-chameleon-700p-001",
    //            "seismology-chameleon-1000p-001",
    //            "soykb-chameleon-10fastq-10ch-001",
    //            "soykb-chameleon-10fastq-20ch-001",
    //            "soykb-chameleon-30fastq-10ch-001",
    //            "soykb-chameleon-40fastq-20ch-001",
    //            "srasearch-chameleon-10a-005",
    //            "srasearch-chameleon-20a-003",
    //            "srasearch-chameleon-40a-003",
    //            "srasearch-chameleon-50a-003");

    var benchmarks =
        List.of(
            "1000genome-chameleon-2ch-250k-001"
//            "cycles-chameleon-1l-1c-9p-001",
//            "epigenomics-chameleon-hep-1seq-100k-001",
//            "montage-chameleon-2mass-01d-001"
        );
    Random random = new Random(seed);

    var fitness = List.of("simple", "multi", "rank");
    var experimentBaseDirectory = "experiments";
    double mutationProbability = 0.1;
    int populationSize = 100;
    int offspringPopulationSize = 200;
    Termination termination = new TerminationByEvaluations(executions);
    List<ExperimentProblem<SchedulePermutationSolution>> problemList = new ArrayList<>();
    List<ExperimentAlgorithm<SchedulePermutationSolution, List<SchedulePermutationSolution>>>
        algorithmList = new ArrayList<>();

    for (var benchmark : benchmarks) {

      for (int i = 2; i <= 16; i = i * 8) {
        var baseProblem =
            new SchedulingProblem(
                benchmark + "-hosts-" + i,
                new File(workflowsPath + benchmark + ".json"),
                new File(hostsPath + type + "/hosts-" + i + ".json"),
                "441Gf",
                "simple",
                seed);

        var experimentProblem = new ExperimentProblem<>(baseProblem);
        problemList.add(experimentProblem);

        for (var f : fitness) {

          var problem =
              new SchedulingProblem(
                  benchmark + "-hosts-" + i,
                  new File(workflowsPath + benchmark + ".json"),
                  new File(hostsPath + type + "/hosts-" + i + ".json"),
                  "441Gf",
                  f,
                  seed);

          Operators operators = new Operators(problem.getInstanceData(), random);
          CrossoverOperator<SchedulePermutationSolution> crossover =
              new ScheduleCrossover(1, operators);

          MutationOperator<SchedulePermutationSolution> mutation =
              new ScheduleMutation(mutationProbability, operators);

          for (int run = 0; run < INDEPENDENT_RUNS; run++) {

            Algorithm<List<SchedulePermutationSolution>> algorithm =
                new NSGAIIBuilder<>(
                        problem, populationSize, offspringPopulationSize, crossover, mutation)
                    .setTermination(termination)
                    .setEvaluation(new MultiThreadEvaluationMulti(16, problem))
                    .build();
            algorithmList.add(new ExperimentAlgorithm<>(algorithm, f, experimentProblem, run));
          }

          LOG.info("Done benchmark {} with {} hosts and fitness {}", benchmark, i, f);
        }
      }
    }

    Experiment<SchedulePermutationSolution, List<SchedulePermutationSolution>> experiment =
        new ExperimentBuilder<SchedulePermutationSolution, List<SchedulePermutationSolution>>(
                "NSGAIIComputingReferenceParetoFrontsStudy")
            .setAlgorithmList(algorithmList)
            .setProblemList(problemList)
            .setExperimentBaseDirectory(experimentBaseDirectory)
            .setOutputParetoFrontFileName("FUN")
            .setOutputParetoSetFileName("VAR")
            .setReferenceFrontDirectory(
                experimentBaseDirectory
                    + "/NSGAIIComputingReferenceParetoFrontsStudy/referenceFronts")
            .setIndicatorList(
                List.of(
                    new Epsilon(),
                    new Spread(),
                    new GenerationalDistance(),
                    new PISAHypervolume(),
                    new NormalizedHypervolume(),
                    new InvertedGenerationalDistance(),
                    new InvertedGenerationalDistancePlus()))
            .setIndependentRuns(INDEPENDENT_RUNS)
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
}
