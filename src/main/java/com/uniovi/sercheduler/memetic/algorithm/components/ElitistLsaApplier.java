package com.uniovi.sercheduler.memetic.algorithm.components;

import com.uniovi.sercheduler.jmetal.problem.SchedulePermutationSolution;
import com.uniovi.sercheduler.localsearch.algorithms.localsearchalgorithm.LocalSearchAlgorithm;
import com.uniovi.sercheduler.localsearch.operator.NeighborhoodOperatorLazy;
import com.uniovi.sercheduler.memetic.algorithm.MemeticAlgorithm;
import com.uniovi.sercheduler.memetic.observer.MemeticObserver;

import java.util.List;

public class ElitistLsaApplier implements LsaApplier {
    @Override
    public void applyLSA(List<SchedulePermutationSolution> population, LocalSearchAlgorithm lsa,
                         List<NeighborhoodOperatorLazy> operatorList, MemeticObserver observer) {

        int bestIndex =
                MemeticAlgorithm.getBestSolutionPos(population);

        SchedulePermutationSolution bestSolution = population.get(bestIndex);

        double bestSolutionMakespan = bestSolution.getFitnessInfo().fitness().get("makespan");

        observer.addMakespanBeforeLSA(bestSolutionMakespan);

        observer.updateBestSolutionsOrigins(bestSolution);

        observer.updateMemeticEvolution(
                bestSolutionMakespan
        );

        lsa.setInitialSolution(bestSolution);

        SchedulePermutationSolution enhancedBestSolution = lsa.runLocalSearchLazy(operatorList, observer);

        double enhancedBestSolutionMakespan = enhancedBestSolution.getFitnessInfo().fitness().get("makespan");

        population.set(bestIndex, enhancedBestSolution);

        observer.addMakespanAfterLSA(enhancedBestSolutionMakespan);

        observer.updateMemeticEvolution(
                enhancedBestSolutionMakespan
        );

    }
}
