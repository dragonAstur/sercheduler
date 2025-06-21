package com.uniovi.sercheduler.localsearch.operator;

import com.uniovi.sercheduler.jmetal.problem.SchedulePermutationSolution;

import java.util.List;

public interface NeighborhoodOperatorPositional {

    List<GeneratedNeighbor> execute(SchedulePermutationSolution actualSolution, int position);

}
