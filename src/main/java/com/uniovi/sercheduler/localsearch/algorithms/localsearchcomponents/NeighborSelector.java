package com.uniovi.sercheduler.localsearch.algorithms.localsearchcomponents;

import com.uniovi.sercheduler.jmetal.problem.SchedulePermutationSolution;
import com.uniovi.sercheduler.localsearch.evaluator.LocalsearchEvaluator;
import com.uniovi.sercheduler.localsearch.observer.Observer;
import com.uniovi.sercheduler.localsearch.operator.GeneratedNeighbor;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

public interface NeighborSelector {

    /*
    This is only executed during simple climbing strategies, otherwise there is no need for it to be lazy
     */
    Optional<GeneratedNeighbor> selectBestNeighborLazy(SchedulePermutationSolution actualSolution,
                                                       Stream<GeneratedNeighbor> neighbors, LocalsearchEvaluator evaluator,
                                                       AtomicInteger counter, AcceptanceCriterion acceptanceCriterion,
                                                       TerminationCriterion terminationCriterion,
                                                       Observer observer);

    SchedulePermutationSolution selectBestNeighborGlobalAndUpdateObserver(SchedulePermutationSolution originalSolution,
                                                                          SchedulePermutationSolution bestSolutionKnown,
                                                         List<GeneratedNeighbor> neighborsList, LocalsearchEvaluator evaluator,
                                                         TerminationCriterion terminationCriterion,
                                                         Observer observer);

    SchedulePermutationSolution selectBestNeighborGlobal(SchedulePermutationSolution originalSolution,
                                                         SchedulePermutationSolution bestSolutionKnown,
                                                         List<GeneratedNeighbor> neighborsList,
                                                         LocalsearchEvaluator evaluator,
                                                         TerminationCriterion terminationCriterion,
                                                         Observer observer);

    void updateObserverMetrics(Observer observer);

    int getNumberOfGeneratedNeighbors();

}
