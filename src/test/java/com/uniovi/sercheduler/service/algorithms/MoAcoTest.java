package com.uniovi.sercheduler.service.algorithms;

import com.uniovi.sercheduler.dao.Objective;
import com.uniovi.sercheduler.dto.InstanceData;
import com.uniovi.sercheduler.jmetal.algorithm.MOACO;
import com.uniovi.sercheduler.jmetal.evaluation.MultiThreadEvaluationMulti;
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

    var moAco = new MOACO(problem, new Random(randomSeed), new SequentialEvaluation<>(problem));

    moAco.run();

    var result = moAco.result();

    assertEquals(11, result.size());
    // Verify non-domination
    assertTrue(NonDominatedChecker.areAllNonDominated(result));

    List<Map<String, Double>> expectedObjectives =
        List.of(
            Map.of("energy", 419.5, "makespan", 231.5),
            Map.of("energy", 566.9, "makespan", 187.0),
            Map.of("energy", 596.55, "makespan", 181.5),
            Map.of("energy", 415.7, "makespan", 254.5),
            Map.of("energy", 553.65, "makespan", 196.5),
            Map.of("energy", 455.49999999999994, "makespan", 222.5),
            Map.of("energy", 559.0999999999999, "makespan", 193.0),
            Map.of("energy", 497.29999999999995, "makespan", 212.5),
            Map.of("energy", 533.3, "makespan", 203.5),
            Map.of("energy", 368.9, "makespan", 263.5),
            Map.of("energy", 518.8999999999999, "makespan", 208.0));

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
            new MultiThreadEvaluationMulti(0, problem, Objective.MAKESPAN.objectiveName));

    moAco.run();

    var result = moAco.result();

    assertEquals(7, result.size());
    // Verify non-domination
    assertTrue(NonDominatedChecker.areAllNonDominated(result));

    List<Map<String, Double>> expectedObjectives = List.of(
            Map.of("energy", 609.55, "makespan", 180.5),
            Map.of("energy", 530.6, "makespan", 199.0),
            Map.of("energy", 531.0999999999999, "makespan", 191.0),
            Map.of("energy", 574.0500000000001, "makespan", 181.5),
            Map.of("energy", 558.9000000000001, "makespan", 184.5),
            Map.of("energy", 465.95, "makespan", 224.5),
            Map.of("energy", 482.5, "makespan", 209.0)
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
