package com.uniovi.sercheduler.localsearch.algorithms.localsearchcomponents;

import com.uniovi.sercheduler.jmetal.problem.SchedulePermutationSolution;

public interface AcceptanceCriterion {

    boolean checkAcceptance(SchedulePermutationSolution actualSolution, SchedulePermutationSolution bestNeighbor);
}
