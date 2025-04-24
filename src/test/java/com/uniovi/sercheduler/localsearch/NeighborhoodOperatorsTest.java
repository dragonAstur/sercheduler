package com.uniovi.sercheduler.localsearch;

import com.uniovi.sercheduler.dto.InstanceData;
import com.uniovi.sercheduler.jmetal.problem.SchedulePermutationSolution;
import com.uniovi.sercheduler.localsearch.operator.*;
import com.uniovi.sercheduler.service.PlanPair;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static com.uniovi.sercheduler.util.LoadLocalsearchTestInstanceData.loadNeighborhoodOperatorsTest;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class NeighborhoodOperatorsTest {

    @Test
    void swapTest(){

        InstanceData instanceData = loadNeighborhoodOperatorsTest();

        int position = 5;

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

        SchedulePermutationSolution originalSolution = new SchedulePermutationSolution(0, 0, null, plan, "");

        List<List<PlanPair>> expectedPlans = new ArrayList<>();

        List<PlanPair> expectedPlan1 =
                List.of(
                        new PlanPair(instanceData.workflow().get("task01"), instanceData.hosts().get("HostA")),
                        new PlanPair(instanceData.workflow().get("task06"), instanceData.hosts().get("HostB")),
                        new PlanPair(instanceData.workflow().get("task04"), instanceData.hosts().get("HostA")),
                        new PlanPair(instanceData.workflow().get("task05"), instanceData.hosts().get("HostC")),
                        new PlanPair(instanceData.workflow().get("task03"), instanceData.hosts().get("HostC")),
                        new PlanPair(instanceData.workflow().get("task02"), instanceData.hosts().get("HostA")),
                        new PlanPair(instanceData.workflow().get("task07"), instanceData.hosts().get("HostA")),
                        new PlanPair(instanceData.workflow().get("task09"), instanceData.hosts().get("HostB")),
                        new PlanPair(instanceData.workflow().get("task08"), instanceData.hosts().get("HostC")),
                        new PlanPair(instanceData.workflow().get("task10"), instanceData.hosts().get("HostC")));

        expectedPlans.add(expectedPlan1);

        List<PlanPair> expectedPlan2 =
                List.of(
                        new PlanPair(instanceData.workflow().get("task01"), instanceData.hosts().get("HostA")),
                        new PlanPair(instanceData.workflow().get("task02"), instanceData.hosts().get("HostA")),
                        new PlanPair(instanceData.workflow().get("task06"), instanceData.hosts().get("HostB")),
                        new PlanPair(instanceData.workflow().get("task05"), instanceData.hosts().get("HostC")),
                        new PlanPair(instanceData.workflow().get("task03"), instanceData.hosts().get("HostC")),
                        new PlanPair(instanceData.workflow().get("task04"), instanceData.hosts().get("HostA")),
                        new PlanPair(instanceData.workflow().get("task07"), instanceData.hosts().get("HostA")),
                        new PlanPair(instanceData.workflow().get("task09"), instanceData.hosts().get("HostB")),
                        new PlanPair(instanceData.workflow().get("task08"), instanceData.hosts().get("HostC")),
                        new PlanPair(instanceData.workflow().get("task10"), instanceData.hosts().get("HostC")));

        expectedPlans.add(expectedPlan2);

        List<PlanPair> expectedPlan3 =
                List.of(
                        new PlanPair(instanceData.workflow().get("task01"), instanceData.hosts().get("HostA")),
                        new PlanPair(instanceData.workflow().get("task02"), instanceData.hosts().get("HostA")),
                        new PlanPair(instanceData.workflow().get("task04"), instanceData.hosts().get("HostA")),
                        new PlanPair(instanceData.workflow().get("task06"), instanceData.hosts().get("HostB")),
                        new PlanPair(instanceData.workflow().get("task03"), instanceData.hosts().get("HostC")),
                        new PlanPair(instanceData.workflow().get("task05"), instanceData.hosts().get("HostC")),
                        new PlanPair(instanceData.workflow().get("task07"), instanceData.hosts().get("HostA")),
                        new PlanPair(instanceData.workflow().get("task09"), instanceData.hosts().get("HostB")),
                        new PlanPair(instanceData.workflow().get("task08"), instanceData.hosts().get("HostC")),
                        new PlanPair(instanceData.workflow().get("task10"), instanceData.hosts().get("HostC")));

        expectedPlans.add(expectedPlan3);

        List<PlanPair> expectedPlan4 =
                List.of(
                        new PlanPair(instanceData.workflow().get("task01"), instanceData.hosts().get("HostA")),
                        new PlanPair(instanceData.workflow().get("task02"), instanceData.hosts().get("HostA")),
                        new PlanPair(instanceData.workflow().get("task04"), instanceData.hosts().get("HostA")),
                        new PlanPair(instanceData.workflow().get("task05"), instanceData.hosts().get("HostC")),
                        new PlanPair(instanceData.workflow().get("task06"), instanceData.hosts().get("HostB")),
                        new PlanPair(instanceData.workflow().get("task03"), instanceData.hosts().get("HostC")),
                        new PlanPair(instanceData.workflow().get("task07"), instanceData.hosts().get("HostA")),
                        new PlanPair(instanceData.workflow().get("task09"), instanceData.hosts().get("HostB")),
                        new PlanPair(instanceData.workflow().get("task08"), instanceData.hosts().get("HostC")),
                        new PlanPair(instanceData.workflow().get("task10"), instanceData.hosts().get("HostC")));

        expectedPlans.add(expectedPlan4);

        List<PlanPair> expectedPlan5 =
                List.of(
                        new PlanPair(instanceData.workflow().get("task01"), instanceData.hosts().get("HostA")),
                        new PlanPair(instanceData.workflow().get("task02"), instanceData.hosts().get("HostA")),
                        new PlanPair(instanceData.workflow().get("task04"), instanceData.hosts().get("HostA")),
                        new PlanPair(instanceData.workflow().get("task05"), instanceData.hosts().get("HostC")),
                        new PlanPair(instanceData.workflow().get("task03"), instanceData.hosts().get("HostC")),
                        new PlanPair(instanceData.workflow().get("task07"), instanceData.hosts().get("HostA")),
                        new PlanPair(instanceData.workflow().get("task06"), instanceData.hosts().get("HostB")),
                        new PlanPair(instanceData.workflow().get("task09"), instanceData.hosts().get("HostB")),
                        new PlanPair(instanceData.workflow().get("task08"), instanceData.hosts().get("HostC")),
                        new PlanPair(instanceData.workflow().get("task10"), instanceData.hosts().get("HostC")));

        expectedPlans.add(expectedPlan5);

        List<PlanPair> expectedPlan6 =
                List.of(
                        new PlanPair(instanceData.workflow().get("task01"), instanceData.hosts().get("HostA")),
                        new PlanPair(instanceData.workflow().get("task02"), instanceData.hosts().get("HostA")),
                        new PlanPair(instanceData.workflow().get("task04"), instanceData.hosts().get("HostA")),
                        new PlanPair(instanceData.workflow().get("task05"), instanceData.hosts().get("HostC")),
                        new PlanPair(instanceData.workflow().get("task03"), instanceData.hosts().get("HostC")),
                        new PlanPair(instanceData.workflow().get("task09"), instanceData.hosts().get("HostB")),
                        new PlanPair(instanceData.workflow().get("task07"), instanceData.hosts().get("HostA")),
                        new PlanPair(instanceData.workflow().get("task06"), instanceData.hosts().get("HostB")),
                        new PlanPair(instanceData.workflow().get("task08"), instanceData.hosts().get("HostC")),
                        new PlanPair(instanceData.workflow().get("task10"), instanceData.hosts().get("HostC")));

        expectedPlans.add(expectedPlan6);

        List<GeneratedNeighbor> generatedNeighbors = new NeighborhoodSwapPositional().execute(originalSolution, position);

        for(int i = 0; i < generatedNeighbors.size(); i++)
            assertEquals(
                    expectedPlans.get(i),
                    generatedNeighbors.get(i).generatedSolution().getPlan()
            );

    }

    @Test
    void insertionTest(){

        InstanceData instanceData = loadNeighborhoodOperatorsTest();

        int position = 5;

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

        SchedulePermutationSolution originalSolution = new SchedulePermutationSolution(0, 0, null, plan, "");

        List<List<PlanPair>> expectedPlans = new ArrayList<>();

        List<PlanPair> expectedPlan1 =
                List.of(
                        new PlanPair(instanceData.workflow().get("task01"), instanceData.hosts().get("HostA")),
                        new PlanPair(instanceData.workflow().get("task06"), instanceData.hosts().get("HostB")),
                        new PlanPair(instanceData.workflow().get("task02"), instanceData.hosts().get("HostA")),
                        new PlanPair(instanceData.workflow().get("task04"), instanceData.hosts().get("HostA")),
                        new PlanPair(instanceData.workflow().get("task05"), instanceData.hosts().get("HostC")),
                        new PlanPair(instanceData.workflow().get("task03"), instanceData.hosts().get("HostC")),
                        new PlanPair(instanceData.workflow().get("task07"), instanceData.hosts().get("HostA")),
                        new PlanPair(instanceData.workflow().get("task09"), instanceData.hosts().get("HostB")),
                        new PlanPair(instanceData.workflow().get("task08"), instanceData.hosts().get("HostC")),
                        new PlanPair(instanceData.workflow().get("task10"), instanceData.hosts().get("HostC")));

        expectedPlans.add(expectedPlan1);

        List<PlanPair> expectedPlan2 =
                List.of(
                        new PlanPair(instanceData.workflow().get("task01"), instanceData.hosts().get("HostA")),
                        new PlanPair(instanceData.workflow().get("task02"), instanceData.hosts().get("HostA")),
                        new PlanPair(instanceData.workflow().get("task06"), instanceData.hosts().get("HostB")),
                        new PlanPair(instanceData.workflow().get("task04"), instanceData.hosts().get("HostA")),
                        new PlanPair(instanceData.workflow().get("task05"), instanceData.hosts().get("HostC")),
                        new PlanPair(instanceData.workflow().get("task03"), instanceData.hosts().get("HostC")),
                        new PlanPair(instanceData.workflow().get("task07"), instanceData.hosts().get("HostA")),
                        new PlanPair(instanceData.workflow().get("task09"), instanceData.hosts().get("HostB")),
                        new PlanPair(instanceData.workflow().get("task08"), instanceData.hosts().get("HostC")),
                        new PlanPair(instanceData.workflow().get("task10"), instanceData.hosts().get("HostC")));

        expectedPlans.add(expectedPlan2);

        List<PlanPair> expectedPlan3 =
                List.of(
                        new PlanPair(instanceData.workflow().get("task01"), instanceData.hosts().get("HostA")),
                        new PlanPair(instanceData.workflow().get("task02"), instanceData.hosts().get("HostA")),
                        new PlanPair(instanceData.workflow().get("task04"), instanceData.hosts().get("HostA")),
                        new PlanPair(instanceData.workflow().get("task06"), instanceData.hosts().get("HostB")),
                        new PlanPair(instanceData.workflow().get("task05"), instanceData.hosts().get("HostC")),
                        new PlanPair(instanceData.workflow().get("task03"), instanceData.hosts().get("HostC")),
                        new PlanPair(instanceData.workflow().get("task07"), instanceData.hosts().get("HostA")),
                        new PlanPair(instanceData.workflow().get("task09"), instanceData.hosts().get("HostB")),
                        new PlanPair(instanceData.workflow().get("task08"), instanceData.hosts().get("HostC")),
                        new PlanPair(instanceData.workflow().get("task10"), instanceData.hosts().get("HostC")));

        expectedPlans.add(expectedPlan3);

        List<PlanPair> expectedPlan4 =
                List.of(
                        new PlanPair(instanceData.workflow().get("task01"), instanceData.hosts().get("HostA")),
                        new PlanPair(instanceData.workflow().get("task02"), instanceData.hosts().get("HostA")),
                        new PlanPair(instanceData.workflow().get("task04"), instanceData.hosts().get("HostA")),
                        new PlanPair(instanceData.workflow().get("task05"), instanceData.hosts().get("HostC")),
                        new PlanPair(instanceData.workflow().get("task06"), instanceData.hosts().get("HostB")),
                        new PlanPair(instanceData.workflow().get("task03"), instanceData.hosts().get("HostC")),
                        new PlanPair(instanceData.workflow().get("task07"), instanceData.hosts().get("HostA")),
                        new PlanPair(instanceData.workflow().get("task09"), instanceData.hosts().get("HostB")),
                        new PlanPair(instanceData.workflow().get("task08"), instanceData.hosts().get("HostC")),
                        new PlanPair(instanceData.workflow().get("task10"), instanceData.hosts().get("HostC")));

        expectedPlans.add(expectedPlan4);

        List<PlanPair> expectedPlan5 =
                List.of(
                        new PlanPair(instanceData.workflow().get("task01"), instanceData.hosts().get("HostA")),
                        new PlanPair(instanceData.workflow().get("task02"), instanceData.hosts().get("HostA")),
                        new PlanPair(instanceData.workflow().get("task04"), instanceData.hosts().get("HostA")),
                        new PlanPair(instanceData.workflow().get("task05"), instanceData.hosts().get("HostC")),
                        new PlanPair(instanceData.workflow().get("task03"), instanceData.hosts().get("HostC")),
                        new PlanPair(instanceData.workflow().get("task07"), instanceData.hosts().get("HostA")),
                        new PlanPair(instanceData.workflow().get("task06"), instanceData.hosts().get("HostB")),
                        new PlanPair(instanceData.workflow().get("task09"), instanceData.hosts().get("HostB")),
                        new PlanPair(instanceData.workflow().get("task08"), instanceData.hosts().get("HostC")),
                        new PlanPair(instanceData.workflow().get("task10"), instanceData.hosts().get("HostC")));

        expectedPlans.add(expectedPlan5);

        List<PlanPair> expectedPlan6 =
                List.of(
                        new PlanPair(instanceData.workflow().get("task01"), instanceData.hosts().get("HostA")),
                        new PlanPair(instanceData.workflow().get("task02"), instanceData.hosts().get("HostA")),
                        new PlanPair(instanceData.workflow().get("task04"), instanceData.hosts().get("HostA")),
                        new PlanPair(instanceData.workflow().get("task05"), instanceData.hosts().get("HostC")),
                        new PlanPair(instanceData.workflow().get("task03"), instanceData.hosts().get("HostC")),
                        new PlanPair(instanceData.workflow().get("task07"), instanceData.hosts().get("HostA")),
                        new PlanPair(instanceData.workflow().get("task09"), instanceData.hosts().get("HostB")),
                        new PlanPair(instanceData.workflow().get("task06"), instanceData.hosts().get("HostB")),
                        new PlanPair(instanceData.workflow().get("task08"), instanceData.hosts().get("HostC")),
                        new PlanPair(instanceData.workflow().get("task10"), instanceData.hosts().get("HostC")));

        expectedPlans.add(expectedPlan6);

        List<GeneratedNeighbor> generatedNeighbors = new NeighborhoodInsertionPositional().execute(originalSolution, position);

        for(int i = 0; i < generatedNeighbors.size(); i++)
            assertEquals(
                    expectedPlans.get(i),
                    generatedNeighbors.get(i).generatedSolution().getPlan()
            );

    }

    @Test
    void changeHostTest(){

        InstanceData instanceData = loadNeighborhoodOperatorsTest();

        int position = 5;

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

        SchedulePermutationSolution originalSolution = new SchedulePermutationSolution(0, 0, null, plan, "");

        List<List<PlanPair>> expectedPlans = new ArrayList<>();

        List<PlanPair> expectedPlan1 =
                List.of(
                        new PlanPair(instanceData.workflow().get("task01"), instanceData.hosts().get("HostA")),
                        new PlanPair(instanceData.workflow().get("task02"), instanceData.hosts().get("HostA")),
                        new PlanPair(instanceData.workflow().get("task04"), instanceData.hosts().get("HostA")),
                        new PlanPair(instanceData.workflow().get("task05"), instanceData.hosts().get("HostC")),
                        new PlanPair(instanceData.workflow().get("task03"), instanceData.hosts().get("HostC")),
                        new PlanPair(instanceData.workflow().get("task06"), instanceData.hosts().get("HostC")),
                        new PlanPair(instanceData.workflow().get("task07"), instanceData.hosts().get("HostA")),
                        new PlanPair(instanceData.workflow().get("task09"), instanceData.hosts().get("HostB")),
                        new PlanPair(instanceData.workflow().get("task08"), instanceData.hosts().get("HostC")),
                        new PlanPair(instanceData.workflow().get("task10"), instanceData.hosts().get("HostC")));

        expectedPlans.add(expectedPlan1);

        List<PlanPair> expectedPlan2 =
                List.of(
                        new PlanPair(instanceData.workflow().get("task01"), instanceData.hosts().get("HostA")),
                        new PlanPair(instanceData.workflow().get("task02"), instanceData.hosts().get("HostA")),
                        new PlanPair(instanceData.workflow().get("task04"), instanceData.hosts().get("HostA")),
                        new PlanPair(instanceData.workflow().get("task05"), instanceData.hosts().get("HostC")),
                        new PlanPair(instanceData.workflow().get("task03"), instanceData.hosts().get("HostC")),
                        new PlanPair(instanceData.workflow().get("task06"), instanceData.hosts().get("HostA")),
                        new PlanPair(instanceData.workflow().get("task07"), instanceData.hosts().get("HostA")),
                        new PlanPair(instanceData.workflow().get("task09"), instanceData.hosts().get("HostB")),
                        new PlanPair(instanceData.workflow().get("task08"), instanceData.hosts().get("HostC")),
                        new PlanPair(instanceData.workflow().get("task10"), instanceData.hosts().get("HostC")));

        expectedPlans.add(expectedPlan2);

        List<GeneratedNeighbor> generatedNeighbors = new NeighborhoodChangeHostPositional(instanceData).execute(originalSolution, position);

        for(int i = 0; i < generatedNeighbors.size(); i++)
            assertEquals(
                    expectedPlans.get(i),
                    generatedNeighbors.get(i).generatedSolution().getPlan()
            );

    }

    @Test
    void swapHostTest(){

        InstanceData instanceData = loadNeighborhoodOperatorsTest();

        int position = 5;

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

        SchedulePermutationSolution originalSolution = new SchedulePermutationSolution(0, 0, null, plan, "");

        List<List<PlanPair>> expectedPlans = new ArrayList<>();

        List<PlanPair> expectedPlan1 =
                List.of(
                        new PlanPair(instanceData.workflow().get("task01"), instanceData.hosts().get("HostA")),
                        new PlanPair(instanceData.workflow().get("task02"), instanceData.hosts().get("HostB")),
                        new PlanPair(instanceData.workflow().get("task04"), instanceData.hosts().get("HostA")),
                        new PlanPair(instanceData.workflow().get("task05"), instanceData.hosts().get("HostC")),
                        new PlanPair(instanceData.workflow().get("task03"), instanceData.hosts().get("HostC")),
                        new PlanPair(instanceData.workflow().get("task06"), instanceData.hosts().get("HostA")),
                        new PlanPair(instanceData.workflow().get("task07"), instanceData.hosts().get("HostA")),
                        new PlanPair(instanceData.workflow().get("task09"), instanceData.hosts().get("HostB")),
                        new PlanPair(instanceData.workflow().get("task08"), instanceData.hosts().get("HostC")),
                        new PlanPair(instanceData.workflow().get("task10"), instanceData.hosts().get("HostC")));

        expectedPlans.add(expectedPlan1);

        List<PlanPair> expectedPlan2 =
                List.of(
                        new PlanPair(instanceData.workflow().get("task01"), instanceData.hosts().get("HostA")),
                        new PlanPair(instanceData.workflow().get("task02"), instanceData.hosts().get("HostA")),
                        new PlanPair(instanceData.workflow().get("task04"), instanceData.hosts().get("HostB")),
                        new PlanPair(instanceData.workflow().get("task05"), instanceData.hosts().get("HostC")),
                        new PlanPair(instanceData.workflow().get("task03"), instanceData.hosts().get("HostC")),
                        new PlanPair(instanceData.workflow().get("task06"), instanceData.hosts().get("HostA")),
                        new PlanPair(instanceData.workflow().get("task07"), instanceData.hosts().get("HostA")),
                        new PlanPair(instanceData.workflow().get("task09"), instanceData.hosts().get("HostB")),
                        new PlanPair(instanceData.workflow().get("task08"), instanceData.hosts().get("HostC")),
                        new PlanPair(instanceData.workflow().get("task10"), instanceData.hosts().get("HostC")));

        expectedPlans.add(expectedPlan2);

        List<PlanPair> expectedPlan3 =
                List.of(
                        new PlanPair(instanceData.workflow().get("task01"), instanceData.hosts().get("HostA")),
                        new PlanPair(instanceData.workflow().get("task02"), instanceData.hosts().get("HostA")),
                        new PlanPair(instanceData.workflow().get("task04"), instanceData.hosts().get("HostA")),
                        new PlanPair(instanceData.workflow().get("task05"), instanceData.hosts().get("HostB")),
                        new PlanPair(instanceData.workflow().get("task03"), instanceData.hosts().get("HostC")),
                        new PlanPair(instanceData.workflow().get("task06"), instanceData.hosts().get("HostC")),
                        new PlanPair(instanceData.workflow().get("task07"), instanceData.hosts().get("HostA")),
                        new PlanPair(instanceData.workflow().get("task09"), instanceData.hosts().get("HostB")),
                        new PlanPair(instanceData.workflow().get("task08"), instanceData.hosts().get("HostC")),
                        new PlanPair(instanceData.workflow().get("task10"), instanceData.hosts().get("HostC")));

        expectedPlans.add(expectedPlan3);

        List<PlanPair> expectedPlan4 =
                List.of(
                        new PlanPair(instanceData.workflow().get("task01"), instanceData.hosts().get("HostA")),
                        new PlanPair(instanceData.workflow().get("task02"), instanceData.hosts().get("HostA")),
                        new PlanPair(instanceData.workflow().get("task04"), instanceData.hosts().get("HostA")),
                        new PlanPair(instanceData.workflow().get("task05"), instanceData.hosts().get("HostC")),
                        new PlanPair(instanceData.workflow().get("task03"), instanceData.hosts().get("HostB")),
                        new PlanPair(instanceData.workflow().get("task06"), instanceData.hosts().get("HostC")),
                        new PlanPair(instanceData.workflow().get("task07"), instanceData.hosts().get("HostA")),
                        new PlanPair(instanceData.workflow().get("task09"), instanceData.hosts().get("HostB")),
                        new PlanPair(instanceData.workflow().get("task08"), instanceData.hosts().get("HostC")),
                        new PlanPair(instanceData.workflow().get("task10"), instanceData.hosts().get("HostC")));

        expectedPlans.add(expectedPlan4);

        List<PlanPair> expectedPlan5 =
                List.of(
                        new PlanPair(instanceData.workflow().get("task01"), instanceData.hosts().get("HostA")),
                        new PlanPair(instanceData.workflow().get("task02"), instanceData.hosts().get("HostA")),
                        new PlanPair(instanceData.workflow().get("task04"), instanceData.hosts().get("HostA")),
                        new PlanPair(instanceData.workflow().get("task05"), instanceData.hosts().get("HostC")),
                        new PlanPair(instanceData.workflow().get("task03"), instanceData.hosts().get("HostC")),
                        new PlanPair(instanceData.workflow().get("task06"), instanceData.hosts().get("HostA")),
                        new PlanPair(instanceData.workflow().get("task07"), instanceData.hosts().get("HostB")),
                        new PlanPair(instanceData.workflow().get("task09"), instanceData.hosts().get("HostB")),
                        new PlanPair(instanceData.workflow().get("task08"), instanceData.hosts().get("HostC")),
                        new PlanPair(instanceData.workflow().get("task10"), instanceData.hosts().get("HostC")));

        expectedPlans.add(expectedPlan5);

        List<PlanPair> expectedPlan6 =
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

        expectedPlans.add(expectedPlan6);

        List<GeneratedNeighbor> generatedNeighbors = new NeighborhoodSwapHostPositional().execute(originalSolution, position);

        for(int i = 0; i < generatedNeighbors.size(); i++)
            assertEquals(
                    expectedPlans.get(i),
                    generatedNeighbors.get(i).generatedSolution().getPlan()
            );

    }

}
