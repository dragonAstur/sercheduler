package com.uniovi.sercheduler.localsearch.algorithms;

import com.uniovi.sercheduler.jmetal.problem.SchedulePermutationSolution;
import com.uniovi.sercheduler.jmetal.problem.SchedulingProblem;
import com.uniovi.sercheduler.localsearch.algorithms.localsearchalgorithm.LocalSearchAlgorithm;
import com.uniovi.sercheduler.localsearch.algorithms.localsearchcomponents.UpgradeAndTimeLimitTermination;
import com.uniovi.sercheduler.localsearch.algorithms.multistart.MultiStartLocalSearch;
import com.uniovi.sercheduler.localsearch.observer.Observer;
import com.uniovi.sercheduler.localsearch.operator.NeighborhoodOperatorLazy;
import com.uniovi.sercheduler.localsearch.algorithms.multistartcomponents.AllOperatorSelector;
import com.uniovi.sercheduler.localsearch.algorithms.multistartcomponents.RandomOperatorSelector;

import java.util.*;

public class SimpleClimbingStrategy {


    public SchedulePermutationSolution execute(SchedulingProblem problem, NeighborhoodOperatorLazy neighborhoodLazyOperator,
                                               Observer observer){

        return execute(problem, List.of(neighborhoodLazyOperator), observer);

    }

    public SchedulePermutationSolution execute(SchedulingProblem problem,
                                               List<NeighborhoodOperatorLazy> neighborhoodLazyOperatorList,
                                               Observer observer){

        LocalSearchAlgorithm localSearchAlgorithm = new LocalSearchAlgorithm.Builder(problem).build();

        observer.startRun( localSearchAlgorithm.startTimeCounter() );

        SchedulePermutationSolution achievedSolution =
                localSearchAlgorithm.runLocalSearchLazy(neighborhoodLazyOperatorList, observer);

        observer.endRun();

        return achievedSolution;

    }

    public SchedulePermutationSolution execute(SchedulingProblem problem, NeighborhoodOperatorLazy neighborhoodLazyOperator,
                                               Long limitTime, Observer observer){

        return execute(problem, List.of(neighborhoodLazyOperator), limitTime, observer);

    }

    public SchedulePermutationSolution execute(SchedulingProblem problem, List<NeighborhoodOperatorLazy> neighborhoodLazyOperatorList,
                                               Long limitTime, Observer observer){

        LocalSearchAlgorithm localSearchAlgorithm = new LocalSearchAlgorithm.Builder(problem)
                .terminationCriterion(new UpgradeAndTimeLimitTermination(limitTime, 0))
                .build();

        MultiStartLocalSearch multiStartLocalSearch = new MultiStartLocalSearch(new AllOperatorSelector());

        return multiStartLocalSearch.executeLazy(localSearchAlgorithm, neighborhoodLazyOperatorList, limitTime, observer);

    }

    public SchedulePermutationSolution executeVNS(SchedulingProblem problem, List<NeighborhoodOperatorLazy> neighborhoodLazyOperatorList,
                                                  Long limitTime, Observer observer){

        LocalSearchAlgorithm localSearchAlgorithm = new LocalSearchAlgorithm.Builder(problem)
                .terminationCriterion(new UpgradeAndTimeLimitTermination(limitTime, 0))
                .build();

        MultiStartLocalSearch multiStartLocalSearch = new MultiStartLocalSearch(new RandomOperatorSelector());

        return multiStartLocalSearch.executeLazy(localSearchAlgorithm, neighborhoodLazyOperatorList, limitTime, observer);

    }





}
