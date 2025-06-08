package com.uniovi.sercheduler.localsearch.algorithms.localsearchcomponents;

import com.uniovi.sercheduler.jmetal.problem.SchedulePermutationSolution;
import com.uniovi.sercheduler.localsearch.evaluator.LocalsearchEvaluator;
import com.uniovi.sercheduler.localsearch.observer.LocalSearchObserver;
import com.uniovi.sercheduler.localsearch.operator.GeneratedNeighbor;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

public class NeighborSelectorImpl implements NeighborSelector {

    public Optional<GeneratedNeighbor> selectBestNeighborLazy(SchedulePermutationSolution actualSolution, Stream<GeneratedNeighbor> neighbors, LocalsearchEvaluator evaluator, AtomicInteger counter, AcceptanceCriterion acceptanceCriterion) {
        return neighbors
                .filter(neighbor -> {

                    counter.incrementAndGet();

                    evaluator.evaluate(actualSolution, neighbor.generatedSolution(), neighbor.movements().get(neighbor.movements().size() - 1));

                    return acceptanceCriterion.checkAcceptance(actualSolution, neighbor.generatedSolution());
                })
                .findFirst();   //this breaks laziness
    }

    public SchedulePermutationSolution selectBestNeighborGlobal(SchedulePermutationSolution originalSolution, List<GeneratedNeighbor> neighborsList, LocalsearchEvaluator evaluator, LocalSearchObserver observer){

        SchedulePermutationSolution bestSolution = originalSolution;
        double originalMakespan = originalSolution.getFitnessInfo().fitness().get("makespan");
        double bestMakespan = originalMakespan;
        double neighborMakespan;
        SchedulePermutationSolution neighborSolution;

        int numberOfBetterNeighbors = 0;
        double neighborImprovingRatio = 0.0;
        double allNeighborsImprovingRatioSum = 0.0;
        double betterNeighborsImprovingRatioSum = 0.0;

        for(GeneratedNeighbor neighbor : neighborsList){

            neighborSolution = neighbor.generatedSolution();
            evaluator.evaluate(originalSolution, neighborSolution, neighbor.movements().get(neighbor.movements().size() - 1));
            neighborMakespan = neighborSolution.getFitnessInfo().fitness().get("makespan");

            neighborImprovingRatio = (originalMakespan - neighborMakespan) / originalMakespan * 100;
            allNeighborsImprovingRatioSum += neighborImprovingRatio;

            if(neighborMakespan < originalMakespan){
                numberOfBetterNeighbors++;
                betterNeighborsImprovingRatioSum += neighborImprovingRatio;

                if(bestMakespan > neighborMakespan){
                    bestMakespan = neighborMakespan;
                    bestSolution = neighborSolution;
                }
            }

        }

        observer.setBetterNeighborsRatio(numberOfBetterNeighbors * 1.00 / neighborsList.size() );
        observer.setAllNeighborsImprovingRatio(allNeighborsImprovingRatioSum / neighborsList.size() );

        if(numberOfBetterNeighbors > 0)
            observer.setBetterNeighborsImprovingRatio( betterNeighborsImprovingRatioSum / numberOfBetterNeighbors );
        else
            observer.setBetterNeighborsImprovingRatio( 0.0 );

        return bestSolution;
    }


}
