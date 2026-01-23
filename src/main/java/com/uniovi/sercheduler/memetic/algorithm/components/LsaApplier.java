package com.uniovi.sercheduler.memetic.algorithm.components;

import com.uniovi.sercheduler.jmetal.problem.SchedulePermutationSolution;
import com.uniovi.sercheduler.localsearch.algorithms.localsearchalgorithm.LocalSearchAlgorithm;
import com.uniovi.sercheduler.localsearch.operator.NeighborhoodOperatorLazy;
import com.uniovi.sercheduler.memetic.observer.MemeticObserver;

import java.util.List;

public interface LsaApplier {

    void applyLSA(List<SchedulePermutationSolution> population, LocalSearchAlgorithm lsa, List<NeighborhoodOperatorLazy> operatorList, MemeticObserver observer);
}
