package com.uniovi.sercheduler.localsearch.observer;

import java.util.List;

public record StartMetrics (
        List<IterationMetrics> iterations
)   {
    public int numberOfIterations(){
        return iterations.size();
    }

    public int numberOfGeneratedNeighbors(){
        return iterations.stream().mapToInt(IterationMetrics::numberOfGeneratedNeighbors).sum();
    }

    public double avgNumberOfGeneratedNeighbors(){
        return numberOfGeneratedNeighbors() * 1.0 / numberOfIterations();
    }

    public double avgBetterNeighborsRatio(){
        return iterations.stream().mapToDouble(IterationMetrics::betterNeighborsRatio).average().orElse(-1);
    }

    public double avgBetterNeighborsImprovingRatio(){
        return iterations.stream().mapToDouble(IterationMetrics::betterNeighborsImprovingRatio).average().orElse(-1);
    }

    public double avgAllNeighborsImprovingRatio(){
        return iterations.stream().mapToDouble(IterationMetrics::allNeighborsImprovingRatio).average().orElse(-1);
    }

    public double startMinReachedMakespan(){
        return iterations.stream().mapToDouble(IterationMetrics::reachedMakespan).min().orElse(-1);
    }
}
