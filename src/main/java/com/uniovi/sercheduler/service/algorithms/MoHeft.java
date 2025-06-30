package com.uniovi.sercheduler.service.algorithms;

import com.uniovi.sercheduler.dao.Objective;
import com.uniovi.sercheduler.dto.InstanceData;
import com.uniovi.sercheduler.jmetal.problem.SchedulePermutationSolution;
import com.uniovi.sercheduler.service.FitnessInfo;
import com.uniovi.sercheduler.service.PlanPair;
import com.uniovi.sercheduler.service.TaskCosts;
import com.uniovi.sercheduler.service.TaskSchedule;
import com.uniovi.sercheduler.service.core.SchedulingHelper;
import com.uniovi.sercheduler.service.support.ScheduleGap;
import org.uma.jmetal.util.comparator.RankingAndCrowdingDistanceComparator;
import org.uma.jmetal.util.densityestimator.impl.CrowdingDistanceDensityEstimator;
import org.uma.jmetal.util.ranking.impl.FastNonDominatedSortRanking;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MoHeft {
  private final InstanceData instanceData;
  private final Map<String, Map<String, Double>> computationMatrix;
  private final Map<String, Map<String, Long>> networkMatrix;

  private final Double referenceSpeedRead;
  private final Double referenceSpeedWrite;

  public MoHeft(InstanceData instanceData) {
    this.instanceData = instanceData;
    this.computationMatrix =
        SchedulingHelper.calculateComputationMatrix(instanceData, instanceData.referenceFlops());
    this.networkMatrix = SchedulingHelper.calculateNetworkMatrix(instanceData);
    this.referenceSpeedWrite = SchedulingHelper.calculateReferenceSpeedWrite(instanceData);
    this.referenceSpeedRead = SchedulingHelper.calculateReferenceSpeedRead(instanceData);
  }

  public List<SchedulePermutationSolution> calculate(int numberOfSolutions) {
    List<SchedulePermutationSolution> solutions = new ArrayList<>(numberOfSolutions);

    // We need to create the heft ranking

    var ranking =
        SchedulingHelper.calculateHeftRanking(
            instanceData, computationMatrix, referenceSpeedRead, referenceSpeedWrite);

    List<PartialSolution> partialSolutions = new ArrayList<>();

    partialSolutions.add(new PartialSolution());

    for (var task : ranking.keySet()) {

      // We need to calculate the makespan and the energy for each host

      List<PartialSolution> newPartialSolutions = new ArrayList<>(partialSolutions.size());

      for (var partialSolution : partialSolutions) {
        for (var host : instanceData.hosts().values()) {

          var newPartialSolution = partialSolution.copy();
          TaskCosts taskCosts =
              SchedulingHelper.calculateEftActive(
                  task,
                  host,
                  newPartialSolution.schedule,
                  newPartialSolution.available,
                  computationMatrix,
                  networkMatrix);

          var ast =
              taskCosts.eft()
                  - computationMatrix.get(task.getName()).get(host.getName())
                  - taskCosts.diskWrite()
                  - taskCosts.taskCommunications()
                  - taskCosts.diskReadStaging();

          // We are working with an insertion algorithm so we need to work with gaps

          // we need to find the closest gap

          var availableHostGaps =
              newPartialSolution.available.getOrDefault(
                  host.getName(), List.of(new ScheduleGap(0D, Double.MAX_VALUE)));
          var gapToReplace =
              availableHostGaps.stream()
                  .filter(gap -> taskCosts.eft() <= gap.end() && taskCosts.ast() >= gap.start())
                  .findFirst()
                  .orElseThrow();

          // Now we need to split the gap in two, using the eft as the slice, depending of the cut
          // we can
          // have one or two gaps. We always generate two gaps so we need to remove the gaps where
          // the
          // start and the end are the same.

          var newGaps =
              Stream.of(
                      new ScheduleGap(gapToReplace.start(), taskCosts.ast()),
                      new ScheduleGap(taskCosts.eft(), gapToReplace.end()))
                  .filter(gap -> !gap.start().equals(gap.end()))
                  .toList();

          // Now we generate a new list with the old gaps and the new ones, removing the used gap.
          var newHostGaps =
              Stream.concat(
                      availableHostGaps.stream().filter(gap -> !gap.equals(gapToReplace)),
                      newGaps.stream())
                  .toList();

          // We need to put the available gaps
          newPartialSolution.available.put(host.getName(), newHostGaps);

          newPartialSolution.schedule.put(
              task.getName(), new TaskSchedule(task, taskCosts.ast(), taskCosts.eft(), host));

          double makespan = Math.max(taskCosts.eft(), partialSolution.currentMakespan);
          double activeEnergy = (taskCosts.eft() - ast) * host.getEnergyCost();
          double standbyEnergy = 0;
          for (var h : instanceData.hosts().values()) {
            standbyEnergy += h.getEnergyCostStandBy() * makespan;
          }

          // We need to update the partial solution with the new plan, makespan and energy.
          newPartialSolution.plan.add(new PlanPair(task, host));
          newPartialSolution.currentMakespan = makespan;
          newPartialSolution.currentActiveEnergy += activeEnergy;
          newPartialSolution.currentStandbyEnergy = standbyEnergy;

          newPartialSolutions.add(newPartialSolution);
        }
      }

      partialSolutions = crowdingDistance(newPartialSolutions, numberOfSolutions);

      // We need to update the partialSolutions to use the pruned solutions

    }

    solutions = partialSolutions.stream().map(PartialSolution::toSolution).toList();

    return solutions;
  }

  private List<PartialSolution> crowdingDistance(
      List<PartialSolution> partialSolutions, int numberOfSolutions) {
    // First, we need to create a solution using JMetal class to be able to use the JMetal
    // CrowdingDistance

    for (var partialSolution : partialSolutions) {
      partialSolution.currentSolution = partialSolution.toSolution();
    }

    var solutions =
        new ArrayList<>(
            partialSolutions.stream().map(PartialSolution::getCurrentSolution).toList());

    // Get non-dominated solutions
    var ranking = new FastNonDominatedSortRanking<SchedulePermutationSolution>();
    ranking.compute(solutions);
    var nonDominated = new ArrayList<>(ranking.getSubFront(0));

    var estimator = new CrowdingDistanceDensityEstimator<SchedulePermutationSolution>();

    estimator.compute(nonDominated);

    // now we need to get only the partialSolutions that are non-dominated

    var filtered =
        partialSolutions.stream()
            .filter(
                ps ->
                    ps.currentSolution
                        .attributes()
                        .containsKey(CrowdingDistanceDensityEstimator.class.getName())).toList();

    // Returns the K best solutions
    return filtered.stream()
            .sorted(Comparator.comparingDouble(ps ->
                    -((Double) ps.currentSolution
                            .attributes()
                            .get(CrowdingDistanceDensityEstimator.class.getName()))))
            .limit(numberOfSolutions)
            .toList();
  }

  private class PartialSolution {
    private List<PlanPair> plan = new ArrayList<>();
    private Map<String, TaskSchedule> schedule = new HashMap<>();
    private Map<String, List<ScheduleGap>> available = new HashMap<>();
    private double currentMakespan = 0;
    private double currentActiveEnergy = 0;
    private double currentStandbyEnergy = 0;
    private SchedulePermutationSolution currentSolution;

    public PartialSolution() {
      super();
    }

    public PartialSolution(
        List<PlanPair> plan,
        Map<String, TaskSchedule> schedule,
        Map<String, List<ScheduleGap>> available,
        double currentMakespan,
        double currentActiveEnergy,
        double currentStandbyEnergy) {
      this.plan = plan;
      this.schedule = schedule;
      this.available = available;
      this.currentMakespan = currentMakespan;
      this.currentActiveEnergy = currentActiveEnergy;
      this.currentStandbyEnergy = currentStandbyEnergy;
    }

    public PartialSolution copy() {

      return new PartialSolution(
          new ArrayList<>(plan),
          new HashMap<>(schedule),
          new HashMap<>(available),
          currentMakespan,
          currentActiveEnergy,
          currentStandbyEnergy);
    }

    public SchedulePermutationSolution toSolution() {

      var fitness =
          Map.of(
              Objective.ENERGY.objectiveName,
              currentActiveEnergy + currentStandbyEnergy,
              Objective.MAKESPAN.objectiveName,
              currentMakespan);
      var orderedSchedule =
          schedule.values().stream().sorted(Comparator.comparing(TaskSchedule::ast)).toList();
      var solution =
          new SchedulePermutationSolution(
              1,
              2,
              new FitnessInfo(fitness, orderedSchedule, "MOHEFT"),
              plan,
              Objective.ENERGY.objectiveName);

      var objectives = List.of(Objective.ENERGY, Objective.MAKESPAN);
      for (int i = 0; i < objectives.size(); i++) {
        solution.objectives()[i] = fitness.get(objectives.get(i).objectiveName);
      }

      return solution;
    }

    public Map<String, TaskSchedule> getSchedule() {
      return schedule;
    }

    public Map<String, List<ScheduleGap>> getAvailable() {
      return available;
    }

    public SchedulePermutationSolution getCurrentSolution() {
      return currentSolution;
    }
  }
}
