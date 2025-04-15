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

    @Override
    public double computeEnhancement(LocalsearchEvaluator evaluator, SchedulePermutationSolution originalSolution, SchedulePermutationSolution generatedSolution) {
        return evaluator.computeEnhancementChangeHost(originalSolution, generatedSolution, this);
    }

    public int[] getChildrenPositions() {
        return childrenPositions;
    }

    public int[] getParentPositions() {
        return parentPositions;
    }
}
