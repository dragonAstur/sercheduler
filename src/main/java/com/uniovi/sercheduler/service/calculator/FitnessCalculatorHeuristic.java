package com.uniovi.sercheduler.service.calculator;

import com.uniovi.sercheduler.dto.InstanceData;
import com.uniovi.sercheduler.dto.Task;
import com.uniovi.sercheduler.jmetal.problem.SchedulePermutationSolution;
import com.uniovi.sercheduler.service.FitnessInfo;
import com.uniovi.sercheduler.service.PlanPair;
import com.uniovi.sercheduler.service.core.SchedulingHelper;

import java.util.ArrayList;
import java.util.List;

/** Finds the fitness using a heft heuristic. */
public class FitnessCalculatorHeuristic extends FitnessCalculatorHeft {

  List<Task> heftRanking;

  public FitnessCalculatorHeuristic(InstanceData instanceData) {
    super(instanceData);
    heftRanking = SchedulingHelper.calculateHeftRanking(instanceData, computationMatrix, referenceSpeedRead, referenceSpeedWrite).keySet().stream().toList();
  }

  /**
   * Calculates the makespan of a given schedule.
   *
   * <p>This is the heuristic version so the plan is completely ignored.
   *
   * @param solution@return The value of the makespan.
   */
  @Override
  public FitnessInfo calculateFitness(SchedulePermutationSolution solution) {
    var plan = solution.getPlan();

    var newPlan = new ArrayList<PlanPair>();

    for (int i = 0; i < plan.size(); i++) {
      newPlan.add(new PlanPair(heftRanking.get(i), plan.get(i).host()));
    }
    solution.setPlan(newPlan);
    return super.calculateFitness(solution);
  }

  @Override
  public String fitnessName(){
    return "heuristic";
  }
}
