package com.uniovi.sercheduler.service.core;

import com.uniovi.sercheduler.dto.InstanceData;
import com.uniovi.sercheduler.dto.Task;
import com.uniovi.sercheduler.service.PlanPair;

import java.util.*;
import java.util.stream.Collectors;

public class RandomKeysCoder {

  private final InstanceData instanceData;
  private final List<String> taskIds;
  private final List<String> hostIds;

  public RandomKeysCoder(InstanceData instanceData) {
    this.instanceData = instanceData;
    this.taskIds = instanceData.workflow().keySet().stream().toList();
    this.hostIds = instanceData.hosts().keySet().stream().toList();
  }

  public List<Double> encode(List<PlanPair> plan) {

    var k = taskIds.size();
    List<Double> particle = new ArrayList<>(Collections.nCopies(2 * k, 0.0));
    for (int i = 0; i < plan.size(); i++) {
      var task = plan.get(i).task();
      var host = plan.get(i).host();

      particle.set(getTaskIndex(task.getName()), (double) i / k);
      particle.set(
          k + getTaskIndex(task.getName()), (double) getHostIndex(host.getName()) / hostIds.size());
    }

    return particle;
  }

  public List<PlanPair> decode(List<Double> particle) {

    var k = taskIds.size();
    List<PlanPair> plan = new ArrayList<>(k);

    PriorityQueue<PrioritizedTask> tasksToExplore =
        new PriorityQueue<>(Comparator.comparingDouble(PrioritizedTask::priority));

    var parentStatus =
        instanceData.workflow().values().stream()
            .map(
                t ->
                    Map.entry(
                        t.getName(),
                        t.getParents().stream().map(Task::getName).collect(Collectors.toList())))
            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

    instanceData.workflow().values().stream()
        .filter(t -> t.getParents().isEmpty())
        .forEach(
            t ->
                tasksToExplore.add(
                    new PrioritizedTask(particle.get(getTaskIndex(t.getName())), t)));

    while (!tasksToExplore.isEmpty()) {

      var task = tasksToExplore.poll().task();

      int hostId =
          (int)
              Math.floor(
                  Math.min(
                      particle.get(k + getTaskIndex(task.getName())) * hostIds.size(),
                      hostIds.size() - 1));

      plan.add(new PlanPair(task, instanceData.hosts().get(hostIds.get(hostId))));

      // We need to remove the task from the parent list of non schedule parents.
      for (var child : task.getChildren()) {
        parentStatus.get(child.getName()).remove(task.getName());
        if (parentStatus.get(child.getName()).isEmpty()) {
          tasksToExplore.add(
              new PrioritizedTask(particle.get(getTaskIndex(child.getName())), child));
        }
      }
    }
    return plan;
  }

  private int getTaskIndex(String taskId) {
    return taskIds.indexOf(taskId);
  }

  private int getHostIndex(String hostId) {
    return hostIds.indexOf(hostId);
  }

  public record PrioritizedTask(double priority, Task task) {}
}
