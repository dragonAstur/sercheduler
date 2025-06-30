package com.uniovi.sercheduler.service;

import static com.uniovi.sercheduler.util.LoadTestInstanceData.loadCalculatorTest;
import static com.uniovi.sercheduler.util.LoadTestInstanceData.loadFitnessTest;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.uniovi.sercheduler.dto.Host;
import com.uniovi.sercheduler.dto.InstanceData;
import com.uniovi.sercheduler.jmetal.problem.SchedulePermutationSolution;
import com.uniovi.sercheduler.service.calculator.*;
import com.uniovi.sercheduler.util.UnitParser;
import java.io.IOException;
import java.util.*;

import org.junit.jupiter.api.Test;

public class FitnessCalculatorTest {

  // The tests use the following graph
  /*
   *           │10
   *        ┌──▼─┐
   *    ┌───┤ T1 ├────┐
   *    │   └─┬──┘    │
   *    │18   │12     │8
   * ┌──▼─┐ ┌─▼──┐ ┌──▼─┐16
   * │ T2 │ │ T3 │ │ T4 ◄──
   * └──┬─┘ └─┬──┘ └──┬─┘
   *    │20   │24     │28
   *    │   ┌─▼──┐    │
   *    └───► T5 ◄────┘
   *        └────┘
   */



  /** Uses a workflow of 10 tasks to compute the fitness suing classic DNC */
  @Test
  void CalculateFitnessSimple() {

    InstanceData instanceData = loadFitnessTest();
    FitnessCalculator fitnessCalculator = new FitnessCalculatorSimple(instanceData);

    List<PlanPair> plan =
        List.of(
            new PlanPair(instanceData.workflow().get("task01"), instanceData.hosts().get("HostA")),
            new PlanPair(instanceData.workflow().get("task04"), instanceData.hosts().get("HostA")),
            new PlanPair(instanceData.workflow().get("task03"), instanceData.hosts().get("HostB")),
            new PlanPair(instanceData.workflow().get("task02"), instanceData.hosts().get("HostC")),
            new PlanPair(instanceData.workflow().get("task05"), instanceData.hosts().get("HostA")),
            new PlanPair(instanceData.workflow().get("task06"), instanceData.hosts().get("HostB")),
            new PlanPair(instanceData.workflow().get("task07"), instanceData.hosts().get("HostC")),
            new PlanPair(instanceData.workflow().get("task09"), instanceData.hosts().get("HostA")),
            new PlanPair(instanceData.workflow().get("task08"), instanceData.hosts().get("HostB")),
            new PlanPair(instanceData.workflow().get("task10"), instanceData.hosts().get("HostC")));

    FitnessInfo result =
        fitnessCalculator.calculateFitness(new SchedulePermutationSolution(1,2,null, plan,"makespan"));

    assertEquals(210D, result.fitness().get("makespan"));
    assertEquals (679.65D, result.fitness().get("energy"), 1e-10);
  }


  @Test
  void CalculateFitnessSimpleCheck() {

    InstanceData instanceData = loadFitnessTest();
    FitnessCalculator fitnessCalculator = new FitnessCalculatorSimple(instanceData);

    List<PlanPair> plan =
        List.of(
            new PlanPair(instanceData.workflow().get("task01"), instanceData.hosts().get("HostA")),
            new PlanPair(instanceData.workflow().get("task02"), instanceData.hosts().get("HostA")),
            new PlanPair(instanceData.workflow().get("task04"), instanceData.hosts().get("HostB")),
            new PlanPair(instanceData.workflow().get("task05"), instanceData.hosts().get("HostC")),
            new PlanPair(instanceData.workflow().get("task06"), instanceData.hosts().get("HostA")),
            new PlanPair(instanceData.workflow().get("task03"), instanceData.hosts().get("HostB")),
            new PlanPair(instanceData.workflow().get("task09"), instanceData.hosts().get("HostC")),
            new PlanPair(instanceData.workflow().get("task08"), instanceData.hosts().get("HostA")),
            new PlanPair(instanceData.workflow().get("task07"), instanceData.hosts().get("HostB")),
            new PlanPair(instanceData.workflow().get("task10"), instanceData.hosts().get("HostC")));

    FitnessInfo result =
        fitnessCalculator.calculateFitness(new SchedulePermutationSolution(1,2,null, plan, "makespan"));

    assertEquals(181.5D, result.fitness().get("makespan"));
  }



  @Test
  void CalculateFitnessHeft() {

    InstanceData instanceData = loadFitnessTest();
    FitnessCalculator fitnessCalculator = new FitnessCalculatorHeft(instanceData);
    List<PlanPair> plan =
        List.of(
            new PlanPair(instanceData.workflow().get("task01"), instanceData.hosts().get("HostA")),
            new PlanPair(instanceData.workflow().get("task02"), instanceData.hosts().get("HostA")),
            new PlanPair(instanceData.workflow().get("task04"), instanceData.hosts().get("HostA")),
            new PlanPair(instanceData.workflow().get("task05"), instanceData.hosts().get("HostC")),
            new PlanPair(instanceData.workflow().get("task03"), instanceData.hosts().get("HostC")),
            new PlanPair(instanceData.workflow().get("task06"), instanceData.hosts().get("HostB")),
            new PlanPair(instanceData.workflow().get("task07"), instanceData.hosts().get("HostA")),
            new PlanPair(instanceData.workflow().get("task09"), instanceData.hosts().get("HostB")),
            new PlanPair(instanceData.workflow().get("task08"), instanceData.hosts().get("HostC")),
            new PlanPair(instanceData.workflow().get("task10"), instanceData.hosts().get("HostC")));

    FitnessInfo result =
        fitnessCalculator.calculateFitness(new SchedulePermutationSolution(1,2,null, plan, "makespan"));

    assertEquals(205D, result.fitness().get("makespan"));
  }

