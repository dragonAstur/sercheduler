package com.uniovi.sercheduler.service;

import com.uniovi.sercheduler.dto.InstanceData;
import com.uniovi.sercheduler.jmetal.problem.SchedulePermutationSolution;
import com.uniovi.sercheduler.util.ThreadSafeStringArray;
import java.util.Comparator;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Calculates the fitness using other fitness calculators. */
public class FitnessCalculatorMulti extends FitnessCalculator {

  static final Logger LOG = LoggerFactory.getLogger(FitnessCalculatorMulti.class);

  ThreadSafeStringArray fitnessUsage = ThreadSafeStringArray.getInstance();

  List<FitnessCalculator> fitnessCalculatorsMakespan;
  List<FitnessCalculator> fitnessCalculatorsEnergy;


  /**
   * Full constructor.
   *
   * @param instanceData Infrastructure to use.
   */
  public FitnessCalculatorMulti(InstanceData instanceData) {
    super(instanceData);
    fitnessCalculatorsMakespan =
        List.of(
            new FitnessCalculatorSimple(instanceData),
            new FitnessCalculatorHeft(instanceData),
            new FitnessCalculatorRank(instanceData));

    fitnessCalculatorsEnergy =
        List.of(new FitnessCalculatorSimple(instanceData),
            new FitnessCalculatorHeftEnergy(instanceData));
  }

  /**
   * Calculates the fitness using 3 calculators and returns the best schedule.
   *
   * @param solution@return The information related to the Fitness.
   */
  @Override
  public FitnessInfo calculateFitness(SchedulePermutationSolution solution) {
    List<FitnessCalculator> fitnessCalculators;

    if(solution.getArbiter().equals("energy")){
      fitnessCalculators = fitnessCalculatorsEnergy;
    }else {
      fitnessCalculators = fitnessCalculatorsMakespan;
    }




    var fitness =
        fitnessCalculators.stream()
            .map(c -> c.calculateFitness(solution))
            .min(
                Comparator.comparing(
                    f -> f.fitness().get(solution.getArbiter())))
            .orElseThrow();

    fitnessUsage.setValue(fitness.fitnessFunction(), fitness.fitness().get("makespan"));

    return fitness;
  }

  @Override
  public String fitnessName() {
    return "multi";
  }
}
