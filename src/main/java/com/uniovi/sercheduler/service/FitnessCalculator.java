package com.uniovi.sercheduler.service;

import com.uniovi.sercheduler.dto.Host;
import com.uniovi.sercheduler.dto.InstanceData;
import com.uniovi.sercheduler.dto.Task;
import com.uniovi.sercheduler.dto.TaskFile;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/** Abstract class for defining the process of calculating a makespan from a solution. */
public abstract class FitnessCalculator {

  InstanceData instanceData;
  Map<String, Map<String, Double>> computationMatrix;
  Map<String, Map<String, Long>> networkMatrix;

  Double referenceSpeed;

  /**
   * Full constructor.
   *
   * @param instanceData Infrastructure to use.
   */
  protected FitnessCalculator(InstanceData instanceData) {
    this.instanceData = instanceData;
    this.computationMatrix = calculateComputationMatrix(instanceData.referenceFlops());
    this.networkMatrix = calculateNetworkMatrix();
    this.referenceSpeed = calculateReferenceSpeed();
  }

  private static Map.Entry<String, Map<String, Long>> calculateStaging(
      Map.Entry<Task, Map<String, Long>> entry) {
    // Do the staging
    Task task = entry.getKey();
    Map<String, Long> comms = entry.getValue();
    Long tasksBits = comms.values().stream().reduce(0L, Long::sum);
    var newComms =
        Stream.concat(
                comms.entrySet().stream(),
                Stream.of(Map.entry(task.getName(), task.getInput().getSizeInBits() - tasksBits)))
            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    return Map.entry(task.getName(), newComms);
  }

