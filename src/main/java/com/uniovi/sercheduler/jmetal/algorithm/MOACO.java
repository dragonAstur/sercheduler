package com.uniovi.sercheduler.jmetal.algorithm;

import com.uniovi.sercheduler.dao.Objective;
import com.uniovi.sercheduler.dto.Host;
import com.uniovi.sercheduler.dto.Task;
import com.uniovi.sercheduler.jmetal.evaluation.SequentialEvaluationMulti;
import com.uniovi.sercheduler.jmetal.problem.SchedulePermutationSolution;
import com.uniovi.sercheduler.jmetal.problem.SchedulingProblem;

import java.util.*;
import java.util.stream.Collectors;

import com.uniovi.sercheduler.service.PlanPair;
import com.uniovi.sercheduler.service.TaskSchedule;
import com.uniovi.sercheduler.service.core.SchedulingHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uma.jmetal.algorithm.Algorithm;
import org.uma.jmetal.component.catalogue.common.evaluation.Evaluation;
import org.uma.jmetal.util.archive.impl.CrowdingDistanceArchive;

/**
 * Initial skeleton for the Multi-Objective Ant Colony Optimization (MOACO) algorithm specifically
 * designed for the SchedulingProblem.
 */
public class MOACO implements Algorithm<List<SchedulePermutationSolution>> {

  private static final Logger logger = LoggerFactory.getLogger(MOACO.class);
  private final SchedulingProblem problem;
  Random random = new Random();

  // Pheromone matrix: pheromone[i][j] represents pheromone level for assigning task i to host j
  private double[][] pheromone;
  private List<String> taskIds;
  private List<String> hostIds;

  // Parameters (can be tuned later)
  private final double alpha; // Pheromone importance
  private final double beta; // Heuristic importance
  private final double lambda; // Global update contribution
  private final double rho; // Local pheromone volatility

  private final int maxIterations;
  private final int antsPerIteration;

  private final double initialPheromone;



  private final Map<String, Map<String, Double>> computationMatrix;

  private final CrowdingDistanceArchive<SchedulePermutationSolution> archive;
  private List<SchedulePermutationSolution> solutions;

  private static final int ARCHIVE_SIZE = 100;

  private Evaluation<SchedulePermutationSolution> evaluation;


  /**
   * Constructor for the MOACO algorithm.
   *
   * @param problem An instance of the scheduling problem.
   */
  public MOACO(
      SchedulingProblem problem,
      Random random,
      Evaluation<SchedulePermutationSolution> evaluation,
      MoAcoParameters parameters) {
    this.problem = problem;
    this.taskIds = problem.getInstanceData().workflow().keySet().stream().toList();
    this.hostIds = problem.getInstanceData().hosts().keySet().stream().toList();
    this.archive = new CrowdingDistanceArchive<>(ARCHIVE_SIZE);
    this.solutions = new ArrayList<>();
    this.random = random;

    this.evaluation = evaluation;
    this.computationMatrix =
        SchedulingHelper.calculateComputationMatrix(
            problem.getInstanceData(), problem.getInstanceData().referenceFlops());

    // Parameters initialization
    this.alpha = parameters.alpha();
    this.beta = parameters.beta();
    this.lambda = parameters.lambda();
    this.rho = parameters.rho();
    this.maxIterations = parameters.iterations();
    this.antsPerIteration = parameters.numberOfAnts();
    this.initialPheromone = (double) antsPerIteration / taskIds.size();
    initializePheromoneMatrix();


  }

  /** Constructs a schedule solution probabilistically using pheromone and heuristic information. */
  private SchedulePermutationSolution constructAntSolution() {
    List<PlanPair> plan = new ArrayList<>();

    Map<String, Task> workflow = problem.getInstanceData().workflow();
    Map<String, List<String>> parentStatus =
        workflow.values().stream()
            .map(
                t ->
                    Map.entry(
                        t.getName(),
                        t.getParents().stream().map(Task::getName).collect(Collectors.toList())))
            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

    Map<String, Task> tasksToExplore =
        workflow.values().stream()
            .filter(t -> t.getParents().isEmpty())
            .collect(Collectors.toMap(Task::getName, t -> t));

    while (!tasksToExplore.isEmpty()) {
      List<Task> availableTasks = new ArrayList<>(tasksToExplore.values());
      Task task = availableTasks.get(random.nextInt(availableTasks.size()));
      int taskIdx = taskIds.indexOf(task.getName());

      // Compute selection probabilities for hosts
      double[] probabilities = new double[hostIds.size()];
      double denominator = 0.0;

      for (int j = 0; j < hostIds.size(); j++) {
        double pheromoneVal = pheromone[taskIdx][j];
        String hostId = hostIds.get(j);
        double execTime = estimateExecutionTime(task, hostId);
        double energy = estimateEnergy(task, hostId);
        double numerator =
            Math.pow(pheromoneVal, alpha)
                * Math.pow(execTime, beta / 2.0)
                * Math.pow(energy, beta / 2.0);
        probabilities[j] = numerator;
        denominator += numerator;
      }

      for (int j = 0; j < probabilities.length; j++) {
        probabilities[j] /= denominator;
      }

      double r = random.nextDouble();
      double cumulative = 0.0;
      int selectedHostIdx = 0;
      for (int j = 0; j < probabilities.length; j++) {
        cumulative += probabilities[j];
        if (r <= cumulative) {
          selectedHostIdx = j;
          break;
        }
      }

      String selectedHost = hostIds.get(selectedHostIdx);
      plan.add(new PlanPair(task, problem.getInstanceData().hosts().get(selectedHost)));

      tasksToExplore.remove(task.getName());
      for (Task child : task.getChildren()) {
        List<String> parents = parentStatus.get(child.getName());
        parents.remove(task.getName());
        if (parents.isEmpty()) {
          tasksToExplore.put(child.getName(), child);
        }
      }
    }

    return new SchedulePermutationSolution(
        plan.size(), problem.numberOfObjectives(), null, plan, Objective.ENERGY.objectiveName);
  }

