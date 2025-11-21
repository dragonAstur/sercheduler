package com.uniovi.sercheduler.service.algorithms;

import com.uniovi.sercheduler.dao.Objective;
import com.uniovi.sercheduler.dto.InstanceData;
import com.uniovi.sercheduler.jmetal.problem.SchedulePermutationSolution;
import com.uniovi.sercheduler.util.NonDominatedChecker;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static com.uniovi.sercheduler.util.LoadTestInstanceData.loadFitnessTest;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MoHeftTest {


    @Test
    void calculateMoHeft() {
        InstanceData instanceData = loadFitnessTest();

        MoHeft moHeft = new MoHeft(instanceData);

       var solutions = moHeft.calculate(10);

       assertEquals(8, solutions.size());

        // Verify fitness values
        List<Map<String, Double>> expectedObjectives = List.of(
                Map.of("energy", 569.5, "makespan", 180.5),
                Map.of("energy", 368.9, "makespan", 263.5),
                Map.of("energy", 569.5, "makespan", 180.5), // duplicate of first
                Map.of("energy", 419.5, "makespan", 231.5),
                Map.of("energy", 511.8999999999999, "makespan", 216.5),
                Map.of("energy", 518.9, "makespan", 212.5),
                Map.of("energy", 562.5, "makespan", 184.5),
                Map.of("energy", 419.5, "makespan", 231.5) // duplicate of 4
        );

        for (int i = 0; i < solutions.size(); i++) {
            var fitness = solutions.get(i).getFitnessInfo().fitness();
            assertEquals(expectedObjectives.get(i).get("energy"), fitness.get(Objective.ENERGY.objectiveName), 1e-6);
            assertEquals(expectedObjectives.get(i).get("makespan"), fitness.get(Objective.MAKESPAN.objectiveName), 1e-6);
        }

        // Verify non-domination
        assertTrue(NonDominatedChecker.areAllNonDominated(solutions));

    }




}



