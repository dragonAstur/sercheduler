package com.uniovi.sercheduler.localsearch.observer;

public record IterationMetrics(
    double reachedMakespan,
    int numberOfGeneratedNeighbors,
    double betterNeighborsRatio,
    double betterNeighborsImprovingRatio,
    double allNeighborsImprovingRatio,
    double improvementRatioWithRespectLastIteration
) { }
