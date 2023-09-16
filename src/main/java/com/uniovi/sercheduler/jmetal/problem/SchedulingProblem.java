package com.uniovi.sercheduler.jmetal.problem;

import com.uniovi.sercheduler.dto.InstanceData;
import com.uniovi.sercheduler.parser.HostFileLoader;
import com.uniovi.sercheduler.parser.HostLoader;
import com.uniovi.sercheduler.parser.WorkflowFileLoader;
import com.uniovi.sercheduler.parser.WorkflowLoader;
import com.uniovi.sercheduler.service.FitnessCalculator;
import com.uniovi.sercheduler.service.PlanGenerator;
import com.uniovi.sercheduler.service.PlanPair;
import com.uniovi.sercheduler.util.UnitParser;
import java.io.File;
import java.util.Random;
import org.uma.jmetal.problem.permutationproblem.PermutationProblem;

public class SchedulingProblem implements PermutationProblem<SchedulePermutationSolution> {

  private final WorkflowLoader workflowLoader;
  private final HostLoader hostLoader;
  private final PlanGenerator planGenerator;

  private final FitnessCalculator fitnessCalculator;

  public SchedulingProblem(
      File workflowFile, File hostsFile, String referenceSpeed, String fitness, Long seed) {
    this.workflowLoader = new WorkflowFileLoader();
    this.hostLoader = new HostFileLoader();
    this.instanceData = loadData(workflowFile, hostsFile, referenceSpeed);
    this.fitnessCalculator = FitnessCalculator.getFitness(fitness, instanceData);
    this.planGenerator = new PlanGenerator(new Random(seed), instanceData);
  }

  public SchedulingProblem(
      File workflowFile,
      File hostsFile,
      String referenceSpeed,
      FitnessCalculator fitnessCalculator,
      PlanGenerator planGenerator,
      HostLoader hostLoader,
      WorkflowLoader workflowLoader) {

    this.fitnessCalculator = fitnessCalculator;
    this.planGenerator = planGenerator;
    this.hostLoader = hostLoader;
    this.workflowLoader = workflowLoader;
    this.instanceData = loadData(workflowFile, hostsFile, referenceSpeed);
  }

  InstanceData instanceData;

  /**
   * @return
   */
  @Override
  public int length() {
    return instanceData.workflow().size();
  }

  /**
   * @return
   */
  @Override
  public int numberOfVariables() {
    return 1;
  }

  /**
   * @return
   */
  @Override
  public int numberOfObjectives() {
    return 1;
  }

  /**
   * @return
   */
  @Override
  public int numberOfConstraints() {
    return 0;
  }

  /**
   * @return
   */
  @Override
  public String name() {
    return "Scheduling problem";
  }

  /**
   * @param schedulePermutationSolution
   * @return
   */
  @Override
  public SchedulePermutationSolution evaluate(
      SchedulePermutationSolution schedulePermutationSolution) {

    var fitnessInfo = fitnessCalculator.calculateFitness(schedulePermutationSolution.plan);
    var plan = fitnessInfo.schedule().stream().map(s -> new PlanPair(s.task(), s.host())).toList();

    schedulePermutationSolution.setPlan(plan);
    schedulePermutationSolution.setFitnessInfo(fitnessInfo);

    schedulePermutationSolution.objectives()[0] = fitnessInfo.fitness().get("makespan");

    return schedulePermutationSolution;
  }

  /**
   * @return
   */
  @Override
  public SchedulePermutationSolution createSolution() {
    var plan = planGenerator.generatePlan();
    return new SchedulePermutationSolution(numberOfVariables(), numberOfObjectives(), null, plan);
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
}
