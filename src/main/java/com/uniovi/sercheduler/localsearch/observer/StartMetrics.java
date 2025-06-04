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
        return iterations.stream().mapToDouble(IterationMetrics::betterNeighborsRatio).average().orElse(0.0);
    }

    public double avgBetterNeighborsImprovingRatio(){
        return iterations.stream().mapToDouble(IterationMetrics::betterNeighborsImprovingRatio).average().orElse(0.0);
    }

    public double avgAllNeighborsImprovingRatio(){
        return iterations.stream().mapToDouble(IterationMetrics::allNeighborsImprovingRatio).average().orElse(0.0);
    }

    public double startMinReachedMakespan(){
        return iterations.get(numberOfIterations()-1).reachedMakespan();
    }
}
