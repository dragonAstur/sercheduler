package com.uniovi.sercheduler.localsearch.movement;

import com.uniovi.sercheduler.jmetal.problem.SchedulePermutationSolution;
import com.uniovi.sercheduler.localsearch.evaluator.LocalsearchEvaluator;

public class SwapMovement implements Movement{

    private int initialPosition;
    private int finalPosition;
    int[] parentsPositions;

    public SwapMovement(int initialPosition, int finalPosition, int[] parentsPositions) {
        this.initialPosition = initialPosition;
        this.finalPosition = finalPosition;
        this.parentsPositions = parentsPositions;
    }

    @Override
    public int getFinalPosition() {
        return finalPosition;
    }

    @Override
    public double computeEnhancement(LocalsearchEvaluator evaluator, SchedulePermutationSolution originalSolution, SchedulePermutationSolution generatedSolution) {
        return 0;
    }

    public int getInitialPosition() {
        return initialPosition;
    }

    public int[] getParentPositions() {
        return parentsPositions;
    }

}
