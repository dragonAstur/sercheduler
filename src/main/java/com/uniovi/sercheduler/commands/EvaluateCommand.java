package com.uniovi.sercheduler.commands;

import com.uniovi.sercheduler.dto.InstanceData;
import com.uniovi.sercheduler.parser.HostLoader;
import com.uniovi.sercheduler.parser.WorkflowLoader;
import com.uniovi.sercheduler.service.FitnessCalculator;
import com.uniovi.sercheduler.service.FitnessCalculatorHeft;
import com.uniovi.sercheduler.service.FitnessCalculatorSimple;
import com.uniovi.sercheduler.service.PlanGenerator;
import com.uniovi.sercheduler.util.UnitParser;
import java.io.File;
import java.time.Duration;
import java.time.Instant;
import java.util.Comparator;
import java.util.Random;
import java.util.stream.IntStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.shell.command.annotation.Command;
import org.springframework.shell.command.annotation.Option;

/** Contains all commands related to the execution of the GA. */
@Command
public class EvaluateCommand {

  static final Logger LOG = LoggerFactory.getLogger(EvaluateCommand.class);

  final WorkflowLoader workflowLoader;
  final HostLoader hostLoader;

  public EvaluateCommand(WorkflowLoader workflowLoader, HostLoader hostLoader) {
    this.workflowLoader = workflowLoader;
    this.hostLoader = hostLoader;
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
    Instant start = Instant.now();

    LOG.info("Loading {} host file", hostsFile);
    var hosts = hostLoader.load(hostLoader.readFromFile(new File(hostsFile)));
    LOG.info("Loaded hosts with {} hosts", hosts.size());

    LOG.info("Loading {} workflow file", workflowFile);
    var workflow = workflowLoader.load(workflowLoader.readFromFile(new File(workflowFile)));
    LOG.info("Loaded workflow with {} tasks", workflow.size());
    var instanceData = new InstanceData(workflow, hosts,UnitParser.parseUnits("441Gf"));

    var fitnessCalculator = chooseFitness(fitness, instanceData);
    var planGenerator = new PlanGenerator(new Random(seed), instanceData);


    var bestSchedule =
        IntStream.range(0, executions)
            .mapToObj(u -> planGenerator.generatePlan())
            .map(p -> fitnessCalculator.calculateFitness(p))
            .min(Comparator.comparing(f -> f.fitness().get("makespan")))
            .orElseThrow();

    LOG.info(
        "Evaluation complete, the workflow is going to take {} seconds",
        bestSchedule.fitness().get("makespan"));

    Instant finish = Instant.now();

    var timeElapsed = Duration.between(start, finish);
    return String.format("Evaluation done, it took %d", timeElapsed.toSeconds());
  }

  private FitnessCalculator chooseFitness(String fitness, InstanceData instanceData) {
    return switch (fitness) {
      case "simple" -> new FitnessCalculatorSimple(instanceData);
      case "heft" -> new FitnessCalculatorHeft(instanceData);
      default -> throw new IllegalStateException("Unexpected value: " + fitness);
    };
  }
}
