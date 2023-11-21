package com.uniovi.sercheduler.service;

import com.uniovi.sercheduler.dto.InstanceData;
import com.uniovi.sercheduler.dto.Task;
import java.util.ArrayList;
import java.util.List;

/** Implementation for calculating the makespan using DNC model. */
public class FitnessCalculatorRank extends FitnessCalculatorSimple {

  List<Task> heftRanking;

  /**
   * Full constructor.
   *
   * @param instanceData Information related to the instance.
   */
  public FitnessCalculatorRank(InstanceData instanceData) {
    super(instanceData);

    heftRanking = calculateHeftRanking();
  }

  /**
   * Calculates the makespan of a given schedule.
   *
   * @param plan Schedule to calculate.
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

  @Override
  public String fitnessName(){
    return "rank";
  }
}
