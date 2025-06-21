package com.uniovi.sercheduler.localsearch.operator;

import com.uniovi.sercheduler.jmetal.problem.SchedulePermutationSolution;
import org.uma.jmetal.operator.Operator;

import java.util.List;

public interface NeighborhoodOperatorGlobal extends NeighborhoodOperator<List<GeneratedNeighbor>> {

    @Override
    List<GeneratedNeighbor> execute(SchedulePermutationSolution actualSolution);

    String getName();
}
