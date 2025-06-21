package com.uniovi.sercheduler.localsearch.operator;

import com.uniovi.sercheduler.jmetal.problem.SchedulePermutationSolution;
import org.uma.jmetal.operator.Operator;

public interface NeighborhoodOperator<T> extends Operator<SchedulePermutationSolution, T> {
    T execute(SchedulePermutationSolution actualSolution);
    String getName();
}
