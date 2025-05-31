package com.uniovi.sercheduler.localsearch.algorithms.localsearchcomponents;

import com.uniovi.sercheduler.jmetal.problem.SchedulingProblem;
import com.uniovi.sercheduler.service.FitnessCalculator;
import com.uniovi.sercheduler.service.FitnessCalculatorSimple;

public class FitnessCalculatorGeneratorImpl implements FitnessCalculatorGenerator{

    private final SchedulingProblem problem;

    public FitnessCalculatorGeneratorImpl(SchedulingProblem problem){
        this.problem = problem;
    }

    public FitnessCalculator createFitnessCalculator(){
        return new FitnessCalculatorSimple(problem.getInstanceData());
    }

}
