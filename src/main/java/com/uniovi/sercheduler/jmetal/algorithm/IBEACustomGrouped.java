package com.uniovi.sercheduler.jmetal.algorithm;

import com.uniovi.sercheduler.jmetal.evaluation.SequentialEvaluationMulti;
import com.uniovi.sercheduler.jmetal.problem.SchedulePermutationSolution;
import java.util.ArrayList;
import java.util.List;
import org.uma.jmetal.algorithm.multiobjective.ibea.IBEA;
import org.uma.jmetal.component.catalogue.common.evaluation.Evaluation;
import org.uma.jmetal.component.catalogue.common.solutionscreation.SolutionsCreation;
import org.uma.jmetal.component.catalogue.ea.selection.Selection;
import org.uma.jmetal.component.catalogue.ea.variation.Variation;
import org.uma.jmetal.problem.Problem;

public class IBEACustomGrouped extends IBEA<SchedulePermutationSolution> {

  private final SolutionsCreation<SchedulePermutationSolution> createInitialPopulation;
  private final Selection<SchedulePermutationSolution> selection;
  private final Variation<SchedulePermutationSolution> variation;
  private Evaluation<SchedulePermutationSolution> evaluation;

  public IBEACustomGrouped(
      Problem<SchedulePermutationSolution> problem,
      int populationSize,
      SolutionsCreation<SchedulePermutationSolution> initialPopulationCreation,
      Evaluation<SchedulePermutationSolution> evaluation,
      int archiveSize,
      int maxEvaluations,
      Selection<SchedulePermutationSolution> selection,
      Variation<SchedulePermutationSolution> variation) {

    super(problem, populationSize, archiveSize, maxEvaluations, null, null, null);

    this.evaluation = evaluation;
    this.createInitialPopulation = initialPopulationCreation;
    this.selection = selection;
    this.variation = variation;
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
      offSpringSolutionSet = this.selection.select(archive);
      offSpringSolutionSet = this.variation.variate(solutionSet, offSpringSolutionSet);
      solutionSet = this.evaluation.evaluate(offSpringSolutionSet);

      int multiplier = 1;
      // The SequentialEvaluationMulti does double the evaluations so it should be registered as
      // such.
      if (evaluation instanceof SequentialEvaluationMulti) {
        multiplier = 2;
      }

      evaluations += this.variation.getOffspringPopulationSize() * multiplier;
    }
  }
}
