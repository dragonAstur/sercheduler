package com.uniovi.sercheduler.memetic.algorithm.components;

import com.uniovi.sercheduler.jmetal.problem.SchedulePermutationSolution;
import com.uniovi.sercheduler.localsearch.algorithms.localsearchalgorithm.LocalSearchAlgorithm;
import com.uniovi.sercheduler.localsearch.operator.NeighborhoodOperatorLazy;
import com.uniovi.sercheduler.memetic.observer.MemeticObserver;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class PercentageLsaApplier implements LsaApplier {

    public static final double DEFAULT_PERCENTAGE = 0.05;

    private double percentage;

    public PercentageLsaApplier(){
        this.percentage = DEFAULT_PERCENTAGE;
    }

    public PercentageLsaApplier(double percentage){
        this.percentage = percentage;
    }

    @Override
    public void applyLSA(List<SchedulePermutationSolution> population, LocalSearchAlgorithm lsa,
                         List<NeighborhoodOperatorLazy> operatorList, MemeticObserver observer) {

        int numberOfApplications = Math.max(1, (int) Math.round(population.size() * this.percentage));

        Random random = new Random();
        int pos;
        SchedulePermutationSolution baseSolution, enhancedSolution;

        List<Integer> randomIndexes = IntStream.range(0, population.size())
                .boxed()
                .collect(Collectors.toList());

        Collections.shuffle(randomIndexes, random);

        for(int i = 0; i < numberOfApplications; i++){

            pos = randomIndexes.get(i);

            baseSolution = population.get(pos);

            observer.updateMemeticEvolution(
                    baseSolution.getFitnessInfo().fitness().get("makespan"),
                    0,
                    0
            );

            lsa.setInitialSolution(baseSolution);

            enhancedSolution = lsa.runLocalSearchLazy(operatorList, observer);

            population.set(pos, enhancedSolution);

            observer.updateMemeticEvolution(
                    enhancedSolution.getFitnessInfo().fitness().get("makespan"),
                    0,
                    0
            );
        }
    }
}
