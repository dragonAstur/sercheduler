package com.uniovi.sercheduler.service.core;

import com.uniovi.sercheduler.dto.Host;
import com.uniovi.sercheduler.dto.InstanceData;
import com.uniovi.sercheduler.dto.Task;
import com.uniovi.sercheduler.dto.TaskFile;
import com.uniovi.sercheduler.service.ParentsInfo;
import com.uniovi.sercheduler.service.TaskCosts;
import com.uniovi.sercheduler.service.TaskSchedule;
import com.uniovi.sercheduler.service.support.ScheduleGap;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SchedulingHelper {

  /**
   * Calculates the time it takes to execute a task in each host.
   *
   * <p>The runtime from the workflow comes in second, but we don't know the flops, so we need to
   *
   * <p>calculate them with a simple rule of three.
   *
   * @param referenceFlops The flops of the hardware that executed the workflow the first time.
   * @return A matrix with the time it takes to execute in each host.
   */
  public static Map<String, Map<String, Double>> calculateComputationMatrix(
      InstanceData instanceData, Long referenceFlops) {

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
  public static Map<String, Map<String, Long>> calculateNetworkMatrix(InstanceData instanceData) {

    return instanceData.workflow().values().stream()
        .map(SchedulingHelper::calculateTasksCommns)
        .map(SchedulingHelper::calculateStaging)
        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
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

  /**
   * Calculates the possible cost of a task, with the average of communications and computation
   * time.
   *
   * @param task Task to calculate.
   * @param savedCosts Contains the saved cost of past operations (acts as a cache).
   * @return The cost.
   */
  public static Double calculateTaskCost(
      Task task,
      Map<String, Double> savedCosts,
      Map<String, Map<String, Double>> computationMatrix,
      Double referenceSpeedRead,
      Double referenceSpeedWrite) {
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

    taskCost += task.getInput().getSizeInBits() / referenceSpeedRead;
    taskCost += task.getOutput().getSizeInBits() / referenceSpeedWrite;

    taskCost += maxChild;

    return taskCost;
  }

  /**
   * Calculates the average of the communications speed for the input.
   *
   * @return the average.
   */
  public static Double calculateReferenceSpeedRead(InstanceData instanceData) {
    return instanceData.hosts().values().stream()
        .map(h -> Math.min(h.getNetworkSpeed(), h.getDiskSpeed()))
        .mapToLong(Long::longValue)
        .average()
        .orElseThrow();
  }

  /**
   * Calculates the average of the communications speed for the output.
   *
   * @return the average.
   */
  public static Double calculateReferenceSpeedWrite(InstanceData instanceData) {
    return instanceData.hosts().values().stream()
        .map(Host::getDiskSpeed)
        .mapToLong(Long::longValue)
        .average()
        .orElseThrow();
  }

  /**
   * Calculates the ranking for the HEFT algorithm, the ranking is in DECREASING ORDER, being the
   * tasks with the highest cost the ones that should be executed first.
   *
   * @return The ranking in DECREASING order.
   */
  public static LinkedHashMap<Task, Double> calculateHeftRanking(
      InstanceData instanceData,
      Map<String, Map<String, Double>> computationMatrix,
      Double referenceSpeedRead,
      Double referenceSpeedWrite) {
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
      savedCosts.put(
          taskToExplore.getName(),
          SchedulingHelper.calculateTaskCost(
              taskToExplore,
              savedCosts,
              computationMatrix,
              referenceSpeedRead,
              referenceSpeedWrite));

      // We need to remove the task from the children list of non-calculated parents.
      for (var parent : taskToExplore.getParents()) {
        childrenStatus.get(parent.getName()).remove(taskToExplore.getName());
        if (childrenStatus.get(parent.getName()).isEmpty()) {
          tasksToExplore.add(parent);
        }
      }
    }
    return savedCosts.entrySet().stream()
        .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
        .collect(
            Collectors.toMap(
                t -> instanceData.workflow().get(t.getKey()),
                Map.Entry::getValue,
                (o, n) -> o,
                LinkedHashMap::new));
  }

  /**
   * Calculates the eft of a given task. With insertion, which means that it take into account the
   * gaps.
   *
   * @param task Task to execute.
   * @param host Where does the task run.
   * @param schedule The schedule to update.
   * @param available When each machine is available.
   * @return Information about the executed task.
   */
  public static TaskCosts calculateEftActive(
      Task task,
      Host host,
      Map<String, TaskSchedule> schedule,
      Map<String, List<ScheduleGap>> available,
      Map<String, Map<String, Double>> computationMatrix,
      Map<String, Map<String, Long>> networkMatrix) {
    var parentsInfo = findTaskCommunications(task, host, schedule, networkMatrix);
    var taskCommunications = parentsInfo.taskCommunications();
    Double diskReadStaging =
        networkMatrix.get(task.getName()).get(task.getName()) / host.getDiskSpeed().doubleValue();
    Double diskWrite = task.getOutput().getSizeInBits() / host.getDiskSpeed().doubleValue();

    // We need to find the first available schedule where we can execute the full task and the ast
    // will be after the eft of the parents.

    double taskTime =
        diskReadStaging
            + diskWrite
            + computationMatrix.get(task.getName()).get(host.getName())
            + taskCommunications;

    var availableHostGaps =
        available.getOrDefault(host.getName(), List.of(new ScheduleGap(0D, Double.MAX_VALUE)));

    Double ast = null;
    double maxEst = parentsInfo.maxEst();

    for (ScheduleGap gap : availableHostGaps) {
      if (gap.start() >= maxEst && taskTime <= (gap.end() - gap.start())) {
        if (ast == null || gap.start() < ast) {
          ast = gap.start();
        }
      }
    }

    // Use default value if no matching gap is found
    if (ast == null) {
      ast = parentsInfo.maxEst();
    }

    Double eft = ast + taskTime;

    return new TaskCosts(diskReadStaging, diskWrite, eft, taskCommunications, ast);
  }

  /**
   * Find the time it takes to transfer all information between the task and it's parents.
   *
   * @param task Task to check.
   * @param host The host where it's going to run.
   * @param schedule The schedule to check the parents' info.
   * @return Information about parents.
   */
  public static ParentsInfo findTaskCommunications(
      Task task,
      Host host,
      Map<String, TaskSchedule> schedule,
      Map<String, Map<String, Long>> networkMatrix) {

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
  public static Long findHostSpeed(Host host, Host parentHost) {
    // If the parent and the current host are the same we should return the disk
    // speed

    if (host.getName().equals(parentHost.getName())) {
      return host.getDiskSpeed();
    }

    // we need to find which network is worse
    var bandwidth = Math.min(host.getNetworkSpeed(), parentHost.getNetworkSpeed());

    // We need to do the minimum between bandwidth and parent disk
    return Math.min(bandwidth, parentHost.getDiskSpeed());
  }
}
