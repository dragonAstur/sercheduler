package com.uniovi.sercheduler.service;

import com.uniovi.sercheduler.dto.Host;
import com.uniovi.sercheduler.dto.InstanceData;
import com.uniovi.sercheduler.dto.Task;
import com.uniovi.sercheduler.dto.TaskFile;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/** Abstract class for defining the process of calculating a makespan from a solution. */
public abstract class MakespanCalculator {

  InstanceData instanceData;

  /**
   * Full constructor.
   *
   * @param instanceData Infrastructure to use.
   */
  protected MakespanCalculator(InstanceData instanceData) {
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
        .map(MakespanCalculator::calculateTasksCommns)
        .map(MakespanCalculator::calculateStaging)
        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
  }
}
