package com.uniovi.sercheduler.localsearch.algorithms.localsearchcomponents;

import com.uniovi.sercheduler.jmetal.problem.SchedulePermutationSolution;
import com.uniovi.sercheduler.service.calculator.FitnessCalculator;

public class InitialSolutionGeneratorSpecified implements InitialSolutionGenerator {

    private SchedulePermutationSolution initialSolution;

    @Override
    public SchedulePermutationSolution createInitialSolution(FitnessCalculator fitnessCalculator) {
        if(initialSolution != null && initialSolution.getFitnessInfo() == null)
            initialSolution.setFitnessInfo(fitnessCalculator.calculateFitness(initialSolution));
        return initialSolution;
    }

    @Override
    public void setInitialSolution(SchedulePermutationSolution initialSolution) {
        this.initialSolution = initialSolution;
    }
}
