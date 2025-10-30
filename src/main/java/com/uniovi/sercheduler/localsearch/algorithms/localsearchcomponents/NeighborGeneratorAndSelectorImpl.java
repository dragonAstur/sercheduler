package com.uniovi.sercheduler.localsearch.algorithms.localsearchcomponents;

import com.uniovi.sercheduler.jmetal.problem.SchedulePermutationSolution;
import com.uniovi.sercheduler.localsearch.evaluator.LocalsearchEvaluator;
import com.uniovi.sercheduler.localsearch.observer.Observer;
import com.uniovi.sercheduler.localsearch.operator.*;
import com.uniovi.sercheduler.service.PlanPair;

import java.util.*;

public class NeighborGeneratorAndSelectorImpl implements NeighborGeneratorAndSelector {

    private final NeighborSelector selector;

    public NeighborGeneratorAndSelectorImpl() {
        this.selector = new NeighborSelectorImpl();
    }

    public int numberOfGeneratedNeighbors() {
        return selector.getNumberOfGeneratedNeighbors();
    }

    public SchedulePermutationSolution generateAndSelectNeighbors(List<NeighborhoodOperatorGlobal> neighborhoodOperatorList,
                                                                  SchedulePermutationSolution actualSolution,
                                                                  TerminationCriterion terminationCriterion,
                                                                  LocalsearchEvaluator evaluator,
                                                                  Observer observer){

        List<SchedulePermutationSolution> generatedSolutions = new ArrayList<>();

        Collections.shuffle(neighborhoodOperatorList);

        for(NeighborhoodOperatorGlobal globalOperator : neighborhoodOperatorList){

            generatedSolutions.add(
                    generateAndSelectNeighborsSingleOperator(actualSolution, globalOperator, terminationCriterion, evaluator, observer)
            );

            if(terminationCriterion.hasTimeExceeded()) break;
        }

        selector.updateObserverMetrics(observer);

        return generatedSolutions.stream()
                .min(Comparator.comparingDouble(s -> s.getFitnessInfo().fitness().get("makespan")))
                .orElseThrow(() -> new NoSuchElementException("No hay soluciones generadas"));

    }

    private SchedulePermutationSolution generateAndSelectNeighborsSingleOperator(SchedulePermutationSolution actualSolution,
                                                                                 NeighborhoodOperatorGlobal globalOperator,
                                                                                 TerminationCriterion terminationCriterion,
                                                                                 LocalsearchEvaluator evaluator,
                                                                                 Observer observer){

        List<PlanPair> plan = List.copyOf(actualSolution.getPlan());
        List<GeneratedNeighbor> positionalGeneratedNeighbors;

        SchedulePermutationSolution actualBestNeighbor, totalBestNeighbor = null;
        NeighborhoodOperatorPositional positionalOperator = globalOperator.getNeighborhoodOperatorPositional();

        for(int i = 0; i < plan.size(); i++) {

            positionalGeneratedNeighbors = positionalOperator.execute(actualSolution, i);

            //Select the best neighbors from all neighbors generated in that position
            actualBestNeighbor = this.selector.selectBestNeighborGlobal(actualSolution,
                    positionalGeneratedNeighbors, evaluator, terminationCriterion);

            if(totalBestNeighbor == null || totalBestNeighbor.getFitnessInfo().fitness().get("makespan") > actualBestNeighbor.getFitnessInfo().fitness().get("makespan"))
                    totalBestNeighbor = actualBestNeighbor;

            observer.updateMakespanEvolution(actualSolution.getFitnessInfo().fitness().get("makespan"),
                    numberOfGeneratedNeighbors());

            if(terminationCriterion.hasTimeExceeded())
                break;
        }

        return totalBestNeighbor;

    }




}
