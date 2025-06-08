package com.uniovi.sercheduler.localsearch.algorithms.multistart;

import com.uniovi.sercheduler.jmetal.problem.SchedulePermutationSolution;
import com.uniovi.sercheduler.localsearch.algorithms.localsearchalgorithm.LocalSearchAlgorithm;
import com.uniovi.sercheduler.localsearch.algorithms.multistartcomponents.StartOperatorSelector;
import com.uniovi.sercheduler.localsearch.observer.LocalSearchObserver;
import com.uniovi.sercheduler.localsearch.operator.NeighborhoodOperatorGlobal;
import com.uniovi.sercheduler.localsearch.operator.NeighborhoodOperatorLazy;

import java.util.List;

public class MultiStartLocalSearch {

    private final StartOperatorSelector startOperatorSelector;


    public MultiStartLocalSearch(StartOperatorSelector startOperatorSelector){
        this.startOperatorSelector = startOperatorSelector;
    }

    public SchedulePermutationSolution executeGlobal(LocalSearchAlgorithm localSearchAlgorithm,
                                               List<NeighborhoodOperatorGlobal> neighborhoodOperatorList,
                                               Long limitTime,
                                               LocalSearchObserver observer)
    {

        long startingTime = localSearchAlgorithm.startTimeCounter();

        SchedulePermutationSolution totalBestNeighbor = null;

        List<NeighborhoodOperatorGlobal> chosenOperators;

        do {

            chosenOperators = startOperatorSelector.selectOperatorsGlobal(neighborhoodOperatorList);    //TODO: here it changes from Global to Lazy

            SchedulePermutationSolution actualSolution =
                    localSearchAlgorithm.runLocalSearchGlobal(chosenOperators, observer);    //TODO: here it changes from Global to Lazy

            //If it is the first time, initialize the total best neighbor variable
            if(totalBestNeighbor == null)
                totalBestNeighbor = actualSolution;

            if(actualSolution.getFitnessInfo().fitness().get("makespan") < totalBestNeighbor.getFitnessInfo().fitness().get("makespan"))
                totalBestNeighbor = actualSolution;

            observer.endStart();

        } while(System.currentTimeMillis() - startingTime < limitTime);

        observer.endRun(System.currentTimeMillis() - startingTime);

        return totalBestNeighbor;
    }

    public SchedulePermutationSolution executeLazy(LocalSearchAlgorithm localSearchAlgorithm,
                                               List<NeighborhoodOperatorLazy> neighborhoodOperatorList,
                                               Long limitTime,
                                               LocalSearchObserver observer)
    {

        long startingTime = System.currentTimeMillis();

        SchedulePermutationSolution totalBestNeighbor = null;

        List<NeighborhoodOperatorLazy> chosenOperators;

        do {

            chosenOperators = startOperatorSelector.selectOperatorsLazy(neighborhoodOperatorList);    //TODO: here it changes from Global to Lazy

            SchedulePermutationSolution actualSolution =
                    localSearchAlgorithm.runLocalSearchLazy(chosenOperators, observer);    //TODO: here it changes from Global to Lazy

            //If it is the first time, initialize the total best neighbor variable
            if(totalBestNeighbor == null)
                totalBestNeighbor = actualSolution;

            if(actualSolution.getFitnessInfo().fitness().get("makespan") < totalBestNeighbor.getFitnessInfo().fitness().get("makespan"))
                totalBestNeighbor = actualSolution;

            observer.endStart();

        } while(System.currentTimeMillis() - startingTime < limitTime);


        observer.endRun(System.currentTimeMillis() - startingTime);

        return totalBestNeighbor;
    }

}
