package com.uniovi.sercheduler.util;

import com.uniovi.sercheduler.jmetal.problem.SchedulePermutationSolution;

import java.util.List;

public class NonDominatedChecker {

    public static boolean areAllNonDominated(List<SchedulePermutationSolution> solutions) {
        for (int i = 0; i < solutions.size(); i++) {
            var a = solutions.get(i).getFitnessInfo().fitness();
            for (int j = 0; j < solutions.size(); j++) {
                if (i == j) continue;

                var b = solutions.get(j).getFitnessInfo().fitness();

                boolean dominates =
                        b.get("energy") <= a.get("energy") &&
                                b.get("makespan") <= a.get("makespan") &&
                                (b.get("energy") < a.get("energy") || b.get("makespan") < a.get("makespan"));

                if (dominates) {
                    return false;
                }
            }
        }
        return true;
    }
}
