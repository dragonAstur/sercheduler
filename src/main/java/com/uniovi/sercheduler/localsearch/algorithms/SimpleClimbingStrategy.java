package com.uniovi.sercheduler.localsearch.algorithms;

import com.uniovi.sercheduler.jmetal.problem.SchedulePermutationSolution;
import com.uniovi.sercheduler.jmetal.problem.SchedulingProblem;
import com.uniovi.sercheduler.localsearch.algorithms.localsearchalgorithm.LocalSearchAlgorithm;
import com.uniovi.sercheduler.localsearch.algorithms.localsearchcomponents.UpgradeAndTimeLimitTermination;
import com.uniovi.sercheduler.localsearch.algorithms.multistart.MultiStartLocalSearch;
import com.uniovi.sercheduler.localsearch.observer.LocalSearchObserver;
import com.uniovi.sercheduler.localsearch.observer.Observer;
import com.uniovi.sercheduler.localsearch.operator.NeighborhoodOperatorLazy;
import com.uniovi.sercheduler.localsearch.algorithms.multistartcomponents.AllStartOperatorSelector;
import com.uniovi.sercheduler.localsearch.algorithms.multistartcomponents.RandomStartOperatorSelector;

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
                .terminationCriterion(new UpgradeAndTimeLimitTermination(limitTime))
                .build();

        MultiStartLocalSearch multiStartLocalSearch = new MultiStartLocalSearch(new AllStartOperatorSelector());

        return multiStartLocalSearch.executeLazy(localSearchAlgorithm, neighborhoodLazyOperatorList, limitTime, observer);

    }

    public SchedulePermutationSolution executeVNS(SchedulingProblem problem, List<NeighborhoodOperatorLazy> neighborhoodLazyOperatorList,
                                                  Long limitTime, Observer observer){

        LocalSearchAlgorithm localSearchAlgorithm = new LocalSearchAlgorithm.Builder(problem)
                .terminationCriterion(new UpgradeAndTimeLimitTermination(limitTime))
                .build();

        MultiStartLocalSearch multiStartLocalSearch = new MultiStartLocalSearch(new RandomStartOperatorSelector());

        return multiStartLocalSearch.executeLazy(localSearchAlgorithm, neighborhoodLazyOperatorList, limitTime, observer);

    }





}
