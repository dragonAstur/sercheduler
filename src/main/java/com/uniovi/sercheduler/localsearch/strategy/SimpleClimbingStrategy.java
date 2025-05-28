package com.uniovi.sercheduler.localsearch.strategy;

import com.uniovi.sercheduler.jmetal.problem.SchedulePermutationSolution;
import com.uniovi.sercheduler.jmetal.problem.SchedulingProblem;
import com.uniovi.sercheduler.localsearch.observer.NeighborhoodObserver;
import com.uniovi.sercheduler.localsearch.operator.NeighborhoodOperatorLazy;
import com.uniovi.sercheduler.localsearch.strategy.neighborgenerator.NeighborGenerator;
import com.uniovi.sercheduler.localsearch.strategy.neighborgenerator.UnionNeighborGenerator;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class SimpleClimbingStrategy extends AbstractStrategy {

    public SimpleClimbingStrategy(NeighborhoodObserver observer) {
        super(observer);
    }

    public SchedulePermutationSolution execute(SchedulingProblem problem, NeighborhoodOperatorLazy neighborhoodLazyOperator){

        return execute(problem, List.of(neighborhoodLazyOperator));

    }

    public SchedulePermutationSolution execute(SchedulingProblem problem, List<NeighborhoodOperatorLazy> neighborhoodLazyOperatorList){

        getObserver().executionStarted();
        AtomicInteger localSearchIterations = new AtomicInteger();
        long startingTime = System.currentTimeMillis();

        NeighborGenerator unionNeighborGenerator = new UnionNeighborGenerator();


        SchedulePermutationSolution bestSolution = this.runLocalSearchLazy(problem, neighborhoodLazyOperatorList, Long.MAX_VALUE, startingTime, localSearchIterations, unionNeighborGenerator);

        getObserver().setExecutionTime(System.currentTimeMillis() - startingTime);
        getObserver().setNumberOfIterations(localSearchIterations.get());
        getObserver().setTotalBestMakespan(bestSolution.getFitnessInfo().fitness().get("makespan"));

        getObserver().executionEnded();

        return bestSolution;

    }

    public SchedulePermutationSolution execute(SchedulingProblem problem, NeighborhoodOperatorLazy neighborhoodLazyOperator, Long limitTime){

        return execute(problem, List.of(neighborhoodLazyOperator), limitTime);

    }

    public SchedulePermutationSolution execute(SchedulingProblem problem, List<NeighborhoodOperatorLazy> neighborhoodLazyOperatorList, Long limitTime){

        getObserver().executionStarted();
        AtomicInteger localSearchIterations = new AtomicInteger();
        long startingTime = System.currentTimeMillis();

        SchedulePermutationSolution totalBestNeighbor = null;
        double totalWorstMakespan = -1;

        NeighborGenerator unionNeighborGenerator = new UnionNeighborGenerator();


        do {

            SchedulePermutationSolution actualSolution = this.runLocalSearchLazy(problem, neighborhoodLazyOperatorList, limitTime, startingTime, localSearchIterations, unionNeighborGenerator);

            //If it is the first time, initialize the total best neighbor variable
            if(totalBestNeighbor == null)
                totalBestNeighbor = actualSolution;

            if(actualSolution.getFitnessInfo().fitness().get("makespan") < totalBestNeighbor.getFitnessInfo().fitness().get("makespan"))
                totalBestNeighbor = actualSolution;
            else if(actualSolution.getFitnessInfo().fitness().get("makespan") > totalWorstMakespan)
                totalWorstMakespan = actualSolution.getFitnessInfo().fitness().get("makespan");

            getObserver().addReachedMakespan(actualSolution.getFitnessInfo().fitness().get("makespan"));

        } while(System.currentTimeMillis() - startingTime < limitTime);


        getObserver().setTotalBestMakespan(totalBestNeighbor.getFitnessInfo().fitness().get("makespan"));
        getObserver().setTotalWorstMakespan(totalWorstMakespan);

        getObserver().setExecutionTime(System.currentTimeMillis() - startingTime);
        getObserver().setNumberOfIterations(localSearchIterations.get());

        getObserver().executionEnded();

        return totalBestNeighbor;

    }

    public SchedulePermutationSolution executeVNS(SchedulingProblem problem, List<NeighborhoodOperatorLazy> neighborhoodLazyOperatorList, Long limitTime){

        getObserver().executionStarted();
        AtomicInteger localSearchIterations = new AtomicInteger();
        long startingTime = System.currentTimeMillis();

        SchedulePermutationSolution totalBestNeighbor = null;
        double totalWorstMakespan = -1;

        NeighborGenerator unionNeighborGenerator = new UnionNeighborGenerator();

        Random random = new Random();
        NeighborhoodOperatorLazy chosenOperator;

        do {

            chosenOperator = neighborhoodLazyOperatorList.get( random.nextInt(0, neighborhoodLazyOperatorList.size()) );

            SchedulePermutationSolution actualSolution = runLocalSearchLazy(problem, chosenOperator, limitTime, startingTime, localSearchIterations, unionNeighborGenerator);

            //If it is the first time, initialize the total best neighbor variable
            if(totalBestNeighbor == null)
                totalBestNeighbor = actualSolution;

            if(actualSolution.getFitnessInfo().fitness().get("makespan") < totalBestNeighbor.getFitnessInfo().fitness().get("makespan"))
                totalBestNeighbor = actualSolution;
            else if(actualSolution.getFitnessInfo().fitness().get("makespan") > totalWorstMakespan)
                totalWorstMakespan = actualSolution.getFitnessInfo().fitness().get("makespan");

            getObserver().addReachedMakespan(actualSolution.getFitnessInfo().fitness().get("makespan"));

        } while(System.currentTimeMillis() - startingTime < limitTime);


        getObserver().setTotalBestMakespan(totalBestNeighbor.getFitnessInfo().fitness().get("makespan"));
        getObserver().setTotalWorstMakespan(totalWorstMakespan);

        getObserver().setExecutionTime(System.currentTimeMillis() - startingTime);
        getObserver().setNumberOfIterations(localSearchIterations.get());

        getObserver().executionEnded();

        return totalBestNeighbor;

    }





}
