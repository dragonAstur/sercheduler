package com.uniovi.sercheduler.localsearch.algorithms.multistart;

import com.uniovi.sercheduler.jmetal.problem.SchedulePermutationSolution;
import com.uniovi.sercheduler.localsearch.algorithms.localsearchalgorithm.LocalSearchAlgorithm;
import com.uniovi.sercheduler.localsearch.algorithms.multistartcomponents.StartOperatorSelector;
import com.uniovi.sercheduler.localsearch.observer.NeighborhoodObserver;
import com.uniovi.sercheduler.localsearch.operator.NeighborhoodOperatorGlobal;
import com.uniovi.sercheduler.localsearch.operator.NeighborhoodOperatorLazy;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class MultiStartLocalSearch {

    private final StartOperatorSelector startOperatorSelector;


    public MultiStartLocalSearch(StartOperatorSelector startOperatorSelector){
        this.startOperatorSelector = startOperatorSelector;
    }

    public SchedulePermutationSolution executeGlobal(LocalSearchAlgorithm localSearchAlgorithm,
                                               List<NeighborhoodOperatorGlobal> neighborhoodOperatorList,
                                               Long limitTime,
                                               NeighborhoodObserver observer)
    {

        observer.executionStarted();
        AtomicInteger localSearchIterations = new AtomicInteger();
        long startingTime = localSearchAlgorithm.startTimeCounter();

        SchedulePermutationSolution totalBestNeighbor = null;
        double totalWorstMakespan = -1;

        List<NeighborhoodOperatorGlobal> chosenOperators;

        do {

            chosenOperators = startOperatorSelector.selectOperatorsGlobal(neighborhoodOperatorList);    //TODO: here it changes from Global to Lazy

            SchedulePermutationSolution actualSolution =
                    localSearchAlgorithm.runLocalSearchGlobal(chosenOperators, localSearchIterations, observer);    //TODO: here it changes from Global to Lazy

            //If it is the first time, initialize the total best neighbor variable
            if(totalBestNeighbor == null)
                totalBestNeighbor = actualSolution;

            if(actualSolution.getFitnessInfo().fitness().get("makespan") < totalBestNeighbor.getFitnessInfo().fitness().get("makespan"))
                totalBestNeighbor = actualSolution;
            else if(actualSolution.getFitnessInfo().fitness().get("makespan") > totalWorstMakespan)
                totalWorstMakespan = actualSolution.getFitnessInfo().fitness().get("makespan");

            observer.addReachedMakespan(actualSolution.getFitnessInfo().fitness().get("makespan"));

        } while(System.currentTimeMillis() - startingTime < limitTime);

        observer.setTotalBestMakespan(totalBestNeighbor.getFitnessInfo().fitness().get("makespan"));
        observer.setTotalWorstMakespan(totalWorstMakespan);

        observer.setExecutionTime(System.currentTimeMillis() - startingTime);
        observer.setNumberOfIterations(localSearchIterations.get());

        observer.executionEnded();

        return totalBestNeighbor;
    }

    public SchedulePermutationSolution executeLazy(LocalSearchAlgorithm localSearchAlgorithm,
                                               List<NeighborhoodOperatorLazy> neighborhoodOperatorList,
                                               Long limitTime,
                                               NeighborhoodObserver observer)
    {

        observer.executionStarted();
        AtomicInteger localSearchIterations = new AtomicInteger();
        long startingTime = System.currentTimeMillis();

        SchedulePermutationSolution totalBestNeighbor = null;
        double totalWorstMakespan = -1;

        List<NeighborhoodOperatorLazy> chosenOperators;

        do {

            chosenOperators = startOperatorSelector.selectOperatorsLazy(neighborhoodOperatorList);    //TODO: here it changes from Global to Lazy

            SchedulePermutationSolution actualSolution =
                    localSearchAlgorithm.runLocalSearchLazy(chosenOperators, localSearchIterations, observer);    //TODO: here it changes from Global to Lazy

            //If it is the first time, initialize the total best neighbor variable
            if(totalBestNeighbor == null)
                totalBestNeighbor = actualSolution;

            if(actualSolution.getFitnessInfo().fitness().get("makespan") < totalBestNeighbor.getFitnessInfo().fitness().get("makespan"))
                totalBestNeighbor = actualSolution;
            else if(actualSolution.getFitnessInfo().fitness().get("makespan") > totalWorstMakespan)
                totalWorstMakespan = actualSolution.getFitnessInfo().fitness().get("makespan");

            observer.addReachedMakespan(actualSolution.getFitnessInfo().fitness().get("makespan"));

        } while(System.currentTimeMillis() - startingTime < limitTime);

        observer.setTotalBestMakespan(totalBestNeighbor.getFitnessInfo().fitness().get("makespan"));
        observer.setTotalWorstMakespan(totalWorstMakespan);

        observer.setExecutionTime(System.currentTimeMillis() - startingTime);
        observer.setNumberOfIterations(localSearchIterations.get());

        observer.executionEnded();

        return totalBestNeighbor;
    }

}
