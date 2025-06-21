package com.uniovi.sercheduler.localsearch.movement;

import com.uniovi.sercheduler.jmetal.problem.SchedulePermutationSolution;
import com.uniovi.sercheduler.localsearch.evaluator.LocalsearchEvaluator;

public class ChangeHostMovement implements Movement {

    int position;
    int[] parentPositions;
    int[] childrenPositions;

    public ChangeHostMovement(int position, int[] parentsPositions, int[] childrenPositions) {
        this.position = position;
        this.parentPositions = parentsPositions;
        this.childrenPositions = childrenPositions;
    }

    public int getPosition() {
        return position;
    }



    public int[] getChildrenPositions() {
        return childrenPositions;
    }

    public int[] getParentsPositions() {
        return parentPositions;
    }

    @Override
    public int getFirstChangePosition() {
        return position;
    }

    @Override
    public int[] changedHostPositions() {
        return new int[]{position};
    }


}
