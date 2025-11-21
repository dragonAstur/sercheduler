package com.uniovi.sercheduler.jmetal.algorithm;

import com.uniovi.sercheduler.jmetal.problem.SchedulePermutationSolution;
import java.util.List;
import org.uma.jmetal.algorithm.AlgorithmBuilder;
import org.uma.jmetal.algorithm.multiobjective.ibea.IBEA;
import org.uma.jmetal.component.catalogue.common.evaluation.Evaluation;
import org.uma.jmetal.component.catalogue.common.solutionscreation.SolutionsCreation;
import org.uma.jmetal.component.catalogue.common.solutionscreation.impl.RandomSolutionsCreation;
import org.uma.jmetal.component.catalogue.ea.selection.Selection;
import org.uma.jmetal.component.catalogue.ea.selection.impl.NaryTournamentSelection;
import org.uma.jmetal.component.catalogue.ea.variation.Variation;
import org.uma.jmetal.component.catalogue.ea.variation.impl.CrossoverAndMutationVariation;
import org.uma.jmetal.operator.crossover.CrossoverOperator;
import org.uma.jmetal.operator.mutation.MutationOperator;
import org.uma.jmetal.operator.selection.SelectionOperator;
import org.uma.jmetal.operator.selection.impl.BinaryTournamentSelection;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.util.comparator.dominanceComparator.impl.DominanceWithConstraintsComparator;

/** This class implements the IBEA algorithm */
public class IBEABuilder implements AlgorithmBuilder<IBEA<SchedulePermutationSolution>> {
  private final Problem<SchedulePermutationSolution> problem;
  private final int populationSize;
  private final int archiveSize;
  private final Selection<SchedulePermutationSolution> selection;
  private final Variation<SchedulePermutationSolution> variation;
  private final SolutionsCreation<SchedulePermutationSolution> createInitialPopulation;
  private final SelectionOperator<List<SchedulePermutationSolution>, SchedulePermutationSolution>
      selectionOperator;
  private final CrossoverOperator<SchedulePermutationSolution> crossover;
  private final MutationOperator<SchedulePermutationSolution> mutation;
  private Evaluation<SchedulePermutationSolution> evaluation;
  private int maxEvaluations;
  private boolean isGrouped;
  /**
   * Constructor
   *
   * @param problem The problem to solve.
   */
  public IBEABuilder(
      Problem<SchedulePermutationSolution> problem,
      int populationSize,
      int archiveSize,
      CrossoverOperator<SchedulePermutationSolution> crossover,
      MutationOperator<SchedulePermutationSolution> mutation) {
    this.problem = problem;
    this.populationSize = populationSize;
    this.archiveSize = archiveSize;
    maxEvaluations = 25000;

    this.createInitialPopulation = new RandomSolutionsCreation<>(problem, populationSize);

    this.variation = new CrossoverAndMutationVariation<>(populationSize, crossover, mutation);

    this.selection =
        new NaryTournamentSelection<>(
            2, variation.getMatingPoolSize(), new DominanceWithConstraintsComparator<>());

    this.selectionOperator = new BinaryTournamentSelection<>();
    this.crossover = crossover;
    this.mutation = mutation;
    this.isGrouped = false;
  }

  /* Getters */
  public int getPopulationSize() {
    return populationSize;
  }

  public int getArchiveSize() {
    return archiveSize;
  }

  public int getMaxEvaluations() {
    return maxEvaluations;
  }

  /* Setters */

  public IBEABuilder setMaxEvaluations(int maxEvaluations) {
    this.maxEvaluations = maxEvaluations;

    return this;
  }

  public IBEABuilder setEvaluation(Evaluation<SchedulePermutationSolution> evaluation) {
    this.evaluation = evaluation;

    return this;
  }

  public IBEA<SchedulePermutationSolution> build() {

    if (isGrouped) {
      return new IBEACustomGrouped(
          problem,
          populationSize,
          createInitialPopulation,
          evaluation,
          archiveSize,
          maxEvaluations,
          selection,
          variation);
    } else {

      return new IBEACustom(
          problem,
          populationSize,
          createInitialPopulation,
          evaluation,
          archiveSize,
          maxEvaluations,
          selectionOperator,
          crossover,
          mutation);
    }
  }

  public void setGrouped(boolean grouped) {
    isGrouped = grouped;
  }
}
