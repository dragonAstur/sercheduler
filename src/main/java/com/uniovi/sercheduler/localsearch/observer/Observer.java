package com.uniovi.sercheduler.localsearch.observer;

public interface Observer {

    void startRun(long startingTime);

    void endRun();

    void endStart();

    long getStartingTime();

    void endMemeticIteration();

    void endLSAIteration();

    void setNumberOfGeneratedNeighbors(int numberOfGeneratedNeighbors);

    void setBetterNeighborsRatio(double betterNeighborsRatio);

    void setAllNeighborsImprovingRatio(double allNeighborsImprovingRatio);

    void setBetterNeighborsImprovingRatio(double betterNeighborsImprovingRatio);

    void setReachedMakespan(double reachedMakespan);

    void updateLSAEvolution(double actualMakespan, long actualIterationNumberOfNeighbors);

    void updateMemeticEvolution(double actualMakespan, long actualLSAInvocations, long actualLSAImprovements);

    long getPeriodicTimeForMakespanEvolution();

    String getStrategyName();

    String getOperatorsName();
}
