package com.uniovi.sercheduler.service;

import com.uniovi.sercheduler.dto.InstanceData;
import com.uniovi.sercheduler.dto.analysis.MultiResult;
import com.uniovi.sercheduler.util.ThreadSafeStringArray;
import java.util.Comparator;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Calculates the fitness using other fitness calculators. */
public class FitnessCalculatorMulti extends FitnessCalculator {

  static final Logger LOG = LoggerFactory.getLogger(FitnessCalculatorMulti.class);

  ThreadSafeStringArray fitnessUsage = ThreadSafeStringArray.getInstance();

  List<FitnessCalculator> fitnessCalculators;

  /**
   * Full constructor.
   *
   * @param instanceData Infrastructure to use.
   */
  public FitnessCalculatorMulti(InstanceData instanceData) {
    super(instanceData);
    fitnessCalculators =
        List.of(
            new FitnessCalculatorSimple(instanceData),
            new FitnessCalculatorHeft(instanceData),
            new FitnessCalculatorRank(instanceData));
  }

  /**
   * Calculates the fitness using 3 calculators and returns the best schedule.
   *
   * @param plan Plan to calculate.
   * @return The information related to the Fitness.
   */
  @Override
  public FitnessInfo calculateFitness(List<PlanPair> plan) {

    var fitness =
        fitnessCalculators.stream()
            .map(c -> c.calculateFitness(plan))
            .min(Comparator.comparing(f -> f.fitness().get("makespan")))
            .orElseThrow();

    fitnessUsage.setValue(fitness.fitnessFunction(), fitness.fitness().get("makespan"));

    return fitness;
  }

  @Override
  public String fitnessName() {
    return "multi";
  }
}
