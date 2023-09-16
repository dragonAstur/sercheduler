package com.uniovi.sercheduler.jmetal.operator;

import com.uniovi.sercheduler.jmetal.problem.SchedulePermutationSolution;
import com.uniovi.sercheduler.service.Operators;
import java.util.List;
import org.uma.jmetal.operator.crossover.CrossoverOperator;

/** Crossover for schedules with topology order. */
public class ScheduleCrossover implements CrossoverOperator<SchedulePermutationSolution> {

  private final Operators operators;
  double crossoverProbability;

  public ScheduleCrossover(double crossoverProbability, Operators operators) {
    this.crossoverProbability = crossoverProbability;
    this.operators = operators;
  }

  /**
   * @return
   */
  @Override
  public double crossoverProbability() {
    return crossoverProbability;
  }

  /**
   * @return
   */
  @Override
  public int numberOfRequiredParents() {
    return 2;
  }

  /**
   * @return
   */
  @Override
  public int numberOfGeneratedChildren() {
    return 2;
  }

  /**
   * @param schedulePermutationSolutions
   * @return
   */
  @Override
  public List<SchedulePermutationSolution> execute(
      List<SchedulePermutationSolution> schedulePermutationSolutions) {
    var schedule1 = schedulePermutationSolutions.get(0);

    // TODO: I need to disscuss with Jorge this
    // I need to create two children to have in the selection
    // an equal number of parents and children.

    var newPlan =
        operators.doCrossover(
            schedulePermutationSolutions.get(0).getPlan(),
            schedulePermutationSolutions.get(1).getPlan());

    var newPlan2 =
        operators.doCrossover(
            schedulePermutationSolutions.get(1).getPlan(),
            schedulePermutationSolutions.get(0).getPlan());
    return List.of(
        new SchedulePermutationSolution(
            schedule1.variables().size(), schedule1.objectives().length, null, newPlan),
        new SchedulePermutationSolution(
            schedule1.variables().size(), schedule1.objectives().length, null, newPlan2));
  }
}
