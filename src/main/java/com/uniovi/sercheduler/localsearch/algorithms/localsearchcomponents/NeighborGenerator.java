package com.uniovi.sercheduler.localsearch.algorithms.localsearchcomponents;

import com.uniovi.sercheduler.jmetal.problem.SchedulePermutationSolution;
import com.uniovi.sercheduler.localsearch.operator.GeneratedNeighbor;
import com.uniovi.sercheduler.localsearch.operator.NeighborhoodOperatorGlobal;
import com.uniovi.sercheduler.localsearch.operator.NeighborhoodOperatorLazy;

import java.util.List;
import java.util.stream.Stream;

public interface NeighborGenerator {

    Stream<GeneratedNeighbor> generateNeighborsLazy(List<NeighborhoodOperatorLazy> neighborhoodLazyOperatorList, SchedulePermutationSolution actualSolution);

    List<GeneratedNeighbor> generateNeighborsGlobal(List<NeighborhoodOperatorGlobal> neighborhoodOperatorList, SchedulePermutationSolution actualSolution);

}
