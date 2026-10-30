package com.uniovi.sercheduler.memetic.algorithm.components;

import com.uniovi.sercheduler.jmetal.problem.SchedulePermutationSolution;
import com.uniovi.sercheduler.localsearch.algorithms.localsearchalgorithm.LocalSearchAlgorithm;
import com.uniovi.sercheduler.localsearch.operator.NeighborhoodOperatorLazy;
import com.uniovi.sercheduler.memetic.algorithm.MemeticAlgorithm;
import com.uniovi.sercheduler.memetic.observer.MemeticObserver;

import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class PercentageAndElitistApplier implements LsaApplier {


    public static final double DEFAULT_PERCENTAGE = 0.05;

    private final double percentage;

    public PercentageAndElitistApplier(double percentage) {
        this.percentage = DEFAULT_PERCENTAGE;
    }

    @Override
    public void applyLSA(List<SchedulePermutationSolution> population, LocalSearchAlgorithm lsa,
                         List<NeighborhoodOperatorLazy> operatorList, MemeticObserver observer) {

        //Saber a cuántos individuos aplicar la búsqueda
        int numberOfApplications = calculateNumberOfApplications(population);


        //Sacar el mejor individuo
        int bestIndex =
                MemeticAlgorithm.getBestSolutionPos(population);

        //Esto cambia con respecto al elitista original:
        SchedulePermutationSolution bestSolution = population.remove(bestIndex); //en vez de get, remove

        double bestSolutionMakespan = bestSolution.getFitnessInfo().fitness().get("makespan");

        observer.addActualMakespanBeforeLSA(bestSolutionMakespan);

        observer.updateBestSolutionsOrigins(bestSolution);

        observer.updateMemeticEvolution(
                bestSolutionMakespan
        );


        //Aplicarle la búsqueda local al mejor individuo
        lsa.setInitialSolution(bestSolution);

        SchedulePermutationSolution enhancedBestSolution = lsa.runLocalSearchLazy(operatorList, observer);

        double enhancedBestSolutionMakespan = enhancedBestSolution.getFitnessInfo().fitness().get("makespan");

        //todavía no se añade el elitista mejorado

        observer.addActualMakespanAfterLSA(enhancedBestSolutionMakespan);

        observer.updateMemeticEvolution(
                enhancedBestSolutionMakespan
        );


        //Aplicar la búsqueda local sobre el porcentaje de la población

        List<Integer> randomIndexes = calculateRandomIndexes(population);

        int pos;
        SchedulePermutationSolution baseSolution, enhancedSolution;
        double baseSolutionMakespan, enhancedSolutionMakespan;

        for(int i = 0; i < numberOfApplications; i++){

            pos = randomIndexes.get(i);

            baseSolution = population.get(pos);

            baseSolutionMakespan = baseSolution.getFitnessInfo().fitness().get("makespan");

            observer.addActualMakespanBeforeLSA(baseSolutionMakespan);

            observer.updateMemeticEvolution(
                    baseSolutionMakespan
            );

            lsa.setInitialSolution(baseSolution);

            enhancedSolution = lsa.runLocalSearchLazy(operatorList, observer);

            population.set(pos, enhancedSolution);

            enhancedSolutionMakespan = baseSolution.getFitnessInfo().fitness().get("makespan");

            observer.addActualMakespanAfterLSA(enhancedSolutionMakespan);

            observer.updateMemeticEvolution(
                    enhancedSolutionMakespan
            );
        }

        observer.updateAccMakespanBeforeAndAfterLSA();

        //Insertar en la población la solución élite mejorada
        population.add(enhancedBestSolution);
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
