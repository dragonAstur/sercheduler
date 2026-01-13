package com.uniovi.sercheduler.memetic.algorithm.components;

import org.uma.jmetal.util.errorchecking.Check;

import java.util.Map;

public class MemeticTerminationByEvaluations implements MemeticTermination {
    private final int maximumNumberOfEvaluations;

    public MemeticTerminationByEvaluations(int maximumNumberOfEvaluations) {
        this.maximumNumberOfEvaluations = maximumNumberOfEvaluations;
    }

    public boolean isMet(Map<String, Object> algorithmStatusData) {
        Check.notNull(algorithmStatusData.get("EVALUATIONS"));
        int currentNumberOfEvaluations = (Integer)algorithmStatusData.get("EVALUATIONS");
        return currentNumberOfEvaluations >= this.maximumNumberOfEvaluations;
    }

    public int getMaximumNumberOfEvaluations() {
        return this.maximumNumberOfEvaluations;
    }
}