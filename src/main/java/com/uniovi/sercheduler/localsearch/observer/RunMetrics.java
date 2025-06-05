package com.uniovi.sercheduler.localsearch.observer;

import java.util.List;

public record RunMetrics (
        String strategyName,
        List<StartMetrics> starts,
        long executionTime
) {
    public int numberOfStarts(){
        return starts.size();
    }

    public double avgGeneratedNeighbors(){
        return starts.stream().mapToDouble(StartMetrics::avgNumberOfGeneratedNeighbors).average().orElse(-1);
    }

    public int numberOfGeneratedNeighbors(){
        return starts.stream().mapToInt(StartMetrics::numberOfGeneratedNeighbors).sum();
    }

    public double minStartsReachedMakespan(){
        return starts.stream().mapToDouble(StartMetrics::startMinReachedMakespan).min().orElse(-1);
    }

    /*public double maxStartsReachedMakespan(){
        return starts.stream().mapToDouble(StartMetrics::startMinReachedMakespan).max().orElse(-1);
    }

    public double avgStartsReachedMakespan(){
        return starts.stream().mapToDouble(StartMetrics::startMinReachedMakespan).average().orElse(-1);
    }*/

    public StartMetrics monoStart(){
        return starts().get(0);
    }
}
