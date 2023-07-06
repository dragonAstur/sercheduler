package com.uniovi.sercheduler.service;

import com.uniovi.sercheduler.dto.Host;
import com.uniovi.sercheduler.dto.InstanceData;
import com.uniovi.sercheduler.dto.Task;
import com.uniovi.sercheduler.dto.TaskFile;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/** Abstract class for defining the process of calculating a makespan from a solution. */
public abstract class FitnessCalculator {

  InstanceData instanceData;

  /**
   * Full constructor.
   *
   * @param instanceData Infrastructure to use.
   */
  protected FitnessCalculator(InstanceData instanceData) {
    this.instanceData = instanceData;
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

  public abstract FitnessInfo calculateFitness(
      List<PlanPair> plan,
      Map<String, Map<String, Double>> computationMatrix,
      Map<String, Map<String, Long>> networkMatrix);

  /**
   * Calculates the eft of a given task.
   *
   * @param task Task to execute.
   * @param host Where does the task run.
   * @param computationMatrix How much time it takes in each host.
   * @param networkMatrix The bits to transfer from each task.
   * @param schedule The schedule to update.
   * @param available When each machine is available.
   * @return Information about the executed task.
   */
  public TaskCosts calculateEft(
      Task task,
      Host host,
      Map<String, Map<String, Double>> computationMatrix,
      Map<String, Map<String, Long>> networkMatrix,
      Map<String, TaskSchedule> schedule,
      Map<String, Double> available) {
    var parentsInfo = findTaskCommunications(task, host, schedule, networkMatrix);
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
   * @param networkMatrix Bits to transfer between tasks.
   * @return Information about parents.
   */
  public ParentsInfo findTaskCommunications(
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
  public Long findHostSpeed(Host host, Host parentHost) {
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
