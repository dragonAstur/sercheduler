package com.uniovi.sercheduler.jmetal.problem;

import com.uniovi.sercheduler.service.PlanPair;
import org.uma.jmetal.solution.AbstractSolution;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.solution.permutationsolution.PermutationSolution;

public class SchedulePermutationSolution extends AbstractSolution<PlanPair> implements
    PermutationSolution<PlanPair> {
  protected SchedulePermutationSolution(int numberOfVariables, int numberOfObjectives) {
    super(numberOfVariables, numberOfObjectives);
  }

  /**
* 
   * @return
*/
  @Override
  public Solution<PlanPair> copy() {
    return null;
  }

/**
* 
   * @return
*/
  @Override
  public int getLength() {
    return 0;
  }
}
