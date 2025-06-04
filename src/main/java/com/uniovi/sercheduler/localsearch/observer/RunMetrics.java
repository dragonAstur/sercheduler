package com.uniovi.sercheduler.localsearch.observer;

import java.util.List;

public record RunMetrics (
        List<StartMetrics> starts
) {
    public int numberOfStarts(){
        return starts.size();
    }

    public double avgGeneratedNeighborsMonoStart(){
        return starts.getFirst().avgNumberOfGeneratedNeighbors();
    }

    public double avgGeneratedNeighborsMultiStart(){
        return starts.stream().mapToDouble(StartMetrics::numberOfGeneratedNeighbors).average().orElse(0.0);
    }

    public double avgBetterNeighborsRatio(){
        return starts.getFirst().avgBetterNeighborsRatio();
    }

    public double avgBetterNeighborsImprovingRatio(){
        return starts.getFirst().avgBetterNeighborsImprovingRatio();
    }

    public double avgAllNeighborsImprovingRatio(){
        return starts.getFirst().avgAllNeighborsImprovingRatio();
    }

    public double minStartReachedMakespan(){
        return starts.stream().mapToDouble(StartMetrics::startMinReachedMakespan).min().orElse(0.0);
    }

    public double maxStartReachedMakespan(){
        return starts.stream().mapToDouble(StartMetrics::startMinReachedMakespan).max().orElse(0.0);
    }

    public double avgStartReachedMakespan(){
        return starts.stream().mapToDouble(StartMetrics::startMinReachedMakespan).average().orElse(0.0);
    }
}
