package com.uniovi.sercheduler.service.calculator;

import com.uniovi.sercheduler.dto.InstanceData;
import com.uniovi.sercheduler.dto.analysis.MultiResult;
import com.uniovi.sercheduler.jmetal.problem.SchedulePermutationSolution;
import com.uniovi.sercheduler.service.FitnessInfo;
import com.uniovi.sercheduler.service.TaskSchedule;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

/** Implementation for calculating the makespan using DNC model. */
public class FitnessCalculatorSimple extends FitnessCalculator {

  private final ArrayList<MultiResult> evaluationsHistory;


  public FitnessCalculatorSimple(InstanceData instanceData, ArrayList<MultiResult> evaluationsHistory) {
    super(instanceData);
    this.evaluationsHistory = evaluationsHistory;

  }

  public FitnessCalculatorSimple(InstanceData instanceData) {
    super(instanceData);
    this.evaluationsHistory = new ArrayList<>();

  }

  /**
   * Calculates the makespan of a given schedule.
   *
   * @param solution
   * @return The value of the makespan.
   */
  @Override
  public FitnessInfo calculateFitness(SchedulePermutationSolution solution) {
    var plan = solution.getPlan();

    double makespan = 0D;
    double energyActive = 0D;

    var available = new HashMap<String, Double>(instanceData.hosts().size());
    var schedule = new HashMap<String, TaskSchedule>(instanceData.workflow().size());

    for (var schedulePair : plan) {
      var taskName = schedulePair.task().getName();
      var hostName = schedulePair.host().getName();

      var taskCosts =
          calculateEftSemiActive(schedulePair.task(), schedulePair.host(), schedule, available);

      available.put(hostName, taskCosts.eft());

      schedule.put(
          taskName,
          new TaskSchedule(
              schedulePair.task(), taskCosts.ast(), taskCosts.eft(), schedulePair.host()));

      makespan = Math.max(taskCosts.eft(), makespan);

      energyActive += (taskCosts.eft() - taskCosts.ast()) * schedulePair.host().getEnergyCost();
    }

    var orderedSchedule =
        schedule.values().stream().sorted(Comparator.comparing(TaskSchedule::ast)).toList();

    // We need to calculate the standby energy of each host
    double energyStandBy = 0;
    for (var host : instanceData.hosts().values()) {
      energyStandBy += host.getEnergyCostStandBy() * makespan;
    }

    double energy = energyActive + energyStandBy;
//    evaluationsHistory.add(
//            new MultiResult(
//                    makespan,
//                    energy,
//                    fitnessName(),
//                    "any"));

    return new FitnessInfo(
        Map.of("makespan", makespan, "energy", energy), orderedSchedule, fitnessName());
  }

  @Override
  public String fitnessName() {
    return "simple";
  }

  public ArrayList<MultiResult> getEvaluationsHistory() {
    return evaluationsHistory;
  }
}