  @Test
  void CalculateFitnessRank() {

    InstanceData instanceData = loadFitnessTest();
    FitnessCalculator fitnessCalculator = new FitnessCalculatorRank(instanceData);
    List<PlanPair> plan =
        List.of(
            new PlanPair(instanceData.workflow().get("task01"), instanceData.hosts().get("HostA")),
            new PlanPair(instanceData.workflow().get("task02"), instanceData.hosts().get("HostA")),
            new PlanPair(instanceData.workflow().get("task04"), instanceData.hosts().get("HostB")),
            new PlanPair(instanceData.workflow().get("task05"), instanceData.hosts().get("HostC")),
            new PlanPair(instanceData.workflow().get("task03"), instanceData.hosts().get("HostA")),
            new PlanPair(instanceData.workflow().get("task06"), instanceData.hosts().get("HostB")),
            new PlanPair(instanceData.workflow().get("task07"), instanceData.hosts().get("HostC")),
            new PlanPair(instanceData.workflow().get("task09"), instanceData.hosts().get("HostA")),
            new PlanPair(instanceData.workflow().get("task08"), instanceData.hosts().get("HostB")),
            new PlanPair(instanceData.workflow().get("task10"), instanceData.hosts().get("HostC")));

    FitnessInfo result =
        fitnessCalculator.calculateFitness(new SchedulePermutationSolution(1,2,null, plan, "makespan"));

    assertEquals(209D, result.fitness().get("makespan"));
  }

  @Test
  void CalculateFitnessHeuristic() {

    InstanceData instanceData = loadFitnessTest();
    FitnessCalculator fitnessCalculator = new FitnessCalculatorHeuristic(instanceData);
    List<PlanPair> plan =
        List.of(
            new PlanPair(instanceData.workflow().get("task01"), instanceData.hosts().get("HostA")),
            new PlanPair(instanceData.workflow().get("task02"), instanceData.hosts().get("HostA")),
            new PlanPair(instanceData.workflow().get("task04"), instanceData.hosts().get("HostB")),
            new PlanPair(instanceData.workflow().get("task05"), instanceData.hosts().get("HostC")),
            new PlanPair(instanceData.workflow().get("task03"), instanceData.hosts().get("HostA")),
            new PlanPair(instanceData.workflow().get("task06"), instanceData.hosts().get("HostB")),
            new PlanPair(instanceData.workflow().get("task07"), instanceData.hosts().get("HostC")),
            new PlanPair(instanceData.workflow().get("task09"), instanceData.hosts().get("HostA")),
            new PlanPair(instanceData.workflow().get("task08"), instanceData.hosts().get("HostB")),
            new PlanPair(instanceData.workflow().get("task10"), instanceData.hosts().get("HostC")));

    FitnessInfo result =
        fitnessCalculator.calculateFitness(new SchedulePermutationSolution(1,2,null, plan, "makespan"));

    assertEquals(180.5D, result.fitness().get("makespan"));
  }

  @Test
  void CalculateFitnessMulti() {

    InstanceData instanceData = loadFitnessTest();
    FitnessCalculator fitnessCalculator =
        new FitnessCalculatorMulti(
            instanceData,
            List.of(
                new FitnessCalculatorSimple(instanceData),
                new FitnessCalculatorHeft(instanceData),
                new FitnessCalculatorRank(instanceData)),
            Collections.emptyList(),
                new ArrayList<>());

    List<PlanPair> plan =
        List.of(
            new PlanPair(instanceData.workflow().get("task01"), instanceData.hosts().get("HostA")),
            new PlanPair(instanceData.workflow().get("task04"), instanceData.hosts().get("HostA")),
            new PlanPair(instanceData.workflow().get("task03"), instanceData.hosts().get("HostB")),
            new PlanPair(instanceData.workflow().get("task02"), instanceData.hosts().get("HostC")),
            new PlanPair(instanceData.workflow().get("task05"), instanceData.hosts().get("HostA")),
            new PlanPair(instanceData.workflow().get("task06"), instanceData.hosts().get("HostB")),
            new PlanPair(instanceData.workflow().get("task07"), instanceData.hosts().get("HostC")),
            new PlanPair(instanceData.workflow().get("task09"), instanceData.hosts().get("HostA")),
            new PlanPair(instanceData.workflow().get("task08"), instanceData.hosts().get("HostB")),
            new PlanPair(instanceData.workflow().get("task10"), instanceData.hosts().get("HostC")));

    FitnessInfo result =
        fitnessCalculator.calculateFitness(new SchedulePermutationSolution(1,2,null, plan, "makespan"));

    assertEquals(209D, result.fitness().get("makespan"));
  }




}
