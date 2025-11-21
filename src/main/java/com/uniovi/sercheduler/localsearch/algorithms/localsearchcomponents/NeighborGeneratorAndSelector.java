package com.uniovi.sercheduler.localsearch.algorithms.localsearchcomponents;

import com.uniovi.sercheduler.jmetal.problem.SchedulePermutationSolution;
import com.uniovi.sercheduler.localsearch.evaluator.LocalsearchEvaluator;
import com.uniovi.sercheduler.localsearch.observer.Observer;
import com.uniovi.sercheduler.localsearch.operator.NeighborhoodOperatorGlobal;

import java.util.List;

public interface NeighborGeneratorAndSelector {

    SchedulePermutationSolution generateAndSelectNeighbors(List<NeighborhoodOperatorGlobal> neighborhoodOperatorList,
                                                             SchedulePermutationSolution actualSolution,
                                                             TerminationCriterion terminationCriterion,
                                                             LocalsearchEvaluator evaluator,
                                                             Observer observer);

    int numberOfGeneratedNeighbors();
}
