package com.uniovi.sercheduler.memetic.algorithm.components;

import org.uma.jmetal.solution.Solution;

import java.util.List;

public interface MemeticReplacement<S extends Solution<?>> {
    List<S> replace(List<S> var1, List<S> var2);

    public static enum RemovalPolicy {
        SEQUENTIAL,
        ONE_SHOT;

        private RemovalPolicy() {
        }
    }
}
