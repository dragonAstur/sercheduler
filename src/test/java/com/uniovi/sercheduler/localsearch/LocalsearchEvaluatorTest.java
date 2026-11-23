package com.uniovi.sercheduler.localsearch;

import com.uniovi.sercheduler.dto.InstanceData;
import com.uniovi.sercheduler.jmetal.problem.SchedulePermutationSolution;
import com.uniovi.sercheduler.localsearch.algorithms.localsearchcomponents.AcceptanceCriterion;
import com.uniovi.sercheduler.localsearch.algorithms.localsearchcomponents.AcceptanceCriterionImpl;
import com.uniovi.sercheduler.localsearch.algorithms.localsearchcomponents.NeighborSelectorImpl;
import com.uniovi.sercheduler.localsearch.algorithms.localsearchcomponents.TerminationCriterion;
import com.uniovi.sercheduler.localsearch.evaluator.LocalsearchEvaluator;
import com.uniovi.sercheduler.localsearch.movement.Movement;
import com.uniovi.sercheduler.localsearch.observer.Observer;
import com.uniovi.sercheduler.localsearch.operator.*;
import com.uniovi.sercheduler.service.FitnessInfo;
import com.uniovi.sercheduler.service.PlanGenerator;
import com.uniovi.sercheduler.service.PlanPair;
import com.uniovi.sercheduler.service.calculator.FitnessCalculator;
import com.uniovi.sercheduler.service.calculator.FitnessCalculatorSimple;
import com.uniovi.sercheduler.util.UnitParser;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Stream;

import static com.uniovi.sercheduler.util.LoadLocalsearchTestInstanceData.loadNeighborhoodOperatorsTest;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

public class LocalsearchEvaluatorTest {

    @Test
    void preparedMakespanMatchesFullEvaluationForAllMovements() {
        InstanceData data = loadNeighborhoodOperatorsTest();
        FitnessCalculator calculator = new FitnessCalculatorSimple(data);
        LocalsearchEvaluator evaluator = new LocalsearchEvaluator(
                calculator.getComputationMatrix(), calculator.getNetworkMatrix(), data);
        List<NeighborhoodOperatorLazy> operators = List.of(
                new NeighborhoodChangeHostLazy(data), new NeighborhoodInsertionLazy(),
                new NeighborhoodSwapLazy(), new NeighborhoodSwapHostLazy());

        for (int seed = 0; seed < 5; seed++) {
            List<PlanPair> plan = new PlanGenerator(new Random(seed), data).generatePlan();
            SchedulePermutationSolution original = new SchedulePermutationSolution(plan.size(), 2, null, plan, "makespan");
            original.setFitnessInfo(calculator.calculateFitness(original));
            var prepared = evaluator.prepare(original);

            for (NeighborhoodOperatorLazy operator : operators) {
                List<GeneratedNeighbor> neighbors = operator.execute(original).toList();
                assertTrue(!neighbors.isEmpty(), operator.getName());
                for (GeneratedNeighbor neighbor : neighbors) {
                    double makespan = evaluator.computeMakespan(prepared, neighbor.generatedSolution().getPlan(), neighbor.movement());
                    evaluator.evaluate(original, neighbor.generatedSolution(), neighbor.movement());
                    assertEquals(neighbor.generatedSolution().getFitnessInfo().fitness().get("makespan"),
                            makespan, operator.getName());
                }
            }
        }
    }

    @Test
    void lazySelectionOnlyMaterializesAcceptedNeighborAndSupportsCustomCriteria() {
        InstanceData data = loadNeighborhoodOperatorsTest();
        FitnessCalculator calculator = new FitnessCalculatorSimple(data);
        LocalsearchEvaluator evaluator = new LocalsearchEvaluator(
                calculator.getComputationMatrix(), calculator.getNetworkMatrix(), data);
        List<PlanPair> plan = new PlanGenerator(new Random(5), data).generatePlan();
        SchedulePermutationSolution original = new SchedulePermutationSolution(plan.size(), 2, null, plan, "makespan");
        original.setFitnessInfo(calculator.calculateFitness(original));
        AcceptanceCriterionImpl criterion = new AcceptanceCriterionImpl();
        List<GeneratedNeighbor> candidates = Stream.of(
                        new NeighborhoodChangeHostLazy(data), new NeighborhoodInsertionLazy(),
                        new NeighborhoodSwapLazy(), new NeighborhoodSwapHostLazy())
                .flatMap(operator -> operator.execute(original)).toList();
        GeneratedNeighbor rejected = null;
        GeneratedNeighbor accepted = null;
        for (GeneratedNeighbor candidate : candidates) {
            evaluator.evaluate(original, candidate.generatedSolution(), candidate.movement());
            if (criterion.checkAcceptance(original, candidate.generatedSolution())) {
                if (accepted == null) accepted = candidate;
            } else if (rejected == null) {
                rejected = candidate;
            }
        }
        assertNotNull(rejected);
        assertNotNull(accepted);
        double expectedMakespan = accepted.generatedSolution().getFitnessInfo().fitness().get("makespan");
        rejected.generatedSolution().setFitnessInfo(null);
        accepted.generatedSolution().setFitnessInfo(null);

        NeighborSelectorImpl selector = new NeighborSelectorImpl();
        var result = selector.selectBestNeighborLazy(original, Stream.of(rejected, accepted), evaluator, criterion,
                mock(TerminationCriterion.class), mock(Observer.class));
        assertEquals(accepted, result.orElseThrow());
        assertNull(rejected.generatedSolution().getFitnessInfo());
        assertEquals(expectedMakespan, accepted.generatedSolution().getFitnessInfo().fitness().get("makespan"));
        assertEquals(expectedMakespan, accepted.generatedSolution().objectives()[0]);
        assertEquals(plan.size(), accepted.generatedSolution().getFitnessInfo().schedule().size());

        rejected.generatedSolution().setFitnessInfo(null);
        AcceptanceCriterion customCriterion = (current, candidate) -> {
            assertNotNull(candidate.getFitnessInfo());
            return true;
        };
        assertEquals(rejected, selector.selectBestNeighborLazy(original, Stream.of(rejected), evaluator, customCriterion,
                mock(TerminationCriterion.class), mock(Observer.class)).orElseThrow());

        rejected.generatedSolution().setFitnessInfo(null);
        LocalsearchEvaluator customEvaluator = new LocalsearchEvaluator(
                calculator.getComputationMatrix(), calculator.getNetworkMatrix(), data) {
            @Override
            public void evaluate(SchedulePermutationSolution source, SchedulePermutationSolution candidate,
                                 Movement movement) {
                super.evaluate(source, candidate, movement);
            }
        };
        assertTrue(selector.selectBestNeighborLazy(original, Stream.of(rejected), customEvaluator, criterion,
                mock(TerminationCriterion.class), mock(Observer.class)).isEmpty());
        assertNotNull(rejected.generatedSolution().getFitnessInfo());

        AcceptanceCriterionImpl derivedCriterion = new AcceptanceCriterionImpl() {
            @Override
            public boolean checkAcceptance(SchedulePermutationSolution current, SchedulePermutationSolution candidate) {
                return false;
            }
        };
        accepted.generatedSolution().setFitnessInfo(null);
        assertTrue(selector.selectBestNeighborLazy(original, Stream.of(accepted), evaluator, derivedCriterion,
                mock(TerminationCriterion.class), mock(Observer.class)).isEmpty());
        assertNotNull(accepted.generatedSolution().getFitnessInfo());
    }

    @Test
    void evaluateSwapTest() {

        //Load data and create a new solution

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

        SchedulePermutationSolution originalSolution = new SchedulePermutationSolution(1,2,null, plan,"makespan");

        //Create a calculator

        FitnessCalculator fitnessCalculator = new FitnessCalculatorSimple(instanceData);

        Map<String, Map<String, Double>> computationMatrix = fitnessCalculator.calculateComputationMatrix(UnitParser.parseUnits("1Gf"));

        Map<String, Map<String, Long>> networkMatrix = fitnessCalculator.calculateNetworkMatrix();

        //Evaluate the original solution

        FitnessInfo fitnessInfo = fitnessCalculator.calculateFitness(originalSolution);

        originalSolution.setFitnessInfo(fitnessInfo);

        //Generate new solutions and create the local search evaluator

        List<GeneratedNeighbor> generatedNeighbors = new NeighborhoodSwapPositional().execute(originalSolution, 5);

        LocalsearchEvaluator localsearchEvaluator = new LocalsearchEvaluator(computationMatrix, networkMatrix, instanceData);

        //Asserts

        double originalSolutionMakespan = originalSolution.getFitnessInfo().fitness().get("makespan");

        for(GeneratedNeighbor gn : generatedNeighbors){

            double generatedSolutionMakespan = fitnessCalculator.calculateFitness(gn.generatedSolution()).fitness().get("makespan");

            double makespanDifference = localsearchEvaluator.computeMakespanEnhancement(originalSolution, gn.generatedSolution(), gn.movement());


            //System.out.println("\n\nNEW GENERATED NEIGHBOR\n\tOriginal solution makespan: " + originalSolutionMakespan + "\n\tActual solution makespan: " + generatedSolutionMakespan + "\n\tDifference: " + makespanDifference + "\n\t\n\n");

            assertEquals(
                    originalSolutionMakespan - generatedSolutionMakespan,
                    makespanDifference
            );

        }

    }

