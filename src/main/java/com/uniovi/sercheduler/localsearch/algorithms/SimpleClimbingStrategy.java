package com.uniovi.sercheduler.localsearch.algorithms;

import com.uniovi.sercheduler.jmetal.problem.SchedulePermutationSolution;
import com.uniovi.sercheduler.jmetal.problem.SchedulingProblem;
import com.uniovi.sercheduler.localsearch.algorithms.localsearchalgorithm.LocalSearchAlgorithm;
import com.uniovi.sercheduler.localsearch.algorithms.localsearchcomponents.UpgradeAndTimeLimitTermination;
import com.uniovi.sercheduler.localsearch.algorithms.multistart.MultiStartLocalSearch;
import com.uniovi.sercheduler.localsearch.observer.LocalSearchObserver;
import com.uniovi.sercheduler.localsearch.operator.NeighborhoodOperatorLazy;
import com.uniovi.sercheduler.localsearch.algorithms.multistartcomponents.AllStartOperatorSelector;
import com.uniovi.sercheduler.localsearch.algorithms.multistartcomponents.RandomStartOperatorSelector;

import java.util.*;

public class SimpleClimbingStrategy extends AbstractStrategy {

    public SimpleClimbingStrategy(LocalSearchObserver observer) {
        super(observer);
    }

    public SchedulePermutationSolution execute(SchedulingProblem problem, NeighborhoodOperatorLazy neighborhoodLazyOperator){

        return execute(problem, List.of(neighborhoodLazyOperator));

    }

    public SchedulePermutationSolution execute(SchedulingProblem problem, List<NeighborhoodOperatorLazy> neighborhoodLazyOperatorList){

        LocalSearchAlgorithm localSearchAlgorithm = new LocalSearchAlgorithm.Builder(problem).build();

        long startingTime = localSearchAlgorithm.startTimeCounter();

        SchedulePermutationSolution achievedSolution =
                localSearchAlgorithm.runLocalSearchLazy(neighborhoodLazyOperatorList, getObserver());

        getObserver().endRun(System.currentTimeMillis() - startingTime);

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
