package com.uniovi.sercheduler.localsearch.operator;

import com.uniovi.sercheduler.jmetal.problem.SchedulePermutationSolution;
import com.uniovi.sercheduler.localsearch.movement.Movement;

import java.util.List;

public record GeneratedNeighbor(SchedulePermutationSolution generatedSolution, List<Movement> movements) {
}
