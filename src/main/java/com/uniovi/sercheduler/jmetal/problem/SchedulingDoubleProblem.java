package com.uniovi.sercheduler.jmetal.problem;

import com.uniovi.sercheduler.dao.Objective;
import com.uniovi.sercheduler.dto.InstanceData;
import com.uniovi.sercheduler.dto.analysis.MultiResult;
import com.uniovi.sercheduler.parser.HostFileLoader;
import com.uniovi.sercheduler.parser.HostLoader;
import com.uniovi.sercheduler.parser.WorkflowFileLoader;
import com.uniovi.sercheduler.parser.WorkflowLoader;
import com.uniovi.sercheduler.service.PlanGenerator;
import com.uniovi.sercheduler.service.PlanPair;
import com.uniovi.sercheduler.service.calculator.FitnessCalculator;
import com.uniovi.sercheduler.service.core.RandomKeysCoder;
import com.uniovi.sercheduler.util.UnitParser;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.IntStream;

import org.uma.jmetal.problem.doubleproblem.DoubleProblem;
import org.uma.jmetal.problem.permutationproblem.PermutationProblem;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.solution.doublesolution.impl.DefaultDoubleSolution;
import org.uma.jmetal.util.bounds.Bounds;

/** Defines the scheduling problem. */
public class SchedulingDoubleProblem implements DoubleProblem {

  private final WorkflowLoader workflowLoader;
  private final HostLoader hostLoader;
  private final PlanGenerator planGenerator;

  private final FitnessCalculator fitnessCalculator;
  private InstanceData instanceData;
  private String name;
  private List<Objective> objectives;
  private String defaultArbiter;
  private ArrayList<MultiResult> evaluationsHistory;
  private final RandomKeysCoder randomKeysCoder;
  private final List<Bounds<Double>> bounds;

  /**
   * Full constructor.
   *
   * @param workflowFile The file containing the workflow.
   * @param hostsFile The file containing the hosts.
   * @param referenceSpeed The CPU reference speed to calculate the runtime.
   * @param fitness The fitness function to use.
   * @param seed The random seed to use
   */
  public SchedulingDoubleProblem(
      File workflowFile,
      File hostsFile,
      String referenceSpeed,
      String fitness,
      Long seed,
      List<Objective> objectives,
      String defaultArbiter,
      int evaluations) {
    this.workflowLoader = new WorkflowFileLoader();
    this.hostLoader = new HostFileLoader();
    this.instanceData = loadData(workflowFile, hostsFile, referenceSpeed);
    this.evaluationsHistory = new ArrayList<>(evaluations);
    this.fitnessCalculator =
        FitnessCalculator.getFitness(fitness, instanceData, evaluationsHistory);
    this.planGenerator = new PlanGenerator(new Random(seed), instanceData);
    this.name = "Scheduling problem";
    this.objectives = objectives;
    this.defaultArbiter = defaultArbiter;
    this.bounds =
        IntStream.range(0, instanceData.workflow().size() * 2)
            .mapToObj(i -> Bounds.create(0.0, 1.0))
            .toList();
    this.randomKeysCoder = new RandomKeysCoder(instanceData);
  }

  /**
   * Full constructor.
   *
   * @param name The name of the problem.
   * @param workflowFile The file containing the workflow.
   * @param hostsFile The file containing the hosts.
   * @param referenceSpeed The CPU reference speed to calculate the runtime.
   * @param fitness The fitness function to use.
   * @param seed The random seed to use
   */
  public SchedulingDoubleProblem(
      String name,
      File workflowFile,
      File hostsFile,
      String referenceSpeed,
      String fitness,
      Long seed,
      List<Objective> objectives,
      String defaultArbiter,
      int evaluations
      ) {
    this.name = name;
    this.workflowLoader = new WorkflowFileLoader();
    this.hostLoader = new HostFileLoader();
    this.instanceData = loadData(workflowFile, hostsFile, referenceSpeed);
    this.evaluationsHistory = new ArrayList<>(evaluations);
    this.fitnessCalculator =
        FitnessCalculator.getFitness(fitness, instanceData, evaluationsHistory);
    this.planGenerator = new PlanGenerator(new Random(seed), instanceData);
    this.objectives = objectives;
    this.defaultArbiter = defaultArbiter;
    this.bounds =
        IntStream.range(0, instanceData.workflow().size() * 2)
            .mapToObj(i -> Bounds.create(0.0, 1.0))
            .toList();
    this.randomKeysCoder = new RandomKeysCoder(instanceData);
  }

