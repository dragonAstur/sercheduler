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

    public int getFirstPosition() {
        return firstPosition;
    }

    public int[] getParentPositions() {
        return parentsPositions;
    }

    @Override
    public int getFirstChangePosition() {
        return Math.min(firstPosition, secondPosition);
    }

    @Override
    public int[] changedHostPositions() {
        return new int[0];
    }


}