  private double estimateExecutionTime(Task task, String hostId) {
    Host host = problem.getInstanceData().hosts().get(hostId);
    var computationTime = computationMatrix.get(task.getName()).get(hostId);
    return computationTime + ((double) task.getOutput().getSizeInBits() / host.getDiskSpeed());
  }

  private double estimateEnergy(Task task, String hostId) {
    Host host = problem.getInstanceData().hosts().get(hostId);
    double execTime = estimateExecutionTime(task, hostId);
    return execTime * host.getEnergyCost();
  }

  /** Executes the MOACO algorithm logic. This method will be implemented step by step. */
  @Override
  public void run() {

    for (int iteration = 0; iteration < maxIterations; iteration++) {
      List<SchedulePermutationSolution> currentAnts = new ArrayList<>();

      for (int i = 0; i < antsPerIteration; i++) {
        SchedulePermutationSolution solution = constructAntSolution();
        var solutions = evaluation.evaluate(List.of(solution));

        // update the local pheromone
        solutions.forEach(this::localPheromoneUpdate);

        solutions.forEach(archive::add);

        currentAnts.addAll(solutions); // For update
        // logSolution(iteration, i, solution);
        if (evaluation instanceof SequentialEvaluationMulti) {
          // We need to update the counter of ants if we are using the MOCMF
          i++;
        }
      }
      // Add global pheromone update based on current ants
      globalPheromoneUpdate(currentAnts);
    }
    solutions = archive.solutions();
  }

  private void globalPheromoneUpdate(List<SchedulePermutationSolution> currentAnts) {
    // Step 1: Evaporation (keep this)
    for (int i = 0; i < pheromone.length; i++) {
      for (int j = 0; j < pheromone[i].length; j++) {
        pheromone[i][j] *= (1 - rho);
      }
    }

    // Step 2: Deposit using only the best ant from this generation
    // SchedulePermutationSolution best = getBestSolutionByMakespan(currentAnts);

    for (var solution : archive.solutions()) {

      for (TaskSchedule assignment : solution.getFitnessInfo().schedule()) {
        int taskIdx = getTaskIndex(assignment.task().getName());
        int hostIdx = getHostIndex(assignment.host().getName());

        double time = assignment.eft();
        double energy = assignment.eft() * assignment.host().getEnergyCost();

        pheromone[taskIdx][hostIdx] += lambda / (time + energy);

      }
    }
  }

  private void localPheromoneUpdate(SchedulePermutationSolution solution) {
    for (var pair : solution.getPlan()) {
      int taskIdx = getTaskIndex(pair.task().getName());
      int hostIdx = getHostIndex(pair.host().getName());
      localPheromoneUpdate(taskIdx, hostIdx);
    }
  }

  private void localPheromoneUpdate(int taskIdx, int hostIdx) {
    pheromone[taskIdx][hostIdx] = (1 - rho) * pheromone[taskIdx][hostIdx];
  }

  private SchedulePermutationSolution getBestSolutionByMakespan(
      List<SchedulePermutationSolution> solutions) {
    return solutions.stream()
        .min(Comparator.comparingDouble(s -> s.getFitnessInfo().fitness().get("makespan")))
        .orElseThrow();
  }

  private void logSolution(int iteration, int antId, SchedulePermutationSolution solution) {
    double makespan = solution.getFitnessInfo().fitness().get("makespan");
    double energy = solution.getFitnessInfo().fitness().get("energy");

    logger.info(
        "Iteration {} | Ant {} => makespan: {}, energy: {}", iteration, antId, makespan, energy);
  }

  /**
   * Returns the final population of non-dominated solutions.
   *
   * @return List of solutions
   */
  @Override
  public List<SchedulePermutationSolution> result() {
    return solutions;
  }

  @Override
  public String name() {
    return "MOACO";
  }

  @Override
  public String description() {
    return "Multi-Objective Ant Colony Optimization for Workflow Scheduling";
  }

  /** Initializes the pheromone matrix with default values. */
  private void initializePheromoneMatrix() {
    int numberOfTasks = taskIds.size();
    int numberOfHosts = hostIds.size();
    pheromone = new double[numberOfTasks][numberOfHosts];


    for (int i = 0; i < numberOfTasks; i++) {
      for (int j = 0; j < numberOfHosts; j++) {
        pheromone[i][j] = initialPheromone;
      }
    }
  }

  // Optionally: add helper methods to get indices from task/host names if needed
  private int getTaskIndex(String taskId) {
    return taskIds.indexOf(taskId);
  }

  private int getHostIndex(String hostId) {
    return hostIds.indexOf(hostId);
  }
}
