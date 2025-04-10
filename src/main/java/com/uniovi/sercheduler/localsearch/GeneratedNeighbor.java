package com.uniovi.sercheduler.localsearch;

import com.uniovi.sercheduler.jmetal.problem.SchedulePermutationSolution;

public record GeneratedNeighbor(SchedulePermutationSolution generatedSolution, int[] changedPlanPairs, int initialPosition, int finalPosition) {
}
