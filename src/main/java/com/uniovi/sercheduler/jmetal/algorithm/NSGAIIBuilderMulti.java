package com.uniovi.sercheduler.jmetal.algorithm;

import com.uniovi.sercheduler.jmetal.problem.SchedulePermutationSolution;
import java.util.Arrays;
import java.util.Comparator;
import org.uma.jmetal.component.catalogue.common.evaluation.Evaluation;
import org.uma.jmetal.component.catalogue.common.evaluation.impl.SequentialEvaluation;
import org.uma.jmetal.component.catalogue.common.solutionscreation.SolutionsCreation;
import org.uma.jmetal.component.catalogue.common.solutionscreation.impl.RandomSolutionsCreation;
import org.uma.jmetal.component.catalogue.common.termination.Termination;
import org.uma.jmetal.component.catalogue.common.termination.impl.TerminationByEvaluations;
import org.uma.jmetal.component.catalogue.ea.replacement.Replacement;
import org.uma.jmetal.component.catalogue.ea.replacement.impl.RankingAndDensityEstimatorReplacement;
import org.uma.jmetal.component.catalogue.ea.selection.Selection;
import org.uma.jmetal.component.catalogue.ea.selection.impl.NaryTournamentSelection;
import org.uma.jmetal.component.catalogue.ea.variation.Variation;
import org.uma.jmetal.component.catalogue.ea.variation.impl.CrossoverAndMutationVariation;
import org.uma.jmetal.operator.crossover.CrossoverOperator;
import org.uma.jmetal.operator.mutation.MutationOperator;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.util.comparator.MultiComparator;
import org.uma.jmetal.util.densityestimator.DensityEstimator;
import org.uma.jmetal.util.densityestimator.impl.CrowdingDistanceDensityEstimator;
import org.uma.jmetal.util.ranking.Ranking;
import org.uma.jmetal.util.ranking.impl.FastNonDominatedSortRanking;

public class NSGAIIBuilderMulti {
  private String name;
  private Ranking<SchedulePermutationSolution> ranking;
  private DensityEstimator<SchedulePermutationSolution> densityEstimator;
  private Evaluation<SchedulePermutationSolution> evaluation;
  private SolutionsCreation<SchedulePermutationSolution> createInitialPopulation;
  private Termination termination;
  private Selection<SchedulePermutationSolution> selection;
  private Variation<SchedulePermutationSolution> variation;
  private Replacement<SchedulePermutationSolution> replacement;

  public NSGAIIBuilderMulti(
      Problem<SchedulePermutationSolution> problem,
      int populationSize,
      int offspringPopulationSize,
      CrossoverOperator<SchedulePermutationSolution> crossover,
      MutationOperator<SchedulePermutationSolution> mutation) {
    name = "NSGAIIMulti";

    densityEstimator = new CrowdingDistanceDensityEstimator<>();
    ranking = new FastNonDominatedSortRanking<>();

    this.createInitialPopulation = new RandomSolutionsCreation<>(problem, populationSize);

    this.replacement =
        new RankingAndDensityEstimatorReplacement<>(
            ranking, densityEstimator, Replacement.RemovalPolicy.ONE_SHOT);

    this.variation =
        new CrossoverAndMutationVariation<>(offspringPopulationSize, crossover, mutation);

    int tournamentSize = 2;
    this.selection =
        new NaryTournamentSelection<>(
            tournamentSize,
            variation.getMatingPoolSize(),
            new MultiComparator<>(
                Arrays.asList(
                    Comparator.comparing(ranking::getRank),
                    Comparator.comparing(densityEstimator::value).reversed())));

    this.termination = new TerminationByEvaluations(25000);

    this.evaluation = new SequentialEvaluation<>(problem);
  }

  public NSGAIIBuilderMulti setTermination(Termination termination) {
    this.termination = termination;

    return this;
  }

  public NSGAIIBuilderMulti setRanking(Ranking<SchedulePermutationSolution> ranking) {
    this.ranking = ranking;
    this.replacement =
        new RankingAndDensityEstimatorReplacement<>(
            ranking, densityEstimator, Replacement.RemovalPolicy.ONE_SHOT);

    return this;
  }

  public NSGAIIBuilderMulti setEvaluation(Evaluation<SchedulePermutationSolution> evaluation) {
    this.evaluation = evaluation;

    return this;
  }

  public NSGAIIBuilderMulti setCreateInitialPopulation(
      SolutionsCreation<SchedulePermutationSolution> solutionsCreation) {
    this.createInitialPopulation = solutionsCreation;

    return this;
  }

  public MultiEvolutionaryAlgorithm build() {
    return new MultiEvolutionaryAlgorithm(
        name, createInitialPopulation, evaluation, termination, selection, variation, replacement);
  }
}
