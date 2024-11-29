package com.uniovi.sercheduler.service;

import com.uniovi.sercheduler.dto.Host;
import com.uniovi.sercheduler.dto.InstanceData;
import com.uniovi.sercheduler.dto.Task;
import com.uniovi.sercheduler.jmetal.problem.SchedulePermutationSolution;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Implementation for calculating the makespan using DNC model and heft second phase, focused on
 * Energy.
 */
public class FitnessCalculatorMinEnergyUM extends FitnessCalculator {
  public FitnessCalculatorMinEnergyUM(InstanceData instanceData) {
    super(instanceData);
  }

  /**
   * Calculates the makespan of a given schedule.
   *
   * @param solution@return The value of the makespan.
   */
  @Override
  public FitnessInfo calculateFitness(SchedulePermutationSolution solution) {
    var plan = solution.getPlan();

    double makespan = 0D;
    double energy = 0D;

    var available = new HashMap<String, Double>(instanceData.hosts().size());
    var schedule = new HashMap<String, TaskSchedule>(instanceData.workflow().size());

    for (var schedulePair : plan) {

      var eftAndAst = calculateHeftTaskCost(schedulePair.task(), schedule, available, makespan);

      makespan = Math.max(eftAndAst.eft(), makespan);

      energy += (eftAndAst.eft() - eftAndAst.ast()) * schedulePair.host().getEnergyCost();
    }

    var orderedSchedule =
        schedule.values().stream().sorted(Comparator.comparing(TaskSchedule::ast)).toList();

    return new FitnessInfo(
        Map.of("makespan", makespan, "energy", energy), orderedSchedule, fitnessName());
  }

  @Override
  public String fitnessName() {
    return "min-energy-UM";
  }

  private EftAndAst calculateHeftTaskCost(
      Task task,
      HashMap<String, TaskSchedule> schedule,
      HashMap<String, Double> available,
      Double currentMakespan) {
    HashMap<String, EftAndEnergy> tempEftAndEnergy = new HashMap<>();
    HashMap<String, TaskCosts> possibleTaskCosts = new HashMap<>();

    for (var host : instanceData.hosts().values()) {

      var taskCosts = calculateEft(task, host, schedule, available);

      var ast =
          taskCosts.eft()
              - computationMatrix.get(task.getName()).get(host.getName())
              - taskCosts.diskWrite()
              - taskCosts.taskCommunications()
              - taskCosts.diskReadStaging();
      var energy = (taskCosts.eft() - ast) * host.getEnergyCost();
      tempEftAndEnergy.put(host.getName(), new EftAndEnergy(taskCosts.eft(), energy));
      possibleTaskCosts.put(host.getName(), taskCosts);
    }

    // We need to sort the possible solutions and find the one that doesn't modify the makespan and
    // has the less energy consumption. If we have to modify the makespan we will choose the first
    // item in the list which is the one that consumes less and take less.

    var sortedEftAndEnergy =
        tempEftAndEnergy.entrySet().stream()
            .sorted(
                Comparator.comparing((Map.Entry<String, EftAndEnergy> e) -> e.getValue().energy())
                    .thenComparing(e -> e.getValue().eft()))
            .collect(
                Collectors.toMap(
                    Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));

    String selectedHostName = sortedEftAndEnergy.entrySet().stream()
        .filter(e -> e.getValue().eft < currentMakespan)
        .findFirst()
        .orElse(sortedEftAndEnergy.entrySet().iterator().next()).getKey();


    var taskCosts = possibleTaskCosts.get(selectedHostName);
    var host = instanceData.hosts().get(selectedHostName);
    available.put(host.getName(), taskCosts.eft());
    var ast =
        available.get(host.getName())
            - computationMatrix.get(task.getName()).get(host.getName())
            - taskCosts.diskWrite()
            - taskCosts.taskCommunications()
            - taskCosts.diskReadStaging();

    schedule.put(task.getName(), new TaskSchedule(task, ast, taskCosts.eft(), host));
    return new EftAndAst(taskCosts.eft(), ast);
  }

  private record EftAndAst(Double eft, Double ast) {}

  private record EftAndEnergy(Double eft, Double energy) {}
}
