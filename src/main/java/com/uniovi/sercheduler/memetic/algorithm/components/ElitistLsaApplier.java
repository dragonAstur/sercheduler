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

        //Usar el initial solution generator
        SchedulePermutationSolution bestSolution = population.get(bestIndex);

        observer.updateMemeticEvolution(
                bestSolution.getFitnessInfo().fitness().get("makespan"),
                0,
                0
        );

        lsa.setInitialSolution(bestSolution);

        SchedulePermutationSolution enhancedBestSolution = lsa.runLocalSearchLazy(operatorList, observer);

        population.set(bestIndex, enhancedBestSolution);

        observer.updateMemeticEvolution(
                enhancedBestSolution.getFitnessInfo().fitness().get("makespan"),
                0,
                0
        );

    }
}
