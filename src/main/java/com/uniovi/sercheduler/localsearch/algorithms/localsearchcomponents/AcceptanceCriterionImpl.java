package com.uniovi.sercheduler.localsearch.algorithms.localsearchcomponents;

import com.uniovi.sercheduler.jmetal.problem.SchedulePermutationSolution;
import com.uniovi.sercheduler.localsearch.operator.GeneratedNeighbor;

public class AcceptanceCriterionImpl implements AcceptanceCriterion {

    private final double UPGRADE_THRESHOLD = 0.01;

    public boolean checkAcceptance(SchedulePermutationSolution actualSolution, SchedulePermutationSolution bestNeighborSolution){
        return checkMakespanAcceptance(actualSolution.getFitnessInfo().fitness().get("makespan"),
                bestNeighborSolution.getFitnessInfo().fitness().get("makespan"));
    }

    boolean checkMakespanAcceptance(double actualMakespan, double neighborMakespan) {
        return actualMakespan - neighborMakespan > UPGRADE_THRESHOLD;
    }

}
