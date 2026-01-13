package com.uniovi.sercheduler.memetic.algorithm.components;

import org.uma.jmetal.solution.Solution;

import java.util.List;

public interface MemeticVariation<S extends Solution<?>> {
    List<S> variate(List<S> var1, List<S> var2);

    int getMatingPoolSize();

    int getOffspringPopulationSize();
}
