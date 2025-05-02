package com.uniovi.sercheduler.localsearch.observer;

import java.util.List;

public record ExecutionMetrics(String strategyName,
                               double reachedCost,
                               long executionTime,
                               int numberOfIterations,
                               List<Integer> numberOfGeneratedNeighborsList,
                               List<Double> betterNeighborsRatioList,
                               List<Double> allNeighborsImprovingRatioList,
                               List<Double> betterNeighborsImprovingRatioList) {}
