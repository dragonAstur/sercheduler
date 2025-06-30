package com.uniovi.sercheduler.jmetal.algorithm;

import com.uniovi.sercheduler.dao.Objective;
import com.uniovi.sercheduler.dto.Task;
import com.uniovi.sercheduler.jmetal.problem.SchedulePermutationSolution;
import com.uniovi.sercheduler.jmetal.problem.SchedulingProblem;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

import com.uniovi.sercheduler.service.PlanPair;
import org.uma.jmetal.algorithm.Algorithm;
import org.uma.jmetal.util.archive.impl.NonDominatedSolutionListArchive;

/**
 * Initial skeleton for the Multi-Objective Ant Colony Optimization (MOACO) algorithm specifically
 * designed for the SchedulingProblem.
 */
public class MOACO implements Algorithm<List<SchedulePermutationSolution>> {

  private final SchedulingProblem problem;
  Random random = new Random();

  // Pheromone matrix: pheromone[i][j] represents pheromone level for assigning task i to host j
  private double[][] pheromone;
  private List<String> taskIds;
  private List<String> hostIds;

  // Parameters (can be tuned later)
  private final double alpha = 1.0; // pheromone importance
  private final double beta = 2.0; // heuristic importance
  private final double evaporationRate = 0.1;
  private final double depositAmount = 10.0;


  private final int maxIterations = 100;
  private final int antsPerIteration = 20;

  private NonDominatedSolutionListArchive<SchedulePermutationSolution> archive;
  private List<SchedulePermutationSolution> solutions;



  /**
   * Constructor for the MOACO algorithm.
   *
   * @param problem An instance of the scheduling problem.
   */
  public MOACO(SchedulingProblem problem, Random random) {
    this.problem = problem;
    this.taskIds = problem.getInstanceData().workflow().keySet().stream().toList();
    this.hostIds = problem.getInstanceData().hosts().keySet().stream().toList();
    initializePheromoneMatrix();
    this.archive = new NonDominatedSolutionListArchive<>();
    this.solutions = new ArrayList<>();
    this.random = random;


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
      double sum = 0.0;
      for (int j = 0; j < hostIds.size(); j++) {
        double pheromoneVal = pheromone[taskIdx][j];
        double heuristicVal = 1.0; // Placeholder: use runtime, cost, etc.
        probabilities[j] = Math.pow(pheromoneVal, alpha) * Math.pow(heuristicVal, beta);
        sum += probabilities[j];
      }

      for (int j = 0; j < probabilities.length; j++) {
        probabilities[j] /= sum;
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

      localPheromoneUpdate(taskIdx, selectedHostIdx);

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

  /** Executes the MOACO algorithm logic. This method will be implemented step by step. */
  @Override
  public void run() {

    for (int iteration = 0; iteration < maxIterations; iteration++) {
      for (int i = 0; i < antsPerIteration; i++) {
        SchedulePermutationSolution solution = constructAntSolution();
        problem.evaluate(solution);
        archive.add(solution);
      }
      // Add global pheromone update based on archive
      globalPheromoneUpdate();
    }
    solutions = archive.solutions();
  }

  private void globalPheromoneUpdate() {
    for (int i = 0; i < pheromone.length; i++) {
      for (int j = 0; j < pheromone[i].length; j++) {
        pheromone[i][j] *= (1 - evaporationRate);
      }
    }

    for (SchedulePermutationSolution solution : archive.solutions()) {
      for (int i = 0; i < solution.getPlan().size(); i++) {
        String taskId = solution.getPlan().get(i).task().getName();
        String hostId = solution.getPlan().get(i).host().getName();
        int taskIdx = getTaskIndex(taskId);
        int hostIdx = getHostIndex(hostId);
        pheromone[taskIdx][hostIdx] += depositAmount;
      }
    }
  }

  private void localPheromoneUpdate(int taskIdx, int hostIdx) {
    pheromone[taskIdx][hostIdx] = (1 - evaporationRate) * pheromone[taskIdx][hostIdx] + evaporationRate * 1.0;
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
    double initialPheromoneValue = 1.0;

    for (int i = 0; i < numberOfTasks; i++) {
      for (int j = 0; j < numberOfHosts; j++) {
        pheromone[i][j] = initialPheromoneValue;
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
