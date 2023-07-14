package com.uniovi.sercheduler.service;

import com.uniovi.sercheduler.dto.InstanceData;
import com.uniovi.sercheduler.dto.Task;
import java.util.ArrayList;
import java.util.List;

/** Finds the fitness using a heft heuristic. */
public class FitnessCalculatorHeuristic extends FitnessCalculatorHeft {

  List<Task> heftRanking;

  public FitnessCalculatorHeuristic(InstanceData instanceData) {
    super(instanceData);
    heftRanking = calculateHeftRanking();
  }

  /**
   * Calculates the makespan of a given schedule.
   *
   * <p>This is the heuristic version so the plan is completely ignored.
   *
   * @param plan Schedule to calculate. (Is ignored)
   * @return The value of the makespan.
   */
  @Override
  public FitnessInfo calculateFitness(List<PlanPair> plan) {

    var newPlan = new ArrayList<PlanPair>();

    for (int i = 0; i < plan.size(); i++) {
      newPlan.add(new PlanPair(heftRanking.get(i), plan.get(i).host()));
    }
    return super.calculateFitness(newPlan);
  }
}
