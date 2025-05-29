package com.uniovi.sercheduler.localsearch.strategy;

import com.uniovi.sercheduler.jmetal.problem.SchedulePermutationSolution;
import com.uniovi.sercheduler.jmetal.problem.SchedulingProblem;
import com.uniovi.sercheduler.localsearch.evaluator.LocalsearchEvaluator;
import com.uniovi.sercheduler.localsearch.observer.NeighborhoodObserver;
import com.uniovi.sercheduler.localsearch.operator.NeighborhoodOperatorGlobal;
import com.uniovi.sercheduler.localsearch.operator.NeighborhoodOperatorLazy;
import com.uniovi.sercheduler.localsearch.strategy.neighborgenerator.NeighborGenerator;
import com.uniovi.sercheduler.localsearch.strategy.neighborgenerator.UnionNeighborGenerator;
import com.uniovi.sercheduler.localsearch.strategy.startoperatorselector.AllStartOperatorSelector;
import com.uniovi.sercheduler.localsearch.strategy.startoperatorselector.RandomStartOperatorSelector;
import com.uniovi.sercheduler.localsearch.strategy.startoperatorselector.StartOperatorSelector;
import com.uniovi.sercheduler.service.FitnessCalculator;
import com.uniovi.sercheduler.service.FitnessCalculatorSimple;
import com.uniovi.sercheduler.service.FitnessInfo;

import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

public class MaximumGradientStrategy extends AbstractStrategy {

    private final double UPGRADE_THRESHOLD = 0.01;

    public MaximumGradientStrategy(NeighborhoodObserver observer) {
        super(observer);
    }

    public SchedulePermutationSolution execute(SchedulingProblem problem, NeighborhoodOperatorGlobal neighborhoodOperator){

        return execute(problem, List.of(neighborhoodOperator));
    }

    public SchedulePermutationSolution execute(SchedulingProblem problem, List<NeighborhoodOperatorGlobal> neighborhoodOperatorList){

        getObserver().executionStarted();
        AtomicInteger localSearchIterations = new AtomicInteger();
        long startingTime = System.currentTimeMillis();

        NeighborGenerator unionNeighborGenerator = new UnionNeighborGenerator();

        SchedulePermutationSolution bestSolution = runLocalSearchGlobal(problem, neighborhoodOperatorList, Long.MAX_VALUE, startingTime, localSearchIterations, unionNeighborGenerator);

        getObserver().setExecutionTime(System.currentTimeMillis() - startingTime);
        getObserver().setNumberOfIterations(localSearchIterations.get());
        getObserver().setTotalBestMakespan(bestSolution.getFitnessInfo().fitness().get("makespan"));

        getObserver().executionEnded();

        return bestSolution;
    }

    public SchedulePermutationSolution execute(SchedulingProblem problem, NeighborhoodOperatorGlobal neighborhoodOperator, Long limitTime){

        return execute(problem, List.of(neighborhoodOperator), limitTime);
    }

    public SchedulePermutationSolution execute(SchedulingProblem problem, List<NeighborhoodOperatorGlobal> neighborhoodOperatorList, Long limitTime){

        getObserver().executionStarted();
        AtomicInteger localSearchIterations = new AtomicInteger();
        long startingTime = System.currentTimeMillis();

        SchedulePermutationSolution totalBestNeighbor = null;
        double totalWorstMakespan = -1;

        NeighborGenerator unionNeighborGenerator = new UnionNeighborGenerator();

        List<NeighborhoodOperatorGlobal> chosenOperators;
        StartOperatorSelector operatorSelector = new RandomStartOperatorSelector();

        do {

            chosenOperators = operatorSelector.selectOperatorsGlobal(neighborhoodOperatorList);

            SchedulePermutationSolution actualSolution = runLocalSearchGlobal(problem, chosenOperators, limitTime, startingTime, localSearchIterations, unionNeighborGenerator);

            //If it is the first time, initialize the total best neighbor variable
            if(totalBestNeighbor == null)
                totalBestNeighbor = actualSolution;

            if(actualSolution.getFitnessInfo().fitness().get("makespan") < totalBestNeighbor.getFitnessInfo().fitness().get("makespan"))
                totalBestNeighbor = actualSolution;
            else if(actualSolution.getFitnessInfo().fitness().get("makespan") > totalWorstMakespan)
                totalWorstMakespan = actualSolution.getFitnessInfo().fitness().get("makespan");

        } while(System.currentTimeMillis() - startingTime < limitTime);

        getObserver().setTotalBestMakespan(totalBestNeighbor.getFitnessInfo().fitness().get("makespan"));
        getObserver().setTotalWorstMakespan(totalWorstMakespan);

        getObserver().setExecutionTime(System.currentTimeMillis() - startingTime);
        getObserver().setNumberOfIterations(localSearchIterations.get());

        getObserver().executionEnded();

        return totalBestNeighbor;
    }

    public SchedulePermutationSolution executeVNS(SchedulingProblem problem, List<NeighborhoodOperatorGlobal> neighborhoodOperatorList, Long limitTime){

        getObserver().executionStarted();
        AtomicInteger localSearchIterations = new AtomicInteger();
        long startingTime = System.currentTimeMillis();

        SchedulePermutationSolution totalBestNeighbor = null;
        double totalWorstMakespan = -1;

        NeighborGenerator unionNeighborGenerator = new UnionNeighborGenerator();

        List<NeighborhoodOperatorGlobal> chosenOperators;
        StartOperatorSelector operatorSelector = new RandomStartOperatorSelector();

        do {

            chosenOperators = operatorSelector.selectOperatorsGlobal(neighborhoodOperatorList);

            SchedulePermutationSolution actualSolution = runLocalSearchGlobal(problem, chosenOperators, limitTime, startingTime, localSearchIterations, unionNeighborGenerator);

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
