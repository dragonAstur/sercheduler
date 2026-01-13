package com.uniovi.sercheduler.memetic.algorithm.components;

import org.uma.jmetal.solution.Solution;

import java.util.List;

public interface MemeticSolutionsCreation<S extends Solution<?>> {
    List<S> create();
}
