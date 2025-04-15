package com.uniovi.sercheduler.localsearch.movement;

import com.uniovi.sercheduler.jmetal.problem.SchedulePermutationSolution;
import com.uniovi.sercheduler.localsearch.evaluator.LocalsearchEvaluator;

public class SwapMovement implements Movement{

    private int firstPosition;
    private int secondPosition;
    int[] parentsPositions;

    public SwapMovement(int firstPosition, int secondPosition, int[] parentsPositions) {
        this.firstPosition = firstPosition;
        this.secondPosition = secondPosition;
        this.parentsPositions = parentsPositions;
    }

    public int getSecondPosition() {
        return secondPosition;
    }

    @Override
    public double computeEnhancement(LocalsearchEvaluator evaluator, SchedulePermutationSolution originalSolution, SchedulePermutationSolution generatedSolution) {
        return 0;
    }

    public int getFirstPosition() {
        return firstPosition;
    }

    public int[] getParentPositions() {
        return parentsPositions;
    }

}
