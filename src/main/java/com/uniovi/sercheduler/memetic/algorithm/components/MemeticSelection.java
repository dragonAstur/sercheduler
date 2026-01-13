package com.uniovi.sercheduler.memetic.algorithm.components;

import com.uniovi.sercheduler.memetic.observer.MemeticObserver;
import org.uma.jmetal.solution.Solution;

import java.util.List;

public interface MemeticSelection<S extends Solution<?>> {
    List<S> select(List<S> var1, MemeticObserver observer);
}
