package com.uniovi.sercheduler.service.calculator;

import com.uniovi.sercheduler.dto.InstanceData;
import com.uniovi.sercheduler.dto.analysis.MultiResult;
import com.uniovi.sercheduler.jmetal.problem.SchedulePermutationSolution;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import com.uniovi.sercheduler.service.FitnessInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Calculates the fitness using other fitness calculators. */
public class FitnessCalculatorMulti extends FitnessCalculator {

  static final Logger LOG = LoggerFactory.getLogger(FitnessCalculatorMulti.class);

  private final List<FitnessCalculator> fitnessCalculatorsMakespan;
  private final List<FitnessCalculator> fitnessCalculatorsEnergy;

  String overrideObjective;

  private final ArrayList<MultiResult> evaluationsHistory;

  /**
   * Basic constructor
   *
   * @param instanceData Infrastructure to use.
   */
  public FitnessCalculatorMulti(
      InstanceData instanceData,
      List<FitnessCalculator> fitnessCalculatorsMakespan,
      List<FitnessCalculator> fitnessCalculatorsEnergy,
      ArrayList<MultiResult> evaluationsHistory) {
    super(instanceData);
    this.fitnessCalculatorsMakespan = fitnessCalculatorsMakespan;
    this.fitnessCalculatorsEnergy = fitnessCalculatorsEnergy;
    this.overrideObjective = "none";
    this.evaluationsHistory = evaluationsHistory;
  }

  public FitnessCalculatorMulti(
      InstanceData instanceData,
      List<FitnessCalculator> fitnessCalculatorsMakespan,
      List<FitnessCalculator> fitnessCalculatorsEnergy,
      String overrideObjective,
      ArrayList<MultiResult> evaluationsHistory) {
    super(instanceData);
    this.fitnessCalculatorsMakespan = fitnessCalculatorsMakespan;
    this.fitnessCalculatorsEnergy = fitnessCalculatorsEnergy;
    this.overrideObjective = overrideObjective;
    this.evaluationsHistory = evaluationsHistory;
  }

  /**
   * Calculates the fitness using 3 calculators and returns the best schedule.
   *
   * @param solution@return The information related to the Fitness.
   */
  @Override
  public FitnessInfo calculateFitness(SchedulePermutationSolution solution) {
    List<FitnessCalculator> fitnessCalculators;

    if ((solution.getArbiter().equals("energy") || overrideObjective.equals("energy"))
        && !overrideObjective.equals("makespan")) {
      fitnessCalculators = fitnessCalculatorsEnergy;
    } else if (solution.getArbiter().equals("makespan") || overrideObjective.equals("makespan")) {
      fitnessCalculators = fitnessCalculatorsMakespan;
    } else {
      throw new RuntimeException("No fitness calculator found");
    }

    var objective = overrideObjective.equals("none") ? solution.getArbiter() : overrideObjective;

    var fitness =
        fitnessCalculators.stream()
            .map(c -> c.calculateFitness(solution))
            .min(Comparator.comparing(f -> f.fitness().get(objective)))
            .orElseThrow();

    solution.setMultiResult(new MultiResult(
            fitness.fitness().get("makespan"),
            fitness.fitness().get("energy"),
            fitness.fitnessFunction(),
            solution.getArbiter()));
//    evaluationsHistory.add(
//        new MultiResult(
//            fitness.fitness().get("makespan"),
//            fitness.fitness().get("energy"),
//            fitness.fitnessFunction(),
//            solution.getArbiter()));

    return fitness;
  }

  @Override
  public String fitnessName() {
    return "multi";
  }
}
