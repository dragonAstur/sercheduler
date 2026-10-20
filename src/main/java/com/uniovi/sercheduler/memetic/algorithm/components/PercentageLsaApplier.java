package com.uniovi.sercheduler.memetic.algorithm.components;

import com.uniovi.sercheduler.jmetal.problem.SchedulePermutationSolution;
import com.uniovi.sercheduler.localsearch.algorithms.localsearchalgorithm.LocalSearchAlgorithm;
import com.uniovi.sercheduler.localsearch.operator.NeighborhoodOperatorLazy;
import com.uniovi.sercheduler.memetic.algorithm.MemeticAlgorithm;
import com.uniovi.sercheduler.memetic.observer.MemeticObserver;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class PercentageLsaApplier implements LsaApplier {

    public static final double DEFAULT_PERCENTAGE = 0.05;

    private final double percentage;

    public PercentageLsaApplier(){
        this.percentage = DEFAULT_PERCENTAGE;
    }

    public PercentageLsaApplier(double percentage){
        this.percentage = percentage;
    }

    @Override
    public void applyLSA(List<SchedulePermutationSolution> population, LocalSearchAlgorithm lsa,
                         List<NeighborhoodOperatorLazy> operatorList, MemeticObserver observer) {

        int bestIndex =
                MemeticAlgorithm.getBestSolutionPos(population);

        //Usar el initial solution generator
        SchedulePermutationSolution bestSolution = population.get(bestIndex);

        observer.updateBestSolutionsOrigins(bestSolution);




        int numberOfApplications = Math.max(1, (int) Math.round(population.size() * this.percentage));

        Random random = new Random();
        int pos;
        SchedulePermutationSolution baseSolution, enhancedSolution;
        double baseSolutionMakespan, enhancedSolutionMakespan;

        List<Integer> randomIndexes = IntStream.range(0, population.size())
                .boxed()
                .collect(Collectors.toList());

        Collections.shuffle(randomIndexes, random);

        for(int i = 0; i < numberOfApplications; i++){

            pos = randomIndexes.get(i);

            baseSolution = population.get(pos);

            baseSolutionMakespan = baseSolution.getFitnessInfo().fitness().get("makespan");

            observer.updateMemeticEvolution(
                    baseSolutionMakespan,
                    0,
                    0
            );

            observer.addMakespanBeforeLSA(baseSolutionMakespan);

            lsa.setInitialSolution(baseSolution);

            enhancedSolution = lsa.runLocalSearchLazy(operatorList, observer);

            population.set(pos, enhancedSolution);

            enhancedSolutionMakespan = baseSolution.getFitnessInfo().fitness().get("makespan");

            observer.updateMemeticEvolution(
                    enhancedSolutionMakespan,
                    0,
                    0
            );

            observer.addMakespanAfterLSA(enhancedSolutionMakespan);
        }
    }
}
