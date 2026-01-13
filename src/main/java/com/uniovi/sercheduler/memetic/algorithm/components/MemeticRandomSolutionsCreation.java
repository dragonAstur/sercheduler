package com.uniovi.sercheduler.memetic.algorithm.components;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.solution.Solution;

public class MemeticRandomSolutionsCreation<S extends Solution<?>> implements MemeticSolutionsCreation<S> {
    private final int numberOfSolutionsToCreate;
    private final Problem<S> problem;

    public MemeticRandomSolutionsCreation(Problem<S> problem, int numberOfSolutionsToCreate) {
        this.problem = problem;
        this.numberOfSolutionsToCreate = numberOfSolutionsToCreate;
    }

    public List<S> create() {
        List<S> solutionList = new ArrayList(this.numberOfSolutionsToCreate);
        IntStream.range(0, this.numberOfSolutionsToCreate).forEach((i) -> {
            solutionList.add(problem.createSolution());
        });
        return solutionList;
    }
}