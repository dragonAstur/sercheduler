package com.uniovi.sercheduler.service.calculator;

import com.uniovi.sercheduler.dto.Host;
import com.uniovi.sercheduler.dto.InstanceData;
import com.uniovi.sercheduler.dto.Task;
import com.uniovi.sercheduler.dto.analysis.MultiResult;
import com.uniovi.sercheduler.jmetal.problem.SchedulePermutationSolution;
import com.uniovi.sercheduler.service.FitnessInfo;
import com.uniovi.sercheduler.service.ParentsInfo;
import com.uniovi.sercheduler.service.TaskCosts;
import com.uniovi.sercheduler.service.TaskSchedule;
import com.uniovi.sercheduler.service.core.SchedulingHelper;
import com.uniovi.sercheduler.service.support.ScheduleGap;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.uniovi.sercheduler.service.core.SchedulingHelper.calculateComputationMatrix;

/** Abstract class for defining the process of calculating a makespan from a solution. */
public abstract class FitnessCalculator {

  InstanceData instanceData;
  Map<String, Map<String, Double>> computationMatrix;
  Map<String, Map<String, Long>> networkMatrix;

  Double referenceSpeedRead;
  Double referenceSpeedWrite;

  /**
   * Full constructor.
   *
   * @param instanceData Infrastructure to use.
   */
  protected FitnessCalculator(InstanceData instanceData) {
    this.instanceData = instanceData;
    this.computationMatrix =
        SchedulingHelper.calculateComputationMatrix(instanceData, instanceData.referenceFlops());
    this.networkMatrix = SchedulingHelper.calculateNetworkMatrix(instanceData);
    this.referenceSpeedWrite = SchedulingHelper.calculateReferenceSpeedWrite(instanceData);
    this.referenceSpeedRead = SchedulingHelper.calculateReferenceSpeedRead(instanceData);
  }

  /**
   * Get the fitness calculator for a specific method.
   *
   * @param fitness The requested fitness.
   * @param instanceData The data related to the problem.
   * @param evaluationsHistory Contains the result of every evaluation done.
   * @return The Fitness calculator.
   */
  public static FitnessCalculator getFitness(
      String fitness, InstanceData instanceData, ArrayList<MultiResult> evaluationsHistory) {
    return switch (fitness) {
      case "simple",
          "simple-mono",
          "simple-makespan",
          "simple-makespan-mono",
          "simple-energy",
          "simple-energy-mono",
          "simple-ibea",
          "simple-spea2",
          "simple-moheft",
          "simple-moaco",
          "simple-smpso" ->
          new FitnessCalculatorSimple(instanceData, evaluationsHistory);
      case "heft", "heft-makespan-mono", "heft-spea2", "heft-ibea", "heft-moaco", "heft-smpso" ->
          new FitnessCalculatorHeft(instanceData);
      case "heft-energy-active", "heft-energy-mono-active" ->
          new FitnessCalculatorHeftEnergy(instanceData, "active");
      case "heft-energy-semi-active", "heft-energy-mono-semi-active" ->
          new FitnessCalculatorHeftEnergy(instanceData, "semi-active");

      case "min-energy-UM-active",
          "min-energy-UM-mono-active",
          "min-energy-UM-active-spea2",
          "min-energy-UM-active-ibea",
          "min-energy-UM-active-moaco","min-energy-UM-active-smpso" ->
          new FitnessCalculatorMinEnergyUM(instanceData, "active");
      case "min-energy-UM-semi-active", "min-energy-UM-mono-semi-active" ->
          new FitnessCalculatorMinEnergyUM(instanceData, "semi-active");

      case "fvlt-me-active",
          "fvlt-me-mono-active",
          "fvlt-me-active-spea2",
          "fvlt-me-active-ibea",
          "fvlt-me-active-moaco","fvlt-me-active-smpso" ->
          new FitnessCalculatorFastVirtualMachineForLargeTasks(instanceData, "active");
      case "fvlt-me-semi-active", "fvlt-me-mono-semi-active" ->
          new FitnessCalculatorFastVirtualMachineForLargeTasks(instanceData, "semi-active");

      case "rank", "rank-makespan", "rank-makespan-mono", "rank-spea2", "rank-ibea", "rank-moaco","rank-smpso" ->
          new FitnessCalculatorRank(instanceData, evaluationsHistory);
      case "multi",
          "multi-double-eval",
          "multi-spea2",
          "multi-ibea",
          "multi-moaco","multi-smpso",
          "multi-pop-50-prob-0.7",
          "multi-pop-50-prob-0.9",
          "multi-pop-50-prob-1.0",
          "multi-pop-100-prob-0.7",
          "multi-pop-100-prob-0.9",
          "multi-pop-100-prob-1.0",
          "multi-pop-150-prob-0.7",
          "multi-pop-150-prob-0.9",
          "multi-pop-150-prob-1.0" ->
          new FitnessCalculatorMulti(
              instanceData,
              List.of(
                  new FitnessCalculatorSimple(
                      instanceData, new ArrayList<>(evaluationsHistory.size())),
                  new FitnessCalculatorHeft(instanceData),
                  new FitnessCalculatorRank(
                      instanceData, new ArrayList<>(evaluationsHistory.size()))),
              List.of(
                  new FitnessCalculatorSimple(
                      instanceData, new ArrayList<>(evaluationsHistory.size())),
                  new FitnessCalculatorMinEnergyUM(instanceData, "active"),
                  new FitnessCalculatorFastVirtualMachineForLargeTasks(instanceData, "active")),
              "none",
              evaluationsHistory);

      case "multi-makespan",
          "multi-makespan-mono",
          "multi-makespan-spea2",
          "multi-makespan-ibea",
          "multi-makespan-moaco","multi-makespan-smpso" ->
          new FitnessCalculatorMulti(
              instanceData,
              List.of(
                  new FitnessCalculatorSimple(
                      instanceData, new ArrayList<>(evaluationsHistory.size())),
                  new FitnessCalculatorHeft(instanceData),
                  new FitnessCalculatorRank(
                      instanceData, new ArrayList<>(evaluationsHistory.size()))),
              Collections.emptyList(),
              "makespan",
              evaluationsHistory);
      case "multi-energy-no-fvlt", "multi-energy-mono-no-fvlt" ->
          new FitnessCalculatorMulti(
              instanceData,
              Collections.emptyList(),
              List.of(
                  new FitnessCalculatorSimple(
                      instanceData, new ArrayList<>(evaluationsHistory.size())),
                  new FitnessCalculatorHeftEnergy(instanceData, "active"),
                  new FitnessCalculatorMinEnergyUM(instanceData, "active")),
              evaluationsHistory);
      case "multi-energy",
          "multi-energy-mono",
          "multi-energy-spea2",
          "multi-energy-ibea",
          "multi-energy-moaco","multi-energy-smpso" ->
          new FitnessCalculatorMulti(
              instanceData,
              Collections.emptyList(),
              List.of(
                  new FitnessCalculatorSimple(
                      instanceData, new ArrayList<>(evaluationsHistory.size())),
                  new FitnessCalculatorMinEnergyUM(instanceData, "active"),
                  new FitnessCalculatorFastVirtualMachineForLargeTasks(instanceData, "active")),
              "energy",
              evaluationsHistory);
      default -> throw new IllegalStateException("Unexpected value: " + fitness);
    };
  }

  public abstract FitnessInfo calculateFitness(SchedulePermutationSolution solution);

  /**
   * Calculates the eft of a given task. Without insertion
   *
   * @param task Task to execute.
   * @param host Where does the task run.
   * @param schedule The schedule to update.
   * @param available When each machine is available.
   * @return Information about the executed task.
   */
  public TaskCosts calculateEftSemiActive(
      Task task, Host host, Map<String, TaskSchedule> schedule, Map<String, Double> available) {
    var parentsInfo = SchedulingHelper.findTaskCommunications(task, host, schedule, networkMatrix);
    var taskCommunications = parentsInfo.taskCommunications();
    Double diskReadStaging =
        networkMatrix.get(task.getName()).get(task.getName()) / host.getDiskSpeed().doubleValue();
    Double diskWrite = task.getOutput().getSizeInBits() / host.getDiskSpeed().doubleValue();
    Double ast = Math.max(available.getOrDefault(host.getName(), 0D), parentsInfo.maxEst());
    Double eft =
        diskReadStaging
            + diskWrite
            + computationMatrix.get(task.getName()).get(host.getName())
            + taskCommunications
            + ast;

    return new TaskCosts(diskReadStaging, diskWrite, eft, taskCommunications, ast);
  }

  /**
   * Provides the name of the fitness used.
   *
   * @return The name of the fitness.
   */
  public abstract String fitnessName();

  public Map<String, Map<String, Double>> getComputationMatrix() {
    return computationMatrix;
  }

  public Map<String, Map<String, Long>> getNetworkMatrix() {
    return networkMatrix;
  }
}