  private static Map.Entry<Task, Map<String, Long>> calculateTasksCommns(Task task) {
    return Map.entry(
        task,
        task.getParents().stream()
            .map(
                parent -> {
                  Long bitsTransferred =
                      parent.getOutput().getFiles().stream()
                          .filter(
                              f ->
                                  task.getInput().getFiles().stream()
                                      .map(TaskFile::getName)
                                      .toList()
                                      .contains(f.getName()))
                          .map(TaskFile::getSize)
                          .reduce(0L, Long::sum);

                  return Map.entry(parent.getName(), bitsTransferred);
                })
            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)));
  }

  /**
   * Calculates the time it takes to execute a task in each host.
   *
   * <p>The runtime from the workflow comes in second, but we don't know the flops, so we need to
   * calculate them with a simple rule of three.
   *
   * @param referenceFlops The flops of the hardware that executed the workflow the first time.
   * @return A matrix with the time it takes to execute in each host.
   */
  public Map<String, Map<String, Double>> calculateComputationMatrix(Long referenceFlops) {

    return instanceData.workflow().values().stream()
        .map(
            task ->
                Map.entry(
                    task.getName(),
                    instanceData.hosts().values().stream()
                        .map(
                            host ->
                                Map.entry(
                                    host.getName(),
                                    task.getRuntime()
                                        * (referenceFlops / host.getFlops().doubleValue())))
                        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue))))
        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
  }

  /**
   * Calculates the communications between tasks.
   *
   * @return A map stating the input form each task.
   */
  public Map<String, Map<String, Long>> calculateNetworkMatrix() {

    return instanceData.workflow().values().stream()
        .map(FitnessCalculator::calculateTasksCommns)
        .map(FitnessCalculator::calculateStaging)
        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
  }

  /**
   * Calculates the ranking for the HEFT algorithm, the ranking is in DECREASING ORDER, being the
   * tasks with the highest cost the ones that should be executed first.
   *
   * @return The ranking in DECREASING order.
   */
  public List<Task> calculateHeftRanking() {
    Map<String, Double> savedCosts = new HashMap<>();

    var childrenStatus =
        instanceData.workflow().values().stream()
            .map(
                t ->
                    Map.entry(
                        t.getName(),
                        t.getChildren().stream().map(Task::getName).collect(Collectors.toList())))
            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

    var tasksToExplore =
        instanceData.workflow().values().stream()
            .filter(t -> t.getChildren().isEmpty())
            .collect(Collectors.toList());

    for (int i = 0; i < instanceData.workflow().size(); i++) {
      var taskToExplore = tasksToExplore.get(i);
      savedCosts.put(taskToExplore.getName(), calculateTaskCost(taskToExplore, savedCosts));

      // We need to remove the task from the children list of non-calculated parents.
      for (var parent : taskToExplore.getParents()) {
        childrenStatus.get(parent.getName()).remove(taskToExplore.getName());
        if (childrenStatus.get(parent.getName()).isEmpty()) {
          tasksToExplore.add(parent);
        }
      }
    }
    // Creates the rank
    return savedCosts.entrySet().stream()
        .sorted(Comparator.comparing(Map.Entry::getValue, Comparator.reverseOrder()))
        .map(p -> instanceData.workflow().get(p.getKey()))
        .toList();
  }

  /**
   * Calculates the possible cost of a task, with the average of communications and computation
   * time.
   *
   * @param task Task to calculate.
   * @param savedCosts Contains the saved cost of past operations (acts as a cache).
   * @return The cost.
   */
  public Double calculateTaskCost(Task task, Map<String, Double> savedCosts) {
    if (savedCosts.get(task.getName()) != null) {
      return savedCosts.get(task.getName());
    }

    var taskCost =
        computationMatrix.get(task.getName()).values().stream()
            .mapToDouble(Double::doubleValue)
            .average()
            .orElseThrow();

    var maxChild =
        task.getChildren().stream()
            .map(Task::getName)
            .map(savedCosts::get)
            .mapToDouble(Double::doubleValue)
            .max()
            .orElse(0D);

    taskCost += task.getInput().getSizeInBits() / referenceSpeed;
    taskCost += task.getOutput().getSizeInBits() / referenceSpeed;

    taskCost += maxChild;

    return taskCost;
  }

  /**
   * Calculates the average of the communications speed.
   *
   * @return the average.
   */
  public Double calculateReferenceSpeed() {
    return instanceData.hosts().values().stream()
        .map(h -> Math.min(h.getNetworkSpeed(), h.getDiskSpeed()))
        .mapToLong(Long::longValue)
        .average()
        .orElseThrow();
  }

  public abstract FitnessInfo calculateFitness(List<PlanPair> plan);

  /**
   * Calculates the eft of a given task.
   *
   * @param task Task to execute.
   * @param host Where does the task run.
   * @param schedule The schedule to update.
   * @param available When each machine is available.
   * @return Information about the executed task.
   */
  public TaskCosts calculateEft(
      Task task, Host host, Map<String, TaskSchedule> schedule, Map<String, Double> available) {
    var parentsInfo = findTaskCommunications(task, host, schedule);
    var taskCommunications = parentsInfo.taskCommunications();
    Double diskReadStaging =
        networkMatrix.get(task.getName()).get(task.getName()) / host.getDiskSpeed().doubleValue();
    Double diskWrite = task.getOutput().getSizeInBits() / host.getDiskSpeed().doubleValue();

    Double eft =
        diskReadStaging
            + diskWrite
            + computationMatrix.get(task.getName()).get(host.getName())
            + Math.max(available.getOrDefault(host.getName(), 0D), parentsInfo.maxEst())
            + taskCommunications;

    return new TaskCosts(diskReadStaging, diskWrite, eft, taskCommunications);
  }

  /**
   * Find the time it takes to transfer all information between the task and it's parents.
   *
   * @param task Task to check.
   * @param host The host where it's going to run.
   * @param schedule The schedule to check the parents' info.
   * @return Information about parents.
   */
  public ParentsInfo findTaskCommunications(
      Task task, Host host, Map<String, TaskSchedule> schedule) {

    double taskCommunications = 0D;
    double maxEst = 0D;
    for (var parent : task.getParents()) {
      var parentHost = schedule.get(parent.getName()).host();

      var slowestSpeed = findHostSpeed(host, parentHost);

      taskCommunications +=
          networkMatrix.get(task.getName()).get(parent.getName()) / slowestSpeed.doubleValue();
      maxEst = Math.max(maxEst, schedule.get(parent.getName()).eft());
    }

    return new ParentsInfo(maxEst, taskCommunications);
  }

  /**
   * Finds the transfer speed between two hosts. Normally is going to be the slowest one from all
   * mediums.
   *
   * @param host Target host.
   * @param parentHost Source Host.
   * @return The speed in bits per second.
   */
  public Long findHostSpeed(Host host, Host parentHost) {
    // If the parent and the current host are the same we should return the disk
    // speed
    var hostName = host.getName();
    var parentHostName = parentHost.getName();
    if (hostName != parentHostName) {
      return host.getDiskSpeed();
    }

    // we need to find which network is worse
    var bandwidth = Math.min(host.getNetworkSpeed(), parentHost.getNetworkSpeed());

    // We need to do the minimum between bandwidth and parent disk
    return Math.min(bandwidth, parentHost.getDiskSpeed());
  }

  /**
   * Get the fitness calculator for a specific method.
   *
   * @param fitness The requested fitness.
   * @param instanceData The data related to the problem.
   * @return The Fitness calculator.
   */
  public static FitnessCalculator getFitness(String fitness, InstanceData instanceData) {
    return switch (fitness) {
      case "simple" -> new FitnessCalculatorSimple(instanceData);
      case "heft" -> new FitnessCalculatorHeft(instanceData);
      case "rank" -> new FitnessCalculatorRank(instanceData);
      case "multi" -> new FitnessCalculatorMulti(instanceData);
      default -> throw new IllegalStateException("Unexpected value: " + fitness);
    };
  }


  /**
   * Provides the name of the fitness used.
   * @return The name of the fitness.
   */
  public abstract String fitnessName();
}
