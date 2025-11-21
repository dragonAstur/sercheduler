package com.uniovi.sercheduler.service.core;

import com.uniovi.sercheduler.dto.InstanceData;
import com.uniovi.sercheduler.jmetal.problem.SchedulePermutationSolution;
import com.uniovi.sercheduler.service.FitnessInfo;
import com.uniovi.sercheduler.service.PlanPair;
import com.uniovi.sercheduler.service.calculator.FitnessCalculator;
import com.uniovi.sercheduler.service.calculator.FitnessCalculatorSimple;
import org.junit.jupiter.api.Test;

import java.util.List;

import static com.uniovi.sercheduler.util.LoadTestInstanceData.loadFitnessTest;
import static org.junit.jupiter.api.Assertions.assertEquals;

class RandomKeysCoderTest {

    @Test
    void codeAndDecodeTest() {

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

        RandomKeysCoder randomKeysCoder = new RandomKeysCoder(instanceData);

        var particle = randomKeysCoder.encode(plan);

        var newPlan = randomKeysCoder.decode(particle);

        assertEquals(plan, newPlan);

        FitnessInfo result =
                fitnessCalculator.calculateFitness(new SchedulePermutationSolution(1,2,null, newPlan,"makespan"));

        assertEquals(210D, result.fitness().get("makespan"));
        assertEquals (679.65D, result.fitness().get("energy"), 1e-10);
    }
}
