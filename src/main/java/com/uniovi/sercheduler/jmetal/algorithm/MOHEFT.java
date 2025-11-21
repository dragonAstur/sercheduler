package com.uniovi.sercheduler.jmetal.algorithm;

import com.uniovi.sercheduler.jmetal.problem.SchedulePermutationSolution;
import com.uniovi.sercheduler.jmetal.problem.SchedulingProblem;
import com.uniovi.sercheduler.service.algorithms.MoHeft;
import org.uma.jmetal.algorithm.Algorithm;

import java.util.List;

public class MOHEFT implements Algorithm<List<SchedulePermutationSolution>> {


    protected SchedulingProblem problem;
    public final int numberOfSolutions;
    private List<SchedulePermutationSolution> solutions;


    public MOHEFT( SchedulingProblem problem, int numberOfSolutions) {
        this.problem = problem;
        this.numberOfSolutions = numberOfSolutions;
    }

    @Override
    public void run() {
        var moheft = new MoHeft(problem.getInstanceData());

        solutions = moheft.calculate(numberOfSolutions);

    }

    @Override
    public List<SchedulePermutationSolution> result() {
        return solutions;
    }

    @Override
    public String name() {
        return "MOHEFT";
    }

    @Override
    public String description() {
        return "Implementation of MOHEFT";
    }
}