  public SchedulingDoubleProblem(
      String name,
      String fitness,
      Long seed,
      InstanceData instanceData,
      List<Objective> objectives,
      String defaultArbiter,
      int evaluations
      ) {
    this.name = name;
    this.workflowLoader = new WorkflowFileLoader();
    this.hostLoader = new HostFileLoader();
    this.instanceData = instanceData;
    this.evaluationsHistory = new ArrayList<>(evaluations);
    this.fitnessCalculator =
        FitnessCalculator.getFitness(fitness, instanceData, evaluationsHistory);
    this.planGenerator = new PlanGenerator(new Random(seed), instanceData);
    this.objectives = objectives;
    this.defaultArbiter = defaultArbiter;
    this.randomKeysCoder = new RandomKeysCoder(instanceData);
    this.bounds =
        IntStream.range(0, instanceData.workflow().size() * 2)
            .mapToObj(i -> Bounds.create(0.0, 1.0))
            .toList();
  }

  /**
   * Number of variables of the problem.
   *
   * @return The number of variables of the problem.
   */
  @Override
  public int numberOfVariables() {
    return instanceData.workflow().size() * 2;
  }

  /**
   * The number of objectives.
   *
   * @return The number of objectives.
   */
  @Override
  public int numberOfObjectives() {
    return 2;
  }

  /**
   * The number of constrains.
   *
   * @return The number of constrains.
   */
  @Override
  public int numberOfConstraints() {
    return 0;
  }

  /**
   * The name of the problem.
   *
   * @return The name of the problem.
   */
  @Override
  public String name() {
    return this.name;
  }

  @Override
  public DoubleSolution evaluate(DoubleSolution doubleSolution) {

    // We need to create a temporal SchedulePermutationSolution

    var plan = randomKeysCoder.decode(doubleSolution.variables());

    var arbiter = (String) doubleSolution.attributes().get("arbiter");

    var tempSolution =
        new SchedulePermutationSolution(
            doubleSolution.variables().size(), objectives.size(), null, plan, arbiter);

    var fitnessInfo = fitnessCalculator.calculateFitness(tempSolution);

    var orderedPlan =
        fitnessInfo.schedule().stream().map(s -> new PlanPair(s.task(), s.host())).toList();

    var particles = randomKeysCoder.encode(orderedPlan);

    for (int i = 0; i < particles.size(); i++) {
      doubleSolution.variables().set(i, particles.get(i));
    }

    for (int i = 0; i < objectives.size(); i++) {
      doubleSolution.objectives()[i] = fitnessInfo.fitness().get(objectives.get(i).objectiveName);
    }
    return doubleSolution;
  }

  /**
   * Generates a random solution.
   *
   * @return A new solution.
   */
  @Override
  public DoubleSolution createSolution() {
    // var plan = planGenerator.generatePlan();

    var bounds =
        IntStream.range(0, instanceData.workflow().size() * 2)
            .mapToObj(i -> Bounds.create(0.0, 1.0))
            .toList();
    var solution = new DefaultDoubleSolution(bounds, 2, 0);
    solution.attributes().put("arbiter", defaultArbiter);

    return solution;
  }

  private InstanceData loadData(File workflowFile, File hostsFile, String referenceSpeed) {

    var hostsJson = hostLoader.readFromFile(hostsFile);
    var hosts = hostLoader.load(hostsJson);

    var workflow = workflowLoader.load(workflowLoader.readFromFile(workflowFile));

    return new InstanceData(workflow, hosts, UnitParser.parseUnits(referenceSpeed));
  }

  public InstanceData getInstanceData() {
    return instanceData;
  }

  public ArrayList<MultiResult> getEvaluationsHistory() {
    return evaluationsHistory;
  }

  @Override
  public List<Bounds<Double>> variableBounds() {
    return bounds;
  }
}
