package com.uniovi.sercheduler.memetic.algorithm.components;

import com.uniovi.sercheduler.memetic.observer.MemeticObserver;
import org.uma.jmetal.solution.Solution;

import java.util.List;

public interface MemeticVariation<S extends Solution<?>> {
    List<S> variate(List<S> var1, List<S> var2, MemeticObserver observer);

    int getMatingPoolSize();

    int getOffspringPopulationSize();
}
