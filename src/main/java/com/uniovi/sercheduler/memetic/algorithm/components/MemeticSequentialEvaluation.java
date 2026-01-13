package com.uniovi.sercheduler.memetic.algorithm.components;

import com.uniovi.sercheduler.memetic.observer.MemeticObserver;
import org.uma.jmetal.component.catalogue.common.evaluation.Evaluation;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.errorchecking.Check;

import java.util.List;
import java.util.Objects;

public class MemeticSequentialEvaluation<S extends Solution<?>> implements MemeticEvaluation<S> {
    private int computedEvaluations;
    private final Problem<S> problem;

    public MemeticSequentialEvaluation(Problem<S> problem) {
        Check.notNull(problem);
        this.problem = problem;
        this.computedEvaluations = 0;
    }

    public List<S> evaluate(List<S> solutionList, MemeticObserver observer) {
        Check.notNull(solutionList);
        Problem var10001 = this.problem;
        Objects.requireNonNull(var10001);
        solutionList.forEach(var10001::evaluate);
        this.computedEvaluations = solutionList.size();
        return solutionList;
    }

    public int computedEvaluations() {
        return this.computedEvaluations;
    }

    public Problem<S> problem() {
        return this.problem;
    }
}
