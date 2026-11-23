package com.uniovi.sercheduler.jmetal.problem;

import com.uniovi.sercheduler.dao.Objective;
import com.uniovi.sercheduler.dto.analysis.MultiResult;
import com.uniovi.sercheduler.service.FitnessInfo;
import com.uniovi.sercheduler.service.PlanPair;
import java.util.List;
import java.util.Map;
import org.uma.jmetal.solution.AbstractSolution;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.solution.permutationsolution.PermutationSolution;

/**
 * Defines a schedule solution. Its plan determines equality and hash code, so it must not be
 * modified while the solution is used as a hash-based collection key.
 */
public class SchedulePermutationSolution extends AbstractSolution<PlanPair>
    implements PermutationSolution<PlanPair> {

  FitnessInfo fitnessInfo;

  String arbiter;
  List<PlanPair> plan;
 private  MultiResult multiResult;

 private SolutionOrigin origin;

  public MultiResult getMultiResult() {
    return multiResult;
  }

  public void setMultiResult(MultiResult multiResult) {
    this.multiResult = multiResult;
  }

  /**
   * Default constructor.
   *
   * @param numberOfVariables Non-negative number of variables; the plan stores their values.
   * @param numberOfObjectives Number of objectives.
   * @param fitnessInfo Contains all information about the problem.
   * @param plan List of the possible schedule.
   */
  public SchedulePermutationSolution(
      int numberOfVariables,
      int numberOfObjectives,
      FitnessInfo fitnessInfo,
      List<PlanPair> plan,
      String arbiter) {
    super(inheritedVariableCount(numberOfVariables), numberOfObjectives);
    this.fitnessInfo = fitnessInfo;
    this.plan = plan;
    this.arbiter = arbiter;
    this.origin = SolutionOrigin.CROSS_MUTATION;
  }

    public SchedulePermutationSolution(
            int numberOfVariables,
            int numberOfObjectives,
            FitnessInfo fitnessInfo,
            List<PlanPair> plan,
            String arbiter,
            SolutionOrigin origin) {
        super(inheritedVariableCount(numberOfVariables), numberOfObjectives);
        this.fitnessInfo = fitnessInfo;
        this.plan = plan;
        this.arbiter = arbiter;
        this.origin = origin;
    }

  /**
   * Copy method.
   *
   * @return The copied solution.
   */
  @Override
  public Solution<PlanPair> copy() {
    FitnessInfo fitnessInfoCopy = null;

    if (fitnessInfo != null) {
      fitnessInfoCopy =
          new FitnessInfo(
              Map.copyOf(fitnessInfo.fitness()),
              List.copyOf(fitnessInfo.schedule()),
              fitnessInfo.fitnessFunction());
    }

    return new SchedulePermutationSolution(
        this.variables().size(),
        this.objectives().length,
        fitnessInfoCopy,
        List.copyOf(this.plan),
        this.arbiter,
        this.origin);
  }

  public List<PlanPair> getPlan() {
    return plan;
  }

  public void setPlan(List<PlanPair> plan) {
    this.plan = plan;
  }

  public FitnessInfo getFitnessInfo() {
    return fitnessInfo;
  }

  public void setFitnessInfo(FitnessInfo fitnessInfo) {
    this.fitnessInfo = fitnessInfo;
  }

    /**
    * Get the length of the solution.
    *
    * @return The size of the plan.
    */
    @Override
    public int getLength() {
    return plan.size();
  }

    public String getArbiter() {
    return arbiter;
  }

    public void setArbiter(String arbiter) {
    this.arbiter = arbiter;
  }

    @Override
    public List<PlanPair> variables() {
    return plan;
  }

    @Override
    public int hashCode() {
        return variables().hashCode();
    }

    private static int inheritedVariableCount(int numberOfVariables) {
        if (numberOfVariables < 0) {
            throw new IllegalArgumentException("Illegal Capacity: " + numberOfVariables);
        }
        // variables() is backed by plan, not AbstractSolution's list.
        return 0;
    }

    public SolutionOrigin getOrigin() {
        return origin;
    }

    public void setOrigin(SolutionOrigin origin) {
        this.origin = origin;
    }
}
