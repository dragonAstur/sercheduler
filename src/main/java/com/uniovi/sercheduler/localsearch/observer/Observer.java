package com.uniovi.sercheduler.localsearch.observer;

import java.util.Map;

public interface Observer {

    void startRun(long startingTime);

    void endRun();

    void endStart();

    void endIteration();

    void setNumberOfGeneratedNeighbors(int numberOfGeneratedNeighbors);

    void setBetterNeighborsRatio(double betterNeighborsRatio);

    void setAllNeighborsImprovingRatio(double allNeighborsImprovingRatio);

    void setBetterNeighborsImprovingRatio(double betterNeighborsImprovingRatio);

    void setReachedMakespan(double reachedMakespan);

    void updateMakespanEvolution(double actualMakespan, long actualIterationNumberOfNeighbors);

    long getPeriodicTimeForMakespanEvolution();

    String getStrategyName();

    String getOperatorsName();
}
