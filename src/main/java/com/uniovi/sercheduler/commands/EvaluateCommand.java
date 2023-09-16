package com.uniovi.sercheduler.commands;

import com.uniovi.sercheduler.jmetal.operator.ScheduleCrossover;
import com.uniovi.sercheduler.jmetal.operator.ScheduleMutation;
import com.uniovi.sercheduler.jmetal.operator.ScheduleReplacement;
import com.uniovi.sercheduler.jmetal.operator.ScheduleSelection;
import com.uniovi.sercheduler.jmetal.problem.SchedulePermutationSolution;
import com.uniovi.sercheduler.jmetal.problem.SchedulingProblem;
import com.uniovi.sercheduler.parser.HostLoader;
import com.uniovi.sercheduler.parser.WorkflowLoader;
import com.uniovi.sercheduler.service.Operators;
import com.uniovi.sercheduler.service.ScheduleExporter;
import java.io.File;
import java.time.Duration;
import java.time.Instant;
import java.util.Random;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.shell.command.annotation.Command;
import org.springframework.shell.command.annotation.Option;
import org.uma.jmetal.component.algorithm.EvolutionaryAlgorithm;
import org.uma.jmetal.component.algorithm.singleobjective.GeneticAlgorithmBuilder;
import org.uma.jmetal.component.catalogue.common.evaluation.impl.MultiThreadedEvaluation;
import org.uma.jmetal.component.catalogue.common.termination.Termination;
import org.uma.jmetal.component.catalogue.common.termination.impl.TerminationByEvaluations;
import org.uma.jmetal.operator.crossover.CrossoverOperator;
import org.uma.jmetal.operator.mutation.MutationOperator;
import org.uma.jmetal.util.fileoutput.SolutionListOutput;
import org.uma.jmetal.util.fileoutput.impl.DefaultFileOutputContext;
import org.uma.jmetal.util.observer.impl.FitnessObserver;

/** Contains all commands related to the execution of the GA. */
@Command
public class EvaluateCommand {

  static final Logger LOG = LoggerFactory.getLogger(EvaluateCommand.class);

  final WorkflowLoader workflowLoader;
  final HostLoader hostLoader;

  final ScheduleExporter scheduleExporter;

  /**
   * Full constructor.
   *
   * @param workflowLoader Loads workflows.
   * @param hostLoader Loads hosts.
   * @param scheduleExporter Exports the schedules.
   */
  public EvaluateCommand(
      WorkflowLoader workflowLoader, HostLoader hostLoader, ScheduleExporter scheduleExporter) {
    this.workflowLoader = workflowLoader;
    this.hostLoader = hostLoader;
    this.scheduleExporter = scheduleExporter;
  }

  /**
   * Command to run the GA for a given instance an infrastructure.
   *
   * @param hostsFile Relative or Absolute path to the hosts file.
   * @param workflowFile Relative or Absolute path to the workflow file.
   * @param executions Number of schedule to generate
   * @param seed Random seed to choose.
   * @return The text to print at the end.
   */
  @Command(command = "evaluate")
  public String evaluate(
      @Option(shortNames = 'H', required = true) String hostsFile,
      @Option(shortNames = 'W', required = true) String workflowFile,
      @Option(shortNames = 'E', defaultValue = "1000") Integer executions,
      @Option(shortNames = 'S', defaultValue = "1") Long seed,
      @Option(shortNames = 'F', defaultValue = "simple") String fitness) {
    final Instant start = Instant.now();

    var problem =
        new SchedulingProblem(new File(workflowFile), new File(hostsFile), "441Gf", fitness, seed);

    Operators operators = new Operators(problem.getInstanceData(), new Random(seed));
    CrossoverOperator<SchedulePermutationSolution> crossover = new ScheduleCrossover(1, operators);

    double mutationProbability = 0.1;
    MutationOperator<SchedulePermutationSolution> mutation =
        new ScheduleMutation(mutationProbability, operators);

    int populationSize = 100;
    int offspringPopulationSize = 100;

    Termination termination = new TerminationByEvaluations(executions);

    EvolutionaryAlgorithm<SchedulePermutationSolution> gaAlgo =
        new GeneticAlgorithmBuilder<>(
                "GGA", problem, populationSize, offspringPopulationSize, crossover, mutation)
            .setTermination(termination)
            .setEvaluation(new MultiThreadedEvaluation<>(8, problem))
            .setSelection(new ScheduleSelection(new Random(seed)))
            .setReplacement(new ScheduleReplacement(new Random(seed)))
            .build();

    gaAlgo.getObservable().register(new FitnessObserver(100));


    gaAlgo.run();

    var population = gaAlgo.getResult();
    LOG.info("Total execution time : {} ms", gaAlgo.getTotalComputingTime());
    LOG.info("Number of evaluations: {} ", gaAlgo.getNumberOfEvaluations());

    new SolutionListOutput(population)
        .setVarFileOutputContext(new DefaultFileOutputContext("VAR.csv", ","))
        .setFunFileOutputContext(new DefaultFileOutputContext("FUN.csv", ","))
        .print();

    Instant finish = Instant.now();

    var timeElapsed = Duration.between(start, finish);

    return String.format("Evaluation done, it took %d", timeElapsed.toSeconds());
  }
}
