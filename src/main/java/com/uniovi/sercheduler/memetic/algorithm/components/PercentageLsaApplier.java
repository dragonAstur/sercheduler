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

    public PercentageLsaApplier(double percentage) {
        this.percentage = percentage;
    }

    @Override
    public void applyLSA(List<SchedulePermutationSolution> population, LocalSearchAlgorithm lsa,
                         List<NeighborhoodOperatorLazy> operatorList, MemeticObserver observer) {

        //Calcular a cuántos individuos hemos de aplicar la búsqueda local
        int numberOfApplications = calculateNumberOfApplications(population);

        //Escoger aleatoriamente a los individuos a los que se les va a
        //aplicar la búsqueda local en función de su posición
        List<Integer> randomIndexes = calculateRandomIndexes(population);

        //Aplicar la búsqueda local a esos individuos de la población
        int pos;
        SchedulePermutationSolution baseSolution, enhancedSolution;
        double baseSolutionMakespan, enhancedSolutionMakespan;

        for(int i = 0; i < numberOfApplications; i++){

            pos = randomIndexes.get(i);

            baseSolution = population.get(pos);

            baseSolutionMakespan = baseSolution.getFitnessInfo().fitness().get("makespan");

            observer.addActualMakespanBeforeLSA(baseSolutionMakespan);

            observer.updateMemeticEvolution(
                    baseSolution
            );

            lsa.setInitialSolution(baseSolution);

            enhancedSolution = lsa.runLocalSearchLazy(operatorList, observer);

            population.set(pos, enhancedSolution);

            enhancedSolutionMakespan = baseSolution.getFitnessInfo().fitness().get("makespan");

            observer.addActualMakespanAfterLSA(enhancedSolutionMakespan);

            observer.updateMemeticEvolution(
                    enhancedSolution
            );
        }

        observer.updateAccMakespanBeforeAndAfterLSA();
    }

    private int calculateNumberOfApplications(List<SchedulePermutationSolution> population) {
        return Math.max(1, (int) Math.round(population.size() * this.percentage));
    }

    private List<Integer> calculateRandomIndexes(List<SchedulePermutationSolution> population) {
        Random random = new Random();

        List<Integer> randomIndexes = IntStream.range(0, population.size())
                .boxed()
                .collect(Collectors.toList());

        Collections.shuffle(randomIndexes, random);
        return randomIndexes;
    }

}
