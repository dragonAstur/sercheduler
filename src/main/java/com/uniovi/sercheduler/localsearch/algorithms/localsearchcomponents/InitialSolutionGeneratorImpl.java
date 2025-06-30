package com.uniovi.sercheduler.localsearch.algorithms.localsearchcomponents;

import com.uniovi.sercheduler.jmetal.problem.SchedulePermutationSolution;
import com.uniovi.sercheduler.jmetal.problem.SchedulingProblem;
import com.uniovi.sercheduler.service.calculator.FitnessCalculator;
import com.uniovi.sercheduler.service.FitnessInfo;

public class InitialSolutionGeneratorImpl implements InitialSolutionGenerator {

    private final SchedulingProblem problem;

    public InitialSolutionGeneratorImpl(SchedulingProblem problem){
        this.problem = problem;
    }

    public SchedulePermutationSolution createInitialSolution(FitnessCalculator fitnessCalculator){

        SchedulePermutationSolution actualSolution = problem.createSolution();

        //Evaluate this new created solution (this step is skipped in the pseudocode)
        FitnessInfo fitnessInfo = fitnessCalculator.calculateFitness(actualSolution);
        actualSolution.setFitnessInfo(fitnessInfo);

        return actualSolution;
    }

}
