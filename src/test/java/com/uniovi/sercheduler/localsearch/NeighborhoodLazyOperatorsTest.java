package com.uniovi.sercheduler.localsearch;

import com.uniovi.sercheduler.dto.InstanceData;
import com.uniovi.sercheduler.jmetal.problem.SchedulePermutationSolution;
import com.uniovi.sercheduler.localsearch.operator.*;
import com.uniovi.sercheduler.service.PlanPair;
import org.jline.terminal.TerminalBuilder;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static com.uniovi.sercheduler.util.LoadLocalsearchTestInstanceData.loadNeighborhoodOperatorsTest;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class NeighborhoodLazyOperatorsTest {

    @Test
    void swapTest(){

        InstanceData instanceData = loadNeighborhoodOperatorsTest();

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

    @Test
    void insertionTest(){

        InstanceData instanceData = loadNeighborhoodOperatorsTest();

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

        List<GeneratedNeighbor> generatedNeighbors = new NeighborhoodInsertionGlobal().execute(originalSolution);

        List<GeneratedNeighbor> generatedNeighborsLazy = new NeighborhoodInsertionLazy().execute(originalSolution).toList();

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

    @Test
    void changeHostTest(){

        InstanceData instanceData = loadNeighborhoodOperatorsTest();

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

        List<GeneratedNeighbor> generatedNeighbors = new NeighborhoodChangeHostGlobal(instanceData).execute(originalSolution);

        List<GeneratedNeighbor> generatedNeighborsLazy = new NeighborhoodChangeHostLazy(instanceData).execute(originalSolution).toList();

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

    @Test
    void swapHostTest(){

        InstanceData instanceData = loadNeighborhoodOperatorsTest();

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

        List<GeneratedNeighbor> generatedNeighbors = new NeighborhoodSwapHostGlobal().execute(originalSolution);

        List<GeneratedNeighbor> generatedNeighborsLazy = new NeighborhoodSwapHostLazy().execute(originalSolution).toList();

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
