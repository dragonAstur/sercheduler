package com.uniovi.sercheduler.localsearch.strategy;

import com.uniovi.sercheduler.jmetal.problem.SchedulePermutationSolution;
import com.uniovi.sercheduler.jmetal.problem.SchedulingProblem;
import com.uniovi.sercheduler.localsearch.evaluator.LocalsearchEvaluator;
import com.uniovi.sercheduler.localsearch.observer.NeighborhoodObserver;
import com.uniovi.sercheduler.localsearch.operator.GeneratedNeighbor;
import com.uniovi.sercheduler.localsearch.operator.NeighborhoodOperatorLazy;
import com.uniovi.sercheduler.service.FitnessCalculatorSimple;
import com.uniovi.sercheduler.service.FitnessInfo;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

public class SimpleClimbingStrategy extends AbstractStrategy {

    public SimpleClimbingStrategy(NeighborhoodObserver observer) {
        super(observer);
    }

    public SchedulePermutationSolution execute(SchedulingProblem problem, NeighborhoodOperatorLazy neighborhoodLazyOperator){

        getObserver().newIteration();
        int localSearchIterations = 0;
        int numberOfGeneratedNeighborsSum = 0;
        long startingTime = System.currentTimeMillis();

        //Generate an inicial random solution
        SchedulePermutationSolution actualSolution = problem.createSolution();

        //Evaluate this new created solution (this step is skipped in the pseudocode)
        FitnessCalculatorSimple fitnessCalculator = new FitnessCalculatorSimple(problem.getInstanceData());
        FitnessInfo fitnessInfo = fitnessCalculator.calculateFitness(actualSolution);
        actualSolution.setFitnessInfo(fitnessInfo);

        //Initialize the control variable, a variable for storing their neighbors and a
        boolean upgradeFound;
        Stream<GeneratedNeighbor> neighbors;
        Optional<GeneratedNeighbor> maybeBetterNeighbor;
        LocalsearchEvaluator evaluator = new LocalsearchEvaluator(fitnessCalculator.getComputationMatrix(), fitnessCalculator.getNetworkMatrix(), problem.getInstanceData());

        do{

            localSearchIterations++;

            upgradeFound = false;

            //Lazy computation of all the neighbors
            neighbors = neighborhoodLazyOperator.execute(actualSolution);

            final SchedulePermutationSolution finalActualSolution = actualSolution; //This is just for functional programming technical problems

            //As we are using laziness, for counting how many neighbours have been really computed, we cannot use
            //a variable that is no effectively final, so we use a Java class called 'AtomicInteger'
            AtomicInteger counter = new AtomicInteger();

            //Find the first neighbor that is better than the source
            maybeBetterNeighbor = neighbors
                    .filter(neighbor -> {

                        counter.incrementAndGet();

                        evaluator.evaluate(finalActualSolution, neighbor.generatedSolution(), neighbor.movements().get(neighbor.movements().size() - 1));

                        return neighbor.generatedSolution().getFitnessInfo().fitness().get("makespan") <
                                finalActualSolution.getFitnessInfo().fitness().get("makespan");
                    })
                    .findFirst();   //This find first is the laziness core

            numberOfGeneratedNeighborsSum += counter.get();

            //If there is an improvement, record it and update the best neighbor
            if (maybeBetterNeighbor.isPresent()) {
                actualSolution = maybeBetterNeighbor.get().generatedSolution();
                upgradeFound = true;
            }

        } while(upgradeFound);

        getObserver().setExecutingTime(System.currentTimeMillis() - startingTime);
        getObserver().setLocalSearchIterations(localSearchIterations);
        getObserver().setReachedCost(actualSolution.getFitnessInfo().fitness().get("makespan"));
        getObserver().setAvgNeighborsNumber(numberOfGeneratedNeighborsSum * 1.0 / localSearchIterations);

        return actualSolution;

    }

}
