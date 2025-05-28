package com.uniovi.sercheduler.jmetal.algorithm;

import com.uniovi.sercheduler.jmetal.operator.ScheduleCrossover;
import com.uniovi.sercheduler.jmetal.problem.SchedulePermutationSolution;
import org.uma.jmetal.algorithm.AlgorithmBuilder;
import org.uma.jmetal.algorithm.multiobjective.ibea.IBEA;
import org.uma.jmetal.algorithm.multiobjective.ibea.mIBEA;
import org.uma.jmetal.operator.crossover.CrossoverOperator;
import org.uma.jmetal.operator.mutation.MutationOperator;
import org.uma.jmetal.operator.selection.SelectionOperator;
import org.uma.jmetal.operator.selection.impl.BinaryTournamentSelection;
import org.uma.jmetal.problem.Problem;

import java.util.List;

/**
 * This class implements the IBEA algorithm
 */
public class IBEABuilder implements AlgorithmBuilder<IBEA<SchedulePermutationSolution>> {
  private Problem<SchedulePermutationSolution> problem;
  private int populationSize;
  private int archiveSize;
  private int maxEvaluations;

  private CrossoverOperator<SchedulePermutationSolution> crossover;
  private MutationOperator<SchedulePermutationSolution> mutation;
  private SelectionOperator<List<SchedulePermutationSolution>, SchedulePermutationSolution> selection;

  /**
   * Constructor
   * @param problem The problem to solve.
   */
  public IBEABuilder(Problem<SchedulePermutationSolution> problem) {
    this.problem = problem;
    populationSize = 100;
    archiveSize = 100;
    maxEvaluations = 25000;




    selection = new BinaryTournamentSelection<>();
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

  public CrossoverOperator<SchedulePermutationSolution> getCrossover() {
    return crossover;
  }

  public MutationOperator<SchedulePermutationSolution> getMutation() {
    return mutation;
  }

  public SelectionOperator<List<SchedulePermutationSolution>, SchedulePermutationSolution> getSelection() {
    return selection;
  }

  /* Setters */
  public IBEABuilder setPopulationSize(int populationSize) {
    this.populationSize = populationSize;

    return this;
  }

  public IBEABuilder setArchiveSize(int archiveSize) {
    this.archiveSize = archiveSize;

    return this;
  }

  public IBEABuilder setMaxEvaluations(int maxEvaluations) {
    this.maxEvaluations = maxEvaluations;

    return this;
  }

  public IBEABuilder setCrossover(CrossoverOperator<SchedulePermutationSolution> crossover) {
    this.crossover = crossover;

    return this;
  }

  public IBEABuilder setMutation(MutationOperator<SchedulePermutationSolution> mutation) {
    this.mutation = mutation;

    return this;
  }

  public IBEABuilder setSelection(SelectionOperator<List<SchedulePermutationSolution>, SchedulePermutationSolution> selection) {
    this.selection = selection;

    return this;
  }

  public IBEA<SchedulePermutationSolution> build() {
    return new mIBEA<>(problem, populationSize, archiveSize, maxEvaluations, selection, crossover,
        mutation);
  }
}