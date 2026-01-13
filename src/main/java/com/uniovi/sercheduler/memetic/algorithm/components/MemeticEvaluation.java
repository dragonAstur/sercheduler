package com.uniovi.sercheduler.memetic.algorithm.components;

import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.solution.Solution;

import java.util.List;

public interface MemeticEvaluation<S extends Solution<?>> {
    List<S> evaluate(List<S> var1);

    int computedEvaluations();

    Problem<S> problem();
}
