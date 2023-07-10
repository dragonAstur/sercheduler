package com.uniovi.sercheduler.service;

import com.uniovi.sercheduler.dto.InstanceData;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** Implementation for calculating the makespan using DNC model. */
public class FitnessCalculatorSimple extends FitnessCalculator {
  public FitnessCalculatorSimple(InstanceData instanceData) {
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
      var taskName = schedulePair.task().getName();
      var hostName = schedulePair.host().getName();

      var taskCosts =
          calculateEft(
              schedulePair.task(),
              schedulePair.host(),
              schedule,
              available);

      available.put(hostName, taskCosts.eft());
      var ast =
          available.get(hostName)
              - computationMatrix.get(taskName).get(hostName)
              - taskCosts.diskWrite()
              - taskCosts.taskCommunications()
              - taskCosts.diskReadStaging();

      schedule.put(
          taskName,
          new TaskSchedule(schedulePair.task(), ast, taskCosts.eft(), schedulePair.host()));

      makespan = Math.max(taskCosts.eft(), makespan);
    }

    var orderedSchedule =
        schedule.values().stream().sorted(Comparator.comparing(TaskSchedule::ast)).toList();

    return new FitnessInfo(Map.of("makespan", makespan), orderedSchedule);
  }
}
