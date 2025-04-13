package com.uniovi.sercheduler.localsearch.movement;

import com.uniovi.sercheduler.jmetal.problem.SchedulePermutationSolution;
import com.uniovi.sercheduler.localsearch.evaluator.LocalsearchEvaluator;

public interface Movement {

    int getFinalPosition();

    double computeEnhancement(LocalsearchEvaluator evaluator, SchedulePermutationSolution originalSolution, SchedulePermutationSolution generatedSolution);
}
