package com.uniovi.sercheduler.localsearch.movement;

import com.uniovi.sercheduler.jmetal.problem.SchedulePermutationSolution;
import com.uniovi.sercheduler.localsearch.evaluator.LocalsearchEvaluator;

public class SwapHostMovement implements Movement {

    private int firstPosition;
    private int secondPosition;

    private int[] firstParentsPositions;
    private int[] firstChildrenPositions;
    private int[] secondParentsPositions;
    private int[] secondChildrenPositions;

    public SwapHostMovement(int firstPosition, int secondPosition, int[] firstParentsPositions, int[] firstChildrenPositions,
                            int[] secondParentsPositions, int[] secondChildrenPositions) {
        this.firstPosition = firstPosition;
        this.secondPosition = secondPosition;
    }

    public int getFirstPosition() {
        return firstPosition;
    }

    public int getSecondPosition() {
        return secondPosition;
    }

    @Override
    public double computeEnhancement(LocalsearchEvaluator evaluator, SchedulePermutationSolution originalSolution, SchedulePermutationSolution generatedSolution) {
        return evaluator.computeEnhancementSwapHost(originalSolution, generatedSolution, this);
    }

    public int[] getFirstParentsPositions() {
        return firstParentsPositions;
    }

    public int[] getFirstChildrenPositions() {
        return firstChildrenPositions;
    }

    public int[] getSecondParentsPositions() {
        return secondParentsPositions;
    }

    public int[] getSecondChildrenPositions() {
        return secondChildrenPositions;
    }
}
