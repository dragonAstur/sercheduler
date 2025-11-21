package com.uniovi.sercheduler.jmetal.evaluation;

import com.uniovi.sercheduler.jmetal.problem.SchedulePermutationSolution;
import java.util.List;
import java.util.stream.Stream;
import org.uma.jmetal.component.catalogue.common.evaluation.Evaluation;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.util.errorchecking.Check;
import org.uma.jmetal.util.evaluator.SolutionListEvaluator;

public class SequentialEvaluationMulti
    implements Evaluation<SchedulePermutationSolution>,
        SolutionListEvaluator<SchedulePermutationSolution> {

  private final Problem<SchedulePermutationSolution> problem;
  private final int numberOfThreads;
  private final String alternativeArbiter;
  private int computedEvaluations;

  public SequentialEvaluationMulti(
      int numberOfThreads,
      Problem<SchedulePermutationSolution> problem,
      String alternativeArbiter) {
    Check.that(
        numberOfThreads >= 0, "The number of threads is a negative value: " + numberOfThreads);
    Check.notNull(problem);

    if (numberOfThreads == 0) {
      numberOfThreads = Runtime.getRuntime().availableProcessors();
    }
    System.setProperty(
        "java.util.concurrent.ForkJoinPool.common.parallelism", "" + numberOfThreads);

    this.numberOfThreads = numberOfThreads;
    this.problem = problem;
    this.alternativeArbiter = alternativeArbiter;
    computedEvaluations = 0;
  }

  @Override
  public List<SchedulePermutationSolution> evaluate(
      List<SchedulePermutationSolution> solutionList) {
    Check.notNull(solutionList);

    solutionList =
        solutionList.stream()
            .flatMap(
                s -> {
                  var copy = (SchedulePermutationSolution) s.copy();
                  copy.setArbiter(alternativeArbiter);

                  return Stream.of(s, copy);
                })
            .toList();
    solutionList.forEach(problem::evaluate);
    computedEvaluations = solutionList.size();

    return solutionList;
  }

  /**
   * Evaluates all the solutions in a sequential manner.
   *
   * @param list The solutions to evaluate.
   * @param problem The problem to fix.
   * @return The evaluated solutions.
   */
  @Override
  public List<SchedulePermutationSolution> evaluate(
      List<SchedulePermutationSolution> list, Problem<SchedulePermutationSolution> problem) {
    return evaluate(list);
  }

  @Override
  public int computedEvaluations() {
    return computedEvaluations;
  }

  public int numberOfThreads() {
    return numberOfThreads;
  }

  @Override
  public Problem<SchedulePermutationSolution> problem() {
    return problem;
  }

  public String getAlternativeArbiter() {
    return alternativeArbiter;
  }

  /**
   * Manages the shutdown of the evaluation, only useful for parallel. */
  @Override
  public void shutdown() {
    // Does nothing because the evaluator is sequential.
  }
}
