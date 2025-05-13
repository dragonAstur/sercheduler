package com.uniovi.sercheduler.localsearch.observer;

import java.util.ArrayList;
import java.util.List;

public record ExecutionMetrics(String strategyName,
                               double bestReachedMakespan,
                               double worstReachedMakespan,
                               long executionTime,
                               int numberOfIterations,
                               List<Integer> numberOfGeneratedNeighborsList,
                               List<Double> reachedMakespanList,
                               List<Double> betterNeighborsRatioList,
                               List<Double> allNeighborsImprovingRatioList,
                               List<Double> betterNeighborsImprovingRatioList) {

    public double avgGeneratedNeighbors(){
        return numberOfGeneratedNeighborsList.stream().mapToInt(Integer::intValue).sum() * 1.0 / numberOfIterations;
    }

    public double avgBetterNeighborsRatio(){
        if(betterNeighborsRatioList.isEmpty())
            throw new IllegalStateException();
        return betterNeighborsRatioList.stream().mapToDouble(Double::doubleValue).sum() / numberOfIterations;
    }

    public double avgAllNeighborsImprovingRatio(){
        if(allNeighborsImprovingRatioList.isEmpty())
            throw new IllegalStateException();
        return allNeighborsImprovingRatioList.stream().mapToDouble(Double::doubleValue).sum() / numberOfIterations;
    }

    public double avgBetterNeighborsImprovingRatio(){
        if(betterNeighborsImprovingRatioList.isEmpty())
            throw new IllegalStateException();
        return betterNeighborsImprovingRatioList.stream().mapToDouble(Double::doubleValue).sum() / numberOfIterations;
    }

    public List<Double> improvementRatioWithRespectToLastIteration(){
        List<Double> result = new ArrayList<>();

        for(int i = 0; i < reachedMakespanList.size(); i++){
            if(i == 0)
                result.add(0.0);
            else
                result.add( (reachedMakespanList.get(i-1) - reachedMakespanList.get(i)) / reachedMakespanList.get(i-1) * 100);
        }

        return result;
    }

}
