package com.uniovi.sercheduler.service;

import com.uniovi.sercheduler.dto.Host;
import com.uniovi.sercheduler.dto.InstanceData;
import com.uniovi.sercheduler.dto.Task;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/** Implementation for calculating the makespan using DNC model and heft second phase. */
public class FitnessCalculatorHeft extends FitnessCalculator {
  public FitnessCalculatorHeft(InstanceData instanceData) {
    super(instanceData);
  }

  /**
   * Calculates the makespan of a given schedule.
   *
   * @param plan Schedule to calculate.
   * @return The value of the makespan.
   */
  @Override
  public FitnessInfo calculateFitness(
      List<PlanPair> plan) {

    double makespan = 0D;

    var available = new HashMap<String, Double>(instanceData.hosts().size());
    var schedule = new HashMap<String, TaskSchedule>(instanceData.workflow().size());

    for (var schedulePair : plan) {

      Double minEft =
          calculateHeftTaskCost(
              schedulePair.task(), schedule, available);

      makespan = Math.max(minEft, makespan);
    }

    var orderedSchedule =
        schedule.values().stream().sorted(Comparator.comparing(TaskSchedule::ast)).toList();

    return new FitnessInfo(Map.of("makespan", makespan), orderedSchedule, fitnessName());
  }

  @Override
  public String fitnessName() {
    return "heft";
  }

  private Double calculateHeftTaskCost(
      Task task,
      HashMap<String, TaskSchedule> schedule,
      HashMap<String, Double> available) {
    double minEft = Double.MAX_VALUE;
    Optional<Host> selectedHost = Optional.empty();
    Optional<TaskCosts> selectedTaskCosts = Optional.empty();
    for (var host : instanceData.hosts().values()) {

      var taskCosts =
          calculateEft(task, host, schedule, available);
      double tmpEft = minEft;

      minEft = Math.min(minEft, taskCosts.eft());
      if (minEft != tmpEft) {
        selectedHost = Optional.of(host);
        selectedTaskCosts = Optional.of(taskCosts);
      }
    }
    var taskCosts = selectedTaskCosts.orElseThrow();
    var host = selectedHost.orElseThrow();
    available.put(host.getName(), taskCosts.eft());
    var ast =
        available.get(host.getName())
            - computationMatrix.get(task.getName()).get(host.getName())
            - taskCosts.diskWrite()
            - taskCosts.taskCommunications()
            - taskCosts.diskReadStaging();

    schedule.put(task.getName(), new TaskSchedule(task, ast, taskCosts.eft(), host));
    return minEft;
  }
}
