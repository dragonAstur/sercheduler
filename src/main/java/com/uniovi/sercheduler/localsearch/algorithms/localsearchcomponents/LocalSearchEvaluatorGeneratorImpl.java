package com.uniovi.sercheduler.localsearch.algorithms.localsearchcomponents;

import com.uniovi.sercheduler.jmetal.problem.SchedulingProblem;
import com.uniovi.sercheduler.localsearch.evaluator.LocalsearchEvaluator;
import com.uniovi.sercheduler.service.calculator.FitnessCalculator;

public class LocalSearchEvaluatorGeneratorImpl implements LocalSearchEvaluatorGenerator {

    private final SchedulingProblem problem;

    public LocalSearchEvaluatorGeneratorImpl(SchedulingProblem problem){
        this.problem = problem;
    }

    public LocalsearchEvaluator createLocalSearchEvaluator(FitnessCalculator fitnessCalculator){

        return new LocalsearchEvaluator(fitnessCalculator.getComputationMatrix(), fitnessCalculator.getNetworkMatrix(), problem.getInstanceData());
    }

}
