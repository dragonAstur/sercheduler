package com.uniovi.sercheduler.jmetal.operator;

import com.uniovi.sercheduler.jmetal.problem.SchedulePermutationSolution;
import com.uniovi.sercheduler.jmetal.problem.SolutionOrigin;
import com.uniovi.sercheduler.service.Operators;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.uniovi.sercheduler.service.PlanPair;
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
   * Getter for the crossover probability.
   *
   * @return The crossoverProbability.
   */
  @Override
  public double crossoverProbability() {
    return crossoverProbability;
  }

  /**
   * The number of parents required for the crossover.
   *
   * @return The required parents.
   */
  @Override
  public int numberOfRequiredParents() {
    return 2;
  }

  /**
   * The number of children generated via crossover.
   *
   * @return The number of generated children.
   */
  @Override
  public int numberOfGeneratedChildren() {
    return 2;
  }

  /**
   * Executes the crossover.
   *
   * @param schedulePermutationSolutions The list of solutions to cross.
   * @return A list of new solutions.
   */
  @Override
  public List<SchedulePermutationSolution> execute(
          List<SchedulePermutationSolution> schedulePermutationSolutions) {

    var schedule1 = schedulePermutationSolutions.get(0);
    var schedule2 = schedulePermutationSolutions.get(1);

    List<PlanPair> newPlan;
    List<PlanPair> newPlan2;

    //TODO: se emplea un random diferente al de la semilla
    Random random = new Random();

    if (random.nextDouble() < crossoverProbability) {

      newPlan = operators.doCrossover(
              schedule1.getPlan(),
              schedule2.getPlan());

      newPlan2 = operators.doCrossover(
              schedule2.getPlan(),
              schedule1.getPlan());

    } else {

      // No crossover: clone the parents' plans
      newPlan = new ArrayList<>(schedule1.getPlan());
      newPlan2 = new ArrayList<>(schedule2.getPlan());
    }

    return List.of(
            new SchedulePermutationSolution(
                    schedule1.variables().size(),
                    schedule1.objectives().length,
                    null,
                    newPlan,
                    schedule1.getArbiter(),
                    SolutionOrigin.CROSS_MUTATION),

            new SchedulePermutationSolution(
                    schedule2.variables().size(),
                    schedule2.objectives().length,
                    null,
                    newPlan2,
                    schedule2.getArbiter(),
                    SolutionOrigin.CROSS_MUTATION)
    );
  }
}
