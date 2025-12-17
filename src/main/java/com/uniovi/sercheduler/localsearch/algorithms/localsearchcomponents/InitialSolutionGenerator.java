package com.uniovi.sercheduler.localsearch.algorithms.localsearchcomponents;

import com.uniovi.sercheduler.jmetal.problem.SchedulePermutationSolution;
import com.uniovi.sercheduler.service.calculator.FitnessCalculator;

public interface InitialSolutionGenerator {

    SchedulePermutationSolution createInitialSolution(FitnessCalculator fitnessCalculator);

    void setInitialSolution(SchedulePermutationSolution initialSolution);

}
