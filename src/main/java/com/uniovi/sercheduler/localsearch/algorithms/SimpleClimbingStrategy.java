package com.uniovi.sercheduler.localsearch.algorithms;

import com.uniovi.sercheduler.jmetal.problem.SchedulePermutationSolution;
import com.uniovi.sercheduler.jmetal.problem.SchedulingProblem;
import com.uniovi.sercheduler.localsearch.algorithms.localsearchalgorithm.LocalSearchAlgorithm;
import com.uniovi.sercheduler.localsearch.algorithms.localsearchcomponents.UpgradeAndTimeLimitTermination;
import com.uniovi.sercheduler.localsearch.algorithms.multistart.MultiStartLocalSearch;
import com.uniovi.sercheduler.localsearch.observer.NeighborhoodObserver;
import com.uniovi.sercheduler.localsearch.operator.NeighborhoodOperatorLazy;
import com.uniovi.sercheduler.localsearch.algorithms.localsearchcomponents.NeighborGenerator;
import com.uniovi.sercheduler.localsearch.algorithms.localsearchcomponents.NeighborGeneratorImpl;
import com.uniovi.sercheduler.localsearch.algorithms.multistartcomponents.AllStartOperatorSelector;
import com.uniovi.sercheduler.localsearch.algorithms.multistartcomponents.RandomStartOperatorSelector;
import com.uniovi.sercheduler.localsearch.algorithms.multistartcomponents.StartOperatorSelector;

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

        LocalSearchAlgorithm localSearchAlgorithm = new LocalSearchAlgorithm.Builder(problem).build();

        getObserver().executionStarted();
        AtomicInteger localSearchIterations = new AtomicInteger();
        long startingTime = localSearchAlgorithm.startTimeCounter();

        SchedulePermutationSolution achievedSolution =
                localSearchAlgorithm.runLocalSearchLazy(neighborhoodLazyOperatorList, localSearchIterations, getObserver());

        getObserver().setExecutionTime(System.currentTimeMillis() - startingTime);
        getObserver().setNumberOfIterations(localSearchIterations.get());
        getObserver().setTotalBestMakespan(achievedSolution.getFitnessInfo().fitness().get("makespan"));

        getObserver().executionEnded();

        return achievedSolution;

    }

    public SchedulePermutationSolution execute(SchedulingProblem problem, NeighborhoodOperatorLazy neighborhoodLazyOperator, Long limitTime){

        return execute(problem, List.of(neighborhoodLazyOperator), limitTime);

    }

    public SchedulePermutationSolution execute(SchedulingProblem problem, List<NeighborhoodOperatorLazy> neighborhoodLazyOperatorList, Long limitTime){

        LocalSearchAlgorithm localSearchAlgorithm = new LocalSearchAlgorithm.Builder(problem)
                .terminationCriterion(new UpgradeAndTimeLimitTermination(limitTime))
                .build();

        MultiStartLocalSearch multiStartLocalSearch = new MultiStartLocalSearch(new AllStartOperatorSelector());

        return multiStartLocalSearch.executeLazy(localSearchAlgorithm, neighborhoodLazyOperatorList, limitTime, getObserver());

    }

    public SchedulePermutationSolution executeVNS(SchedulingProblem problem, List<NeighborhoodOperatorLazy> neighborhoodLazyOperatorList, Long limitTime){

        LocalSearchAlgorithm localSearchAlgorithm = new LocalSearchAlgorithm.Builder(problem)
                .terminationCriterion(new UpgradeAndTimeLimitTermination(limitTime))
                .build();

        MultiStartLocalSearch multiStartLocalSearch = new MultiStartLocalSearch(new RandomStartOperatorSelector());

        return multiStartLocalSearch.executeLazy(localSearchAlgorithm, neighborhoodLazyOperatorList, limitTime, getObserver());

    }





}
