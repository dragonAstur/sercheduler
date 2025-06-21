package com.uniovi.sercheduler.localsearch.algorithms;

import com.uniovi.sercheduler.jmetal.problem.SchedulePermutationSolution;
import com.uniovi.sercheduler.jmetal.problem.SchedulingProblem;
import com.uniovi.sercheduler.localsearch.algorithms.localsearchalgorithm.LocalSearchAlgorithm;
import com.uniovi.sercheduler.localsearch.algorithms.localsearchcomponents.UpgradeAndTimeLimitTermination;
import com.uniovi.sercheduler.localsearch.algorithms.multistart.MultiStartLocalSearch;
import com.uniovi.sercheduler.localsearch.algorithms.multistartcomponents.AllStartOperatorSelector;
import com.uniovi.sercheduler.localsearch.observer.LocalSearchObserver;
import com.uniovi.sercheduler.localsearch.operator.NeighborhoodOperatorGlobal;
import com.uniovi.sercheduler.localsearch.algorithms.multistartcomponents.RandomStartOperatorSelector;

import java.util.List;

public class MaximumGradientStrategy extends AbstractStrategy {

    private final double UPGRADE_THRESHOLD = 0.01;

    public MaximumGradientStrategy(LocalSearchObserver observer) {
        super(observer);
    }

    public SchedulePermutationSolution execute(SchedulingProblem problem, NeighborhoodOperatorGlobal neighborhoodOperator){

        return execute(problem, List.of(neighborhoodOperator));
    }

    public SchedulePermutationSolution execute(SchedulingProblem problem, List<NeighborhoodOperatorGlobal> neighborhoodOperatorList){

        LocalSearchAlgorithm localSearchAlgorithm = new LocalSearchAlgorithm.Builder(problem).build();

        long startingTime = localSearchAlgorithm.startTimeCounter();

        SchedulePermutationSolution achievedSolution =
                localSearchAlgorithm.runLocalSearchGlobal(neighborhoodOperatorList, getObserver());

        getObserver().endRun(System.currentTimeMillis() - startingTime);

        return achievedSolution;
    }

    public SchedulePermutationSolution execute(SchedulingProblem problem, NeighborhoodOperatorGlobal neighborhoodOperator, Long limitTime){

        return execute(problem, List.of(neighborhoodOperator), limitTime);
    }

    public SchedulePermutationSolution execute(SchedulingProblem problem, List<NeighborhoodOperatorGlobal> neighborhoodOperatorList, Long limitTime){

        LocalSearchAlgorithm localSearchAlgorithm = new LocalSearchAlgorithm.Builder(problem)
                .terminationCriterion(new UpgradeAndTimeLimitTermination(limitTime))
                .build();

        MultiStartLocalSearch multiStartLocalSearch = new MultiStartLocalSearch(new AllStartOperatorSelector());

        return multiStartLocalSearch.executeGlobal(localSearchAlgorithm, neighborhoodOperatorList, limitTime, getObserver());

    }

    public SchedulePermutationSolution executeVNS(SchedulingProblem problem, List<NeighborhoodOperatorGlobal> neighborhoodOperatorList, Long limitTime){

        LocalSearchAlgorithm localSearchAlgorithm = new LocalSearchAlgorithm.Builder(problem)
                .terminationCriterion(new UpgradeAndTimeLimitTermination(limitTime))
                .build();

        MultiStartLocalSearch multiStartLocalSearch = new MultiStartLocalSearch(new RandomStartOperatorSelector());

        return multiStartLocalSearch.executeGlobal(localSearchAlgorithm, neighborhoodOperatorList, limitTime, getObserver());
    }


}
