package com.uniovi.sercheduler.localsearch;

import com.uniovi.sercheduler.dto.InstanceData;
import com.uniovi.sercheduler.jmetal.problem.SchedulePermutationSolution;
import com.uniovi.sercheduler.localsearch.operator.GeneratedNeighbor;
import com.uniovi.sercheduler.localsearch.operator.NeighborhoodSwapGlobal;
import com.uniovi.sercheduler.localsearch.operator.NeighborhoodSwapLazy;
import com.uniovi.sercheduler.localsearch.operator.NeighborhoodSwapPositional;
import com.uniovi.sercheduler.service.PlanPair;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static com.uniovi.sercheduler.util.LoadLocalsearchTestInstanceData.loadSpecificSwapTest;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class SpecificSwapOperatorsTest {

    @Test
    void swapTest() {

        InstanceData instanceData = loadSpecificSwapTest();

        int position = 3;

        List<PlanPair> plan =
                List.of(
                        new PlanPair(instanceData.workflow().get("task01"), instanceData.hosts().get("HostB")),
                        new PlanPair(instanceData.workflow().get("task03"), instanceData.hosts().get("HostA")),
                        new PlanPair(instanceData.workflow().get("task06"), instanceData.hosts().get("HostA")),
                        new PlanPair(instanceData.workflow().get("task02"), instanceData.hosts().get("HostB")),
                        new PlanPair(instanceData.workflow().get("task04"), instanceData.hosts().get("HostA")),
                        new PlanPair(instanceData.workflow().get("task05"), instanceData.hosts().get("HostA")));

        SchedulePermutationSolution originalSolution = new SchedulePermutationSolution(0, 0, null, plan, "");

        List<List<PlanPair>> expectedPlans = new ArrayList<>();

        List<PlanPair> expectedPlan1 =
                List.of(
                        new PlanPair(instanceData.workflow().get("task01"), instanceData.hosts().get("HostB")),
                        new PlanPair(instanceData.workflow().get("task03"), instanceData.hosts().get("HostA")),
                        new PlanPair(instanceData.workflow().get("task02"), instanceData.hosts().get("HostB")),
                        new PlanPair(instanceData.workflow().get("task06"), instanceData.hosts().get("HostA")),
                        new PlanPair(instanceData.workflow().get("task04"), instanceData.hosts().get("HostA")),
                        new PlanPair(instanceData.workflow().get("task05"), instanceData.hosts().get("HostA")));

        expectedPlans.add(expectedPlan1);

        List<GeneratedNeighbor> generatedNeighbors = new NeighborhoodSwapPositional().execute(originalSolution, position);

        assertEquals(expectedPlans.size(), generatedNeighbors.size());

        for (int i = 0; i < generatedNeighbors.size(); i++)
            assertEquals(
                    expectedPlans.get(i),
                    generatedNeighbors.get(i).generatedSolution().getPlan()
            );

    }



    @Test
    void swapLazyTest(){

        InstanceData instanceData = loadSpecificSwapTest();

        List<PlanPair> plan =
                List.of(
                        new PlanPair(instanceData.workflow().get("task01"), instanceData.hosts().get("HostB")),
                        new PlanPair(instanceData.workflow().get("task03"), instanceData.hosts().get("HostA")),
                        new PlanPair(instanceData.workflow().get("task06"), instanceData.hosts().get("HostA")),
                        new PlanPair(instanceData.workflow().get("task02"), instanceData.hosts().get("HostB")),
                        new PlanPair(instanceData.workflow().get("task04"), instanceData.hosts().get("HostA")),
                        new PlanPair(instanceData.workflow().get("task05"), instanceData.hosts().get("HostA")));

        SchedulePermutationSolution originalSolution = new SchedulePermutationSolution(0, 0, null, plan, "");

        List<GeneratedNeighbor> generatedNeighbors = new NeighborhoodSwapGlobal().execute(originalSolution);

        List<GeneratedNeighbor> generatedNeighborsLazy = new NeighborhoodSwapLazy().execute(originalSolution).toList();

        assertEquals(generatedNeighbors.size(), generatedNeighborsLazy.size());

        for(int i = 0; i < generatedNeighbors.size(); i++){
            var planGlobal = generatedNeighbors.get(i).generatedSolution().getPlan();
            var planLazy = generatedNeighborsLazy.get(i).generatedSolution().getPlan();
            assertEquals(planGlobal.size(), planLazy.size());
            for(int j = 0; j < planGlobal.size(); j++){
                assertEquals(planGlobal.get(j).task(), planLazy.get(j).task());
                assertEquals(planGlobal.get(j).host(), planLazy.get(j).host());
            }
        }

    }
}