package com.uniovi.sercheduler.localsearch.algorithms.localsearchcomponents;

import com.uniovi.sercheduler.jmetal.problem.SchedulePermutationSolution;
import com.uniovi.sercheduler.service.calculator.FitnessCalculator;

public class InitialSolutionGeneratorSpecified implements InitialSolutionGenerator {

    private SchedulePermutationSolution specifiedSolution;

    @Override
    public SchedulePermutationSolution createInitialSolution() {
        return specifiedSolution;
    }

    @Override
    public SchedulePermutationSolution createInitialEvaluatedSolution(FitnessCalculator fitnessCalculator) {
        if(specifiedSolution != null)
            specifiedSolution.setFitnessInfo(fitnessCalculator.calculateFitness(specifiedSolution));
        return specifiedSolution;
    }

    public void setSpecifiedSolution(SchedulePermutationSolution specifiedSolution) {
        this.specifiedSolution = specifiedSolution;
    }
}
