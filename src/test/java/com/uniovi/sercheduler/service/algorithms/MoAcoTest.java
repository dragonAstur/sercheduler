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

    assertEquals(12, result.size());
    // Verify non-domination
    assertTrue(NonDominatedChecker.areAllNonDominated(result));

    List<Map<String, Double>> expectedObjectives =
        List.of(
            Map.of("energy", 538.5, "makespan", 204.0),
            Map.of("energy", 368.9, "makespan", 263.5),
            Map.of("energy", 574.5, "makespan", 195.0),
            Map.of("energy", 404.9, "makespan", 254.5),
            Map.of("energy", 515.3, "makespan", 208.0),
            Map.of("energy", 451.69999999999993, "makespan", 245.5),
            Map.of("energy", 562.0999999999999, "makespan", 199.0),
            Map.of("energy", 476.69999999999993, "makespan", 223.5),
            Map.of("energy", 495.69999999999993, "makespan", 216.5),
            Map.of("energy", 469.49999999999994, "makespan", 241.5),
            Map.of("energy", 555.8999999999999, "makespan", 201.0),
            Map.of("energy", 594.15, "makespan", 187.5));
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
            new MoAcoParameters(350, 10, 1.0, 1.0, 2.0, 0.1));

    moAco.run();

    var result = moAco.result();

    assertEquals(3, result.size());
    // Verify non-domination
    assertTrue(NonDominatedChecker.areAllNonDominated(result));

    List<Map<String, Double>> expectedObjectives =
        List.of(
            Map.of("energy", 503.85, "makespan", 181.5),
            Map.of("energy", 461.70000000000005, "makespan", 184.5),
            Map.of("energy", 560.95, "makespan", 180.5));

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
