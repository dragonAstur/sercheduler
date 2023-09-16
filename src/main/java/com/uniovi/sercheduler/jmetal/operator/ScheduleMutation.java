package com.uniovi.sercheduler.jmetal.operator;

import com.uniovi.sercheduler.jmetal.problem.SchedulePermutationSolution;
import com.uniovi.sercheduler.service.Operators;
import org.uma.jmetal.operator.mutation.MutationOperator;

public class ScheduleMutation implements MutationOperator<SchedulePermutationSolution> {
  double mutationProbability;

  final Operators operators;

  public ScheduleMutation(double mutationProbability, Operators operators) {
    this.mutationProbability = mutationProbability;
    this.operators = operators;
  }

  /**
   * @param schedulePermutationSolution
   * @return
   */
  @Override
  public SchedulePermutationSolution execute(
      SchedulePermutationSolution schedulePermutationSolution) {

    return new SchedulePermutationSolution(
        schedulePermutationSolution.variables().size(),
        schedulePermutationSolution.objectives().length,
        null,
        operators.mutate(schedulePermutationSolution.getPlan()));
  }

  /**
   * @return
   */
  @Override
  public double mutationProbability() {
    return mutationProbability();
  }
}
