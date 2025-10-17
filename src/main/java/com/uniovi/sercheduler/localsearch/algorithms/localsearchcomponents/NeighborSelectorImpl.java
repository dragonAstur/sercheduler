package com.uniovi.sercheduler.localsearch.algorithms.localsearchcomponents;

import com.uniovi.sercheduler.jmetal.problem.SchedulePermutationSolution;
import com.uniovi.sercheduler.localsearch.evaluator.LocalsearchEvaluator;
import com.uniovi.sercheduler.localsearch.observer.Observer;
import com.uniovi.sercheduler.localsearch.operator.GeneratedNeighbor;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

public class NeighborSelectorImpl implements NeighborSelector {

    private int numberOfBetterNeighbors = 0;
    private double allNeighborsImprovingRatioSum = 0.0;
    private double betterNeighborsImprovingRatioSum = 0.0;
    private int numberOfGeneratedNeighbors = 0;

    public Optional<GeneratedNeighbor> selectBestNeighborLazy(SchedulePermutationSolution actualSolution,
                                                              Stream<GeneratedNeighbor> neighbors, LocalsearchEvaluator evaluator,
                                                              AtomicInteger counter, AcceptanceCriterion acceptanceCriterion,
                                                              TerminationCriterion terminationCriterion) {
        return neighbors
                .takeWhile(neighbor -> terminationCriterion.hasTimeExceeded())
                .filter(neighbor -> {

                    counter.incrementAndGet();

                    evaluator.evaluate(actualSolution, neighbor.generatedSolution(), neighbor.movements().get(neighbor.movements().size() - 1));

                    return acceptanceCriterion.checkAcceptance(actualSolution, neighbor.generatedSolution());
                })
                .findFirst();   //this breaks laziness
    }

    public int getNumberOfGeneratedNeighbors(){
        return numberOfGeneratedNeighbors;
    }

    /**
     * Dada una solución y todos los vecinos que se han logrado generar aplicándole un esquema de vecindad,
     * se evalúan todos y se selecciona al mejor de todos, devolviéndolo.
     *
     * @param originalSolution la solución original sobre la que se han generado todos los vecinos
     * @param neighborsList la lista de vecinos de la solución original
     * @param evaluator el evaluador que determina el fitness de cada solución
     * @param observer un observer para registrar métricas del algoritmo
     * @param terminationCriterion el criterio de parada
     * @return el mejor que vecino que mejore a su vez la solución actual
     */
    public SchedulePermutationSolution selectBestNeighborGlobal(SchedulePermutationSolution originalSolution,
                                                                List<GeneratedNeighbor> neighborsList,
                                                                LocalsearchEvaluator evaluator,
                                                                TerminationCriterion terminationCriterion,
                                                                Observer observer){

        SchedulePermutationSolution bestSolution = selectBestNeighborGlobal(originalSolution, neighborsList, evaluator,
                terminationCriterion);

        updateObserverMetrics(observer);

        return bestSolution;
    }

    public SchedulePermutationSolution selectBestNeighborGlobal(SchedulePermutationSolution originalSolution,
                                                                List<GeneratedNeighbor> neighborsList,
                                                                LocalsearchEvaluator evaluator,
                                                                TerminationCriterion terminationCriterion){
        SchedulePermutationSolution bestSolution = originalSolution;
        double originalMakespan = originalSolution.getFitnessInfo().fitness().get("makespan");
        double bestMakespan = originalMakespan;
        double neighborMakespan;
        SchedulePermutationSolution neighborSolution;

        double neighborImprovingRatio;

        this.numberOfGeneratedNeighbors += neighborsList.size();

        for(GeneratedNeighbor neighbor : neighborsList){

            neighborSolution = neighbor.generatedSolution();
            evaluator.evaluate(originalSolution, neighborSolution, neighbor.movements().get(neighbor.movements().size() - 1));
            neighborMakespan = neighborSolution.getFitnessInfo().fitness().get("makespan");

            neighborImprovingRatio = (originalMakespan - neighborMakespan) / originalMakespan * 100;
            this.allNeighborsImprovingRatioSum += neighborImprovingRatio;

            if(neighborMakespan < originalMakespan){
                this.numberOfBetterNeighbors++;
                this.betterNeighborsImprovingRatioSum += neighborImprovingRatio;

                if(bestMakespan > neighborMakespan){
                    bestMakespan = neighborMakespan;
                    bestSolution = neighborSolution;
                }
            }

            if(terminationCriterion.hasTimeExceeded())
                break;

        }

        return bestSolution;
    }

    public void updateObserverMetrics(Observer observer){
        observer.setBetterNeighborsRatio(this.numberOfBetterNeighbors * 1.00 / this.numberOfGeneratedNeighbors);
        observer.setAllNeighborsImprovingRatio(allNeighborsImprovingRatioSum / this.numberOfGeneratedNeighbors );

        if(this.numberOfBetterNeighbors > 0)
            observer.setBetterNeighborsImprovingRatio( this.betterNeighborsImprovingRatioSum / this.numberOfBetterNeighbors );
        else
            observer.setBetterNeighborsImprovingRatio( 0.0 );

        observer.setNumberOfGeneratedNeighbors(this.numberOfGeneratedNeighbors);
    }


}
