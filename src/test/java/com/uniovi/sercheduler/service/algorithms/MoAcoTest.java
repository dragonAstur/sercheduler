package com.uniovi.sercheduler.service.algorithms;

import com.uniovi.sercheduler.dao.Objective;
import com.uniovi.sercheduler.dto.InstanceData;
import com.uniovi.sercheduler.jmetal.algorithm.MOACO;
import com.uniovi.sercheduler.jmetal.algorithm.MoAcoParameters;
import com.uniovi.sercheduler.jmetal.evaluation.SequentialEvaluationMulti;
import com.uniovi.sercheduler.jmetal.problem.SchedulingProblem;
import com.uniovi.sercheduler.util.NonDominatedChecker;
import org.junit.jupiter.api.Test;
import org.uma.jmetal.component.catalogue.common.evaluation.impl.SequentialEvaluation;

import java.util.List;
import java.util.Map;
import java.util.Random;

import static com.uniovi.sercheduler.util.LoadTestInstanceData.loadFitnessTest;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MoAcoTest {

  @Test
  void testMoAco() {

    InstanceData instanceData = loadFitnessTest();

    var randomSeed = 1L;
    var problem =
        new SchedulingProblem(
            "Schedule test",
            "simple",
            randomSeed,
            instanceData,
            List.of(Objective.ENERGY, Objective.MAKESPAN),
            "energy",
            1);

    var moAco =
        new MOACO(
            problem,
            new Random(randomSeed),
            new SequentialEvaluation<>(problem),
            new MoAcoParameters(350, 10, 1.0, 1.0, 2.0, 0.1));

    moAco.run();

    var result = moAco.result();

    assertEquals(6, result.size());
    // Verify non-domination
    assertTrue(NonDominatedChecker.areAllNonDominated(result));

    List<Map<String, Double>> expectedObjectives = List.of(
            Map.of("energy", 519.1, "makespan", 212.0),
            Map.of("energy", 535.05, "makespan", 211.5),
            Map.of("energy", 497.29999999999995, "makespan", 212.5),
            Map.of("energy", 565.3499999999999, "makespan", 196.5),
            Map.of("energy", 604.65, "makespan", 181.5),
            Map.of("energy", 583.5999999999999, "makespan", 183.5)
    );

    for (int i = 0; i < result.size(); i++) {
      var fitness = result.get(i).getFitnessInfo().fitness();
      assertEquals(
          expectedObjectives.get(i).get("energy"),
          fitness.get(Objective.ENERGY.objectiveName),
          1e-6);
      assertEquals(
          expectedObjectives.get(i).get("makespan"),
          fitness.get(Objective.MAKESPAN.objectiveName),
          1e-6);
    }
  }

  @Test
  void testMoAcoMoCmf() {

    InstanceData instanceData = loadFitnessTest();

    var randomSeed = 1L;
    var problem =
        new SchedulingProblem(
            "Schedule test",
            "multi-moaco",
            randomSeed,
            instanceData,
            List.of(Objective.ENERGY, Objective.MAKESPAN),
            "energy",
            1);

    var moAco =
        new MOACO(
            problem,
            new Random(randomSeed),
            new SequentialEvaluationMulti(0, problem, Objective.MAKESPAN.objectiveName),
            new MoAcoParameters(100, 20, 1.0, 2.0, 1.0, 0.1));

    moAco.run();

    var result = moAco.result();

    assertEquals(5, result.size());
    // Verify non-domination
    assertTrue(NonDominatedChecker.areAllNonDominated(result));

    List<Map<String, Double>> expectedObjectives = List.of(
            Map.of("energy", 521.2, "makespan", 186.5),
            Map.of("energy", 578.5, "makespan", 180.5),
            Map.of("energy", 519.8, "makespan", 203.5),
            Map.of("energy", 513.1, "makespan", 213.5),
            Map.of("energy", 524.1, "makespan", 181.5)
    );

    for (int i = 0; i < result.size(); i++) {
      var fitness = result.get(i).getFitnessInfo().fitness();
      assertEquals(
          expectedObjectives.get(i).get("energy"),
          fitness.get(Objective.ENERGY.objectiveName),
          1e-6);
      assertEquals(
          expectedObjectives.get(i).get("makespan"),
          fitness.get(Objective.MAKESPAN.objectiveName),
          1e-6);
    }
  }
}
