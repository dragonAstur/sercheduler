package com.uniovi.sercheduler.localsearch.observer;

import com.uniovi.sercheduler.jmetal.problem.SchedulePermutationSolution;

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

    void updateMemeticEvolution(SchedulePermutationSolution actualSolution); //, long actualLSAInvocations, long actualLSAImprovements);

    void updateMemeticEvolution();

    long getPeriodicTimeForMakespanEvolution();

    String getStrategyName();

    String getOperatorsName();

    void incrementLSAImprovement();

    void incrementLSAIteration();

    void incrementLSAInvocation();

    void incrementEvaluatedNeighbor();

    void updateLSAInvocationsAndIterations();
}
