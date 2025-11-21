package com.uniovi.sercheduler.jmetal.algorithm;

import com.uniovi.sercheduler.jmetal.evaluation.SequentialEvaluationMulti;
import com.uniovi.sercheduler.jmetal.problem.SchedulePermutationSolution;
import java.util.ArrayList;
import java.util.List;
import org.uma.jmetal.algorithm.multiobjective.ibea.IBEA;
import org.uma.jmetal.component.catalogue.common.evaluation.Evaluation;
import org.uma.jmetal.component.catalogue.common.solutionscreation.SolutionsCreation;
import org.uma.jmetal.operator.crossover.CrossoverOperator;
import org.uma.jmetal.operator.mutation.MutationOperator;
import org.uma.jmetal.operator.selection.SelectionOperator;
import org.uma.jmetal.problem.Problem;

public class IBEACustom extends IBEA<SchedulePermutationSolution> {

  private final SolutionsCreation<SchedulePermutationSolution> createInitialPopulation;

  private Evaluation<SchedulePermutationSolution> evaluation;

  public IBEACustom(
      Problem<SchedulePermutationSolution> problem,
      int populationSize,
      SolutionsCreation<SchedulePermutationSolution> initialPopulationCreation,
      Evaluation<SchedulePermutationSolution> evaluation,
      int archiveSize,
      int maxEvaluations,
      SelectionOperator<List<SchedulePermutationSolution>, SchedulePermutationSolution>
          selectionOperator,
      CrossoverOperator<SchedulePermutationSolution> crossoverOperator,
      MutationOperator<SchedulePermutationSolution> mutationOperator) {

    super(problem, populationSize, archiveSize, maxEvaluations, selectionOperator, crossoverOperator, mutationOperator);

    this.evaluation = evaluation;
    this.createInitialPopulation = initialPopulationCreation;

  }

  @Override
  public void run() {

    List<SchedulePermutationSolution> solutionSet;

    // Initialize the variables
    archive = new ArrayList<>(archiveSize);

    solutionSet = this.createInitialPopulation.create();
    solutionSet = this.evaluation.evaluate(solutionSet);
    int evaluations = solutionSet.size();

    List<SchedulePermutationSolution> offSpringSolutionSet;
    while (evaluations < maxEvaluations) {
      List<SchedulePermutationSolution> union = new ArrayList<>();
      union.addAll(solutionSet);
      union.addAll(archive);
      calculateFitness(union);
      archive = union;

      while (archive.size() > populationSize) {
        removeWorst(archive);
      }
      // Create a new offspringPopulation
      offSpringSolutionSet = new ArrayList<>(populationSize);
      SchedulePermutationSolution parent1;
      SchedulePermutationSolution parent2;
      while (offSpringSolutionSet.size() < populationSize) {
        int j = 0;
        do {
          j++;
          parent1 = selectionOperator.execute(archive);
        } while (j < IBEA.TOURNAMENTS_ROUNDS);
        int k = 0;
        do {
          k++;
          parent2 = selectionOperator.execute(archive);
        } while (k < IBEA.TOURNAMENTS_ROUNDS);

        List<SchedulePermutationSolution> parents = new ArrayList<>(2);
        parents.add(parent1);
        parents.add(parent2);

        // make the crossover
        List<SchedulePermutationSolution> offspring = crossoverOperator.execute(parents);
        mutationOperator.execute(offspring.get(0));

        var evaluated = evaluation.evaluate(List.of(offspring.get(0)));
        offSpringSolutionSet.addAll(evaluated);
        evaluations++;
        if (evaluation instanceof SequentialEvaluationMulti) {
          evaluations++;
        }
      }
      solutionSet = offSpringSolutionSet;
    }
  }
}
