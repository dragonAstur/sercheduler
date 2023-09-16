package com.uniovi.sercheduler.jmetal.problem;

import com.uniovi.sercheduler.service.FitnessInfo;
import com.uniovi.sercheduler.service.PlanPair;
import java.util.List;
import java.util.Map;
import org.uma.jmetal.solution.AbstractSolution;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.solution.permutationsolution.PermutationSolution;

public class SchedulePermutationSolution extends AbstractSolution<PlanPair>
    implements PermutationSolution<PlanPair> {

  FitnessInfo fitnessInfo;
  List<PlanPair> plan;

  public SchedulePermutationSolution(
      int numberOfVariables, int numberOfObjectives, FitnessInfo fitnessInfo, List<PlanPair> plan) {
    super(numberOfVariables, numberOfObjectives);
    this.fitnessInfo = fitnessInfo;
    this.plan = plan;
  }

  /**
   * @return
   */
  @Override
  public Solution<PlanPair> copy() {

    var fitnessInfoCopy =
        new FitnessInfo(Map.copyOf(fitnessInfo.fitness()), List.copyOf(fitnessInfo.schedule()));

    return new SchedulePermutationSolution(
        this.variables().size(), this.objectives().length, fitnessInfoCopy, List.copyOf(this.plan));
  }

  public void setFitnessInfo(FitnessInfo fitnessInfo) {
    this.fitnessInfo = fitnessInfo;
  }

  public void setPlan(List<PlanPair> plan) {
    this.plan = plan;
  }

  public List<PlanPair> getPlan() {
    return plan;
  }

  public FitnessInfo getFitnessInfo() {
    return fitnessInfo;
  }

  /**
   * @return
   */
  @Override
  public int getLength() {
    return plan.size();
  }
}
