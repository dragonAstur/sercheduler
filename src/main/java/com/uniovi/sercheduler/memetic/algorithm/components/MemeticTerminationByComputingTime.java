package com.uniovi.sercheduler.memetic.algorithm.components;

import org.uma.jmetal.component.catalogue.common.termination.Termination;
import org.uma.jmetal.util.errorchecking.Check;

import java.util.Map;

public class MemeticTerminationByComputingTime implements MemeticTermination {
    private final long maxComputingTime;
    private int evaluations;

    public MemeticTerminationByComputingTime(long maxComputingTime) {
        this.maxComputingTime = maxComputingTime;
    }

    public boolean isMet(Map<String, Object> algorithmStatusData) {
        Check.notNull(algorithmStatusData.get("COMPUTING_TIME"));
        Check.notNull(algorithmStatusData.get("EVALUATIONS"));
        long currentComputingTime = (Long)algorithmStatusData.get("COMPUTING_TIME");
        this.evaluations = (Integer)algorithmStatusData.get("EVALUATIONS");
        return currentComputingTime >= this.maxComputingTime;
    }

    public int getEvaluations() {
        return this.evaluations;
    }

    public long getMaxComputingTime() {
        return this.maxComputingTime;
    }
}