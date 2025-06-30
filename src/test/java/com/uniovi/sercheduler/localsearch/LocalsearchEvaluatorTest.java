package com.uniovi.sercheduler.localsearch;

import com.uniovi.sercheduler.dto.InstanceData;
import com.uniovi.sercheduler.jmetal.problem.SchedulePermutationSolution;
import com.uniovi.sercheduler.localsearch.evaluator.LocalsearchEvaluator;
import com.uniovi.sercheduler.localsearch.operator.*;
import com.uniovi.sercheduler.service.calculator.FitnessCalculator;
import com.uniovi.sercheduler.service.calculator.FitnessCalculatorSimple;
import com.uniovi.sercheduler.service.FitnessInfo;
import com.uniovi.sercheduler.service.PlanPair;
import com.uniovi.sercheduler.service.core.SchedulingHelper;
import com.uniovi.sercheduler.util.UnitParser;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static com.uniovi.sercheduler.util.LoadLocalsearchTestInstanceData.loadNeighborhoodOperatorsTest;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class LocalsearchEvaluatorTest {

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

        Map<String, Map<String, Double>> computationMatrix = SchedulingHelper.calculateComputationMatrix(instanceData, UnitParser.parseUnits("1Gf"));

        Map<String, Map<String, Long>> networkMatrix = SchedulingHelper.calculateNetworkMatrix(instanceData);

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

            double makespanDifference = localsearchEvaluator.computeMakespanEnhancement(originalSolution, gn.generatedSolution(), gn.movements().get(0));


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

        Map<String, Map<String, Double>> computationMatrix = SchedulingHelper.calculateComputationMatrix(instanceData, UnitParser.parseUnits("1Gf"));

        Map<String, Map<String, Long>> networkMatrix = SchedulingHelper.calculateNetworkMatrix(instanceData);

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

            double makespanDifference = localsearchEvaluator.computeMakespanEnhancement(originalSolution, gn.generatedSolution(), gn.movements().get(0));


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

            double makespanDifference = localsearchEvaluator.computeMakespanEnhancement(originalSolution, gn.generatedSolution(), gn.movements().get(0));


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

            double makespanDifference = localsearchEvaluator.computeMakespanEnhancement(originalSolution, gn.generatedSolution(), gn.movements().get(0));


            //System.out.println("\n\nNEW GENERATED NEIGHBOR\n\tOriginal solution makespan: " + originalSolutionMakespan + "\n\tActual solution makespan: " + generatedSolutionMakespan + "\n\tDifference: " + makespanDifference + "\n\t\n\n");

            assertEquals(
                    originalSolutionMakespan - generatedSolutionMakespan,
                    makespanDifference
            );

        }

    }
}
