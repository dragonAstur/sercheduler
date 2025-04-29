package com.uniovi.sercheduler.localsearch.operator;

import com.uniovi.sercheduler.jmetal.problem.SchedulePermutationSolution;
import org.uma.jmetal.operator.Operator;

import java.util.stream.Stream;

public interface NeighborhoodOperatorLazy extends Operator<SchedulePermutationSolution, Stream<GeneratedNeighbor>>  {

    Stream<GeneratedNeighbor> execute(SchedulePermutationSolution actualSolution);

}