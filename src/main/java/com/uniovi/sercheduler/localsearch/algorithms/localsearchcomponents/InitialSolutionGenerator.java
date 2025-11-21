package com.uniovi.sercheduler.localsearch.algorithms.localsearchcomponents;

import com.uniovi.sercheduler.jmetal.problem.SchedulePermutationSolution;
import com.uniovi.sercheduler.service.FitnessCalculator;

public interface InitialSolutionGenerator {

    public SchedulePermutationSolution createInitialSolution(FitnessCalculator fitnessCalculator);
}
