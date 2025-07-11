package com.uniovi.sercheduler.jmetal.problem;

import com.uniovi.sercheduler.dto.analysis.MultiResult;
import com.uniovi.sercheduler.service.FitnessInfo;
import com.uniovi.sercheduler.service.PlanPair;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

import org.uma.jmetal.solution.AbstractSolution;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.solution.doublesolution.impl.DefaultDoubleSolution;
import org.uma.jmetal.solution.permutationsolution.PermutationSolution;
import org.uma.jmetal.util.bounds.Bounds;

/** Defines a schedule solution. */
public class ScheduleDoubleSolution extends AbstractSolution<Double> implements DoubleSolution {

  private FitnessInfo fitnessInfo;

  private String arbiter;
  private MultiResult multiResult;

  protected List<Bounds<Double>> bounds;

  public MultiResult getMultiResult() {
    return multiResult;
  }

  public void setMultiResult(MultiResult multiResult) {
    this.multiResult = multiResult;
  }

  /**
   * Default constructor.
   *
   * @param numberOfObjectives Number of objectives.
   * @param fitnessInfo Contains all information about the problem.
   */
  public ScheduleDoubleSolution(
      int numberOfObjectives,
      FitnessInfo fitnessInfo,
      List<Bounds<Double>> bounds,
      String arbiter) {
    super(bounds.size(), numberOfObjectives);
    this.bounds = bounds;
    this.fitnessInfo = fitnessInfo;
    this.arbiter = arbiter;
    this.attributes.put("arbiter", arbiter);
  }

  public ScheduleDoubleSolution(ScheduleDoubleSolution solution) {
    super(solution.variables().size(), solution.objectives().length, solution.constraints().length);
    IntStream.range(0, solution.variables().size())
        .forEach((i) -> this.variables().set(i, (Double) solution.variables().get(i)));
    IntStream.range(0, solution.objectives().length)
        .forEach((i) -> this.objectives()[i] = solution.objectives()[i]);
    this.bounds = solution.bounds;
    this.attributes = new HashMap<>(solution.attributes);
    this.arbiter = solution.arbiter;

    FitnessInfo fitnessInfoCopy = null;

    if (fitnessInfo != null) {
      fitnessInfoCopy =
          new FitnessInfo(
              Map.copyOf(fitnessInfo.fitness()),
              List.copyOf(fitnessInfo.schedule()),
              fitnessInfo.fitnessFunction());
    }

    this.fitnessInfo = fitnessInfoCopy;
  }

  /**
   * Copy method.
   *
   * @return The copied solution.
   */
  @Override
  public ScheduleDoubleSolution copy() {

    return new ScheduleDoubleSolution(this);
  }

  public FitnessInfo getFitnessInfo() {
    return fitnessInfo;
  }

  public void setFitnessInfo(FitnessInfo fitnessInfo) {
    this.fitnessInfo = fitnessInfo;
  }

  public String getArbiter() {
    return arbiter;
  }

  public void setArbiter(String arbiter) {
    this.attributes.put("arbiter", arbiter);
    this.arbiter = arbiter;
  }

  @Override
  public Bounds<Double> getBounds(int i) {
    return null;
  }
}