    @Test
    void evaluateInsertionTest() {

        //Load data and create a new solution

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

        SchedulePermutationSolution originalSolution = new SchedulePermutationSolution(1,2,null, plan,"makespan");

        //Create a calculator

        FitnessCalculator fitnessCalculator = new FitnessCalculatorSimple(instanceData);

        Map<String, Map<String, Double>> computationMatrix = fitnessCalculator.calculateComputationMatrix(UnitParser.parseUnits("1Gf"));

        Map<String, Map<String, Long>> networkMatrix = fitnessCalculator.calculateNetworkMatrix();

        //Evaluate the original solution

        FitnessInfo fitnessInfo = fitnessCalculator.calculateFitness(originalSolution);

        originalSolution.setFitnessInfo(fitnessInfo);

        //Generate new solutions and create the local search evaluator

        List<GeneratedNeighbor> generatedNeighbors = new NeighborhoodInsertionPositional().execute(originalSolution, 5);

        LocalsearchEvaluator localsearchEvaluator = new LocalsearchEvaluator(computationMatrix, networkMatrix, instanceData);

        //Asserts

        double originalSolutionMakespan = originalSolution.getFitnessInfo().fitness().get("makespan");

        for(GeneratedNeighbor gn : generatedNeighbors){

            double generatedSolutionMakespan = fitnessCalculator.calculateFitness(gn.generatedSolution()).fitness().get("makespan");

            double makespanDifference = localsearchEvaluator.computeMakespanEnhancement(originalSolution, gn.generatedSolution(), gn.movement());


            //System.out.println("\n\nNEW GENERATED NEIGHBOR\n\tOriginal solution makespan: " + originalSolutionMakespan + "\n\tActual solution makespan: " + generatedSolutionMakespan + "\n\tDifference: " + makespanDifference + "\n\t\n\n");

            assertEquals(
                    originalSolutionMakespan - generatedSolutionMakespan,
                    makespanDifference
            );

        }

    }

    @Test
    void evaluateChangeHostTest() {

        //Load data and create a new solution

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

        SchedulePermutationSolution originalSolution = new SchedulePermutationSolution(1,2,null, plan,"makespan");

        //Create a calculator

        FitnessCalculator fitnessCalculator = new FitnessCalculatorSimple(instanceData);

        Map<String, Map<String, Double>> computationMatrix = fitnessCalculator.getComputationMatrix();

        Map<String, Map<String, Long>> networkMatrix = fitnessCalculator.getNetworkMatrix();

        //Evaluate the original solution

        FitnessInfo fitnessInfo = fitnessCalculator.calculateFitness(originalSolution);

        originalSolution.setFitnessInfo(fitnessInfo);

        //Generate new solutions and create the local search evaluator

        List<GeneratedNeighbor> generatedNeighbors = new NeighborhoodChangeHostPositional(instanceData).execute(originalSolution, 5);

        LocalsearchEvaluator localsearchEvaluator = new LocalsearchEvaluator(computationMatrix, networkMatrix, instanceData);

        //Asserts

        double originalSolutionMakespan = originalSolution.getFitnessInfo().fitness().get("makespan");

        for(GeneratedNeighbor gn : generatedNeighbors){

            double generatedSolutionMakespan = fitnessCalculator.calculateFitness(gn.generatedSolution()).fitness().get("makespan");

            double makespanDifference = localsearchEvaluator.computeMakespanEnhancement(originalSolution, gn.generatedSolution(), gn.movement());


            //System.out.println("\n\nNEW GENERATED NEIGHBOR\n\tOriginal solution makespan: " + originalSolutionMakespan + "\n\tActual solution makespan: " + generatedSolutionMakespan + "\n\tDifference: " + makespanDifference + "\n\t\n\n");

            assertEquals(
                    originalSolutionMakespan - generatedSolutionMakespan,
                    makespanDifference
            );

        }

    }

    @Test
    void evaluateSwapHostTest() {

        //Load data and create a new solution

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

        SchedulePermutationSolution originalSolution = new SchedulePermutationSolution(1,2,null, plan,"makespan");

        //Create a calculator

        FitnessCalculator fitnessCalculator = new FitnessCalculatorSimple(instanceData);

        Map<String, Map<String, Double>> computationMatrix = fitnessCalculator.getComputationMatrix();

        Map<String, Map<String, Long>> networkMatrix = fitnessCalculator.getNetworkMatrix();

        //Evaluate the original solution

        FitnessInfo fitnessInfo = fitnessCalculator.calculateFitness(originalSolution);

        originalSolution.setFitnessInfo(fitnessInfo);

        //Generate new solutions and create the local search evaluator

        List<GeneratedNeighbor> generatedNeighbors = new NeighborhoodSwapHostPositional().execute(originalSolution, 5);

        LocalsearchEvaluator localsearchEvaluator = new LocalsearchEvaluator(computationMatrix, networkMatrix, instanceData);

        //Asserts

        double originalSolutionMakespan = originalSolution.getFitnessInfo().fitness().get("makespan");

        for(GeneratedNeighbor gn : generatedNeighbors){

            double generatedSolutionMakespan = fitnessCalculator.calculateFitness(gn.generatedSolution()).fitness().get("makespan");

            double makespanDifference = localsearchEvaluator.computeMakespanEnhancement(originalSolution, gn.generatedSolution(), gn.movement());


            //System.out.println("\n\nNEW GENERATED NEIGHBOR\n\tOriginal solution makespan: " + originalSolutionMakespan + "\n\tActual solution makespan: " + generatedSolutionMakespan + "\n\tDifference: " + makespanDifference + "\n\t\n\n");

            assertEquals(
                    originalSolutionMakespan - generatedSolutionMakespan,
                    makespanDifference
            );

        }

    }
}
