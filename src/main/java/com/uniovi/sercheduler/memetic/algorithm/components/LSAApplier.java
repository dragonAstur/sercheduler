package com.uniovi.sercheduler.memetic.algorithm.components;

import com.uniovi.sercheduler.jmetal.problem.SchedulePermutationSolution;
import com.uniovi.sercheduler.memetic.observer.MemeticObserver;

import java.util.List;

public interface LSAApplier {

    void applyLSA(List<SchedulePermutationSolution> population, MemeticObserver observer);
}
