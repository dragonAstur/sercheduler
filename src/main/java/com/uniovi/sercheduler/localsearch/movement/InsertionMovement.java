package com.uniovi.sercheduler.localsearch.movement;

import com.uniovi.sercheduler.jmetal.problem.SchedulePermutationSolution;
import com.uniovi.sercheduler.localsearch.evaluator.LocalsearchEvaluator;

public class InsertionMovement implements Movement{

    private int[] changedPlanPairs;
    private int initialPosition;
    private int finalPosition;

    int[] parentsPositions;

    public InsertionMovement(int[] changedPlanPairs, int initialPosition, int finalPosition, int[] parentsPositions) {
        this.changedPlanPairs = changedPlanPairs;
        this.initialPosition = initialPosition;
        this.finalPosition = finalPosition;
        this.parentsPositions = parentsPositions;
    }

    public int getFinalPosition() {
        return finalPosition;
    }

    public int getInitialPosition() {
        return initialPosition;
    }

    public int[] getParentPositions() {
        return parentsPositions;
    }

    @Override
    public int getFirstChangePosition() {
        return changedPlanPairs[0];
    }

    @Override
    public int[] changedHostPositions() {
        return new int[0];
    }


}
