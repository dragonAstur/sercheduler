package com.uniovi.sercheduler.jmetal.algorithm;

import com.uniovi.sercheduler.dto.analysis.GenerationInfo;
import com.uniovi.sercheduler.jmetal.problem.SchedulePermutationSolution;
import org.uma.jmetal.algorithm.Algorithm;
import org.uma.jmetal.component.algorithm.EvolutionaryAlgorithm;
import org.uma.jmetal.component.catalogue.common.evaluation.Evaluation;
import org.uma.jmetal.component.catalogue.common.solutionscreation.SolutionsCreation;
import org.uma.jmetal.component.catalogue.common.termination.Termination;
import org.uma.jmetal.component.catalogue.ea.replacement.Replacement;
import org.uma.jmetal.component.catalogue.ea.selection.Selection;
import org.uma.jmetal.component.catalogue.ea.variation.Variation;
import org.uma.jmetal.util.observable.Observable;
import org.uma.jmetal.util.observable.ObservableEntity;
import org.uma.jmetal.util.observable.impl.DefaultObservable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MultiEvolutionaryAlgorithm
    implements Algorithm<List<SchedulePermutationSolution>>, ObservableEntity<Map<String, Object>> {

  private List<SchedulePermutationSolution> population;
  private Evaluation<SchedulePermutationSolution> evaluation;
  private final SolutionsCreation<SchedulePermutationSolution> createInitialPopulation;
  private Termination termination;
  private final Selection<SchedulePermutationSolution> selection;
  private final Variation<SchedulePermutationSolution> variation;
  private final Replacement<SchedulePermutationSolution> replacement;
  private final Map<String, Object> attributes;
  private long initTime;
  private long totalComputingTime;
  private int evaluations;
  private final Observable<Map<String, Object>> observable;
  private final String name;
  private ArrayList<GenerationInfo> generationsHistory;

  public MultiEvolutionaryAlgorithm(
      String name,
      SolutionsCreation<SchedulePermutationSolution> initialPopulationCreation,
      Evaluation<SchedulePermutationSolution> evaluation,
      Termination termination,
      Selection<SchedulePermutationSolution> selection,
      Variation<SchedulePermutationSolution> variation,
      Replacement<SchedulePermutationSolution> replacement) {

    this.name = name;
    this.createInitialPopulation = initialPopulationCreation;
    this.evaluation = evaluation;
    this.termination = termination;
    this.selection = selection;
    this.variation = variation;
    this.replacement = replacement;
    this.observable = new DefaultObservable<>("Evolutionary Algorithm");
    this.attributes = new HashMap<>();
  }

  @Override
  public void run() {

    this.initTime = System.currentTimeMillis();
    this.population = this.createInitialPopulation.create();
    this.population = this.evaluation.evaluate(this.population);
    this.generationsHistory = new ArrayList<>(evaluations / population.size());

    // We add our first generation
    updateGenerationsHistory();

    this.initProgress();

    while (!this.termination.isMet(this.attributes)) {
      List<SchedulePermutationSolution> matingPopulation = this.selection.select(this.population);
      List<SchedulePermutationSolution> offspringPopulation =
          this.variation.variate(this.population, matingPopulation);
      offspringPopulation = this.evaluation.evaluate(offspringPopulation);
      this.population = this.replacement.replace(this.population, offspringPopulation);
      updateGenerationsHistory();
      this.updateProgress();
    }

    this.totalComputingTime = System.currentTimeMillis() - this.initTime;
  }

  private void updateGenerationsHistory() {

    double totalEnergy = 0;
    double totalMakespan = 0;

    int standardEnergy = 0;
    int f1Energy = 0;
    int f2Energy = 0;

    int standardMakespan = 0;
    int f1Makespan = 0;
    int f2Makespan = 0;

    int objectiveEnergy = 0;
    int objectiveMakespan = 0;

    for (var solution : this.population) {
      if (solution.getMultiResult().objective().equals("energy")) {

        switch (solution.getMultiResult().fitness()) {
          case "simple" -> standardEnergy++;
          case "min-energy-UM" -> f1Energy++;
          case "fvlt-me" -> f2Energy++;
        }
        objectiveEnergy++;
      } else {

        switch (solution.getMultiResult().fitness()) {
          case "simple" -> standardMakespan++;
          case "rank" -> f1Makespan++;
          case "heft" -> f2Makespan++;
        }
        objectiveMakespan++;
      }
      totalMakespan += solution.getMultiResult().makespan();
      totalEnergy += solution.getMultiResult().energy();
    }
    generationsHistory.add(
        new GenerationInfo(
            totalMakespan / population().size(),
            totalEnergy / population.size(),
            objectiveEnergy,
            objectiveMakespan,
            standardEnergy,
            f1Energy,
            f2Energy,
            standardMakespan,
            f1Makespan,
            f2Makespan));
  }

  protected void initProgress() {
    this.evaluations = this.population.size();
    this.attributes.put("EVALUATIONS", this.evaluations);
    this.attributes.put("POPULATION", this.population);
    this.attributes.put("COMPUTING_TIME", this.currentComputingTime());
  }

  protected void updateProgress() {
    this.evaluations += this.variation.getOffspringPopulationSize() * 2;
    this.attributes.put("EVALUATIONS", this.evaluations);
    this.attributes.put("POPULATION", this.population);
    this.attributes.put("COMPUTING_TIME", this.currentComputingTime());
    this.observable.setChanged();
    this.observable.notifyObservers(this.attributes);
    this.totalComputingTime = this.currentComputingTime();
  }

  public long currentComputingTime() {
    return System.currentTimeMillis() - this.initTime;
  }

  public int numberOfEvaluations() {
    return this.evaluations;
  }

  public long totalComputingTime() {
    return this.totalComputingTime;
  }

  @Override
  public List<SchedulePermutationSolution> result() {
    return this.population;
  }

  public void updatePopulation(List<SchedulePermutationSolution> newPopulation) {
    this.population = newPopulation;
  }

  @Override
  public String name() {
    return this.name;
  }

  @Override
  public String description() {
    return "Evolutionary algorithm";
  }

  public Map<String, Object> attributes() {
    return this.attributes;
  }

  public List<SchedulePermutationSolution> population() {
    return this.population;
  }

  @Override
  public Observable<Map<String, Object>> observable() {
    return this.observable;
  }

  public void termination(Termination termination) {
    this.termination = termination;
  }

  public Termination termination() {
    return this.termination;
  }

  public void evaluation(Evaluation<SchedulePermutationSolution> evaluation) {
    this.evaluation = evaluation;
  }

  public Evaluation<SchedulePermutationSolution> evaluation() {
    return this.evaluation;
  }

  public ArrayList<GenerationInfo> getGenerationsHistory() {
    return generationsHistory;
  }
}
