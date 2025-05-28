package com.uniovi.sercheduler.localsearch.strategy.neighborgenerator;

import com.uniovi.sercheduler.jmetal.problem.SchedulePermutationSolution;
import com.uniovi.sercheduler.localsearch.operator.GeneratedNeighbor;
import com.uniovi.sercheduler.localsearch.operator.NeighborhoodOperatorGlobal;
import com.uniovi.sercheduler.localsearch.operator.NeighborhoodOperatorLazy;

import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Stream;

public class UnionNeighborGenerator extends AbstractNeighborGenerator{

    public Stream<GeneratedNeighbor> generateNeighborsLazy(List<NeighborhoodOperatorLazy> neighborhoodLazyOperatorList, SchedulePermutationSolution actualSolution){

        List<Supplier<Stream<GeneratedNeighbor>>> operators = new ArrayList<>();

        for(NeighborhoodOperatorLazy neighborhoodLazyOperator : neighborhoodLazyOperatorList)
            operators.add(() -> neighborhoodLazyOperator.execute(actualSolution));

        return shuffleStreams(operators);
    }



    public List<GeneratedNeighbor> generateNeighborsGlobal(List<NeighborhoodOperatorGlobal> neighborhoodOperatorList, SchedulePermutationSolution actualSolution){

        List<GeneratedNeighbor> neighborsList = new ArrayList<>();

        for(NeighborhoodOperatorGlobal neighborhoodOperator : neighborhoodOperatorList){
            neighborsList.addAll(
                    neighborhoodOperator.execute(actualSolution)
            );
        }

        return neighborsList;
    }

}
