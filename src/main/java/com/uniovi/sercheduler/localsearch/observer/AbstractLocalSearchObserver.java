package com.uniovi.sercheduler.localsearch.observer;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractLocalSearchObserver implements Observer{

    private final long periodicTimeForMakespanEvolution;

    private long runStartingTime;

    private final String strategyName;
    private final String operatorsName;

    private List<StartMetrics> starts;

    private List<IterationMetrics> iterations;

    private double reachedMakespan;
    private int numberOfGeneratedNeighbors;
    private double betterNeighborsRatio;
    private double betterNeighborsImprovingRatio;
    private double allNeighborsImprovingRatio;
    private double reachedMakespanImprovingRatioWithRespectLastIteration;

    private LocalSearchEvolutionMetrics localSearchEvolutionMetrics;


    public AbstractLocalSearchObserver(String strategyName, String operatorsName, long periodicTimeForMakespanEvolution){

        this.periodicTimeForMakespanEvolution = periodicTimeForMakespanEvolution;

        this.runStartingTime = -1;

        this.strategyName = strategyName;
        this.operatorsName = operatorsName;

        starts = new ArrayList<>();

        iterations = new ArrayList<>();

        reachedMakespan = -1;
        numberOfGeneratedNeighbors = -1;
        betterNeighborsRatio = -1;
        betterNeighborsImprovingRatio = -1;
        allNeighborsImprovingRatio = -1; //This could be possible, be careful
        reachedMakespanImprovingRatioWithRespectLastIteration = -1;
    }

    @Override
    public void updateMemeticEvolution(double actualMakespan, long actualLSAInvocations, long actualLSAImprovements){

    }

    @Override
    public void startRun(long startingTime) {
        this.runStartingTime = startingTime;

        this.localSearchEvolutionMetrics = new LocalSearchEvolutionMetrics(periodicTimeForMakespanEvolution);
    }

    @Override
    public long getStartingTime() {
        return this.runStartingTime;
    }

    public void setRunStartingTime(long runStartingTime) {
        this.runStartingTime = runStartingTime;
    }

    @Override
    public void endStart() {
        starts.add(
                new StartMetrics(iterations)
        );

        iterations = new ArrayList<>();
    }

    @Override
    public void endMemeticIteration(){

    }

    @Override
    public void endLSAIteration() {
        iterations.add(
                new IterationMetrics(
                        reachedMakespan,
                        numberOfGeneratedNeighbors,
                        betterNeighborsRatio,
                        betterNeighborsImprovingRatio,
                        allNeighborsImprovingRatio,
                        reachedMakespanImprovingRatioWithRespectLastIteration
                )
        );

        reachedMakespan = -1;
        numberOfGeneratedNeighbors = -1;
        betterNeighborsRatio = -1;
        betterNeighborsImprovingRatio = -1;
        allNeighborsImprovingRatio = -1; //This could be possible, be careful
        reachedMakespanImprovingRatioWithRespectLastIteration = -1;
    }

    @Override
    public void setNumberOfGeneratedNeighbors(int numberOfGeneratedNeighbors) {
        this.numberOfGeneratedNeighbors = numberOfGeneratedNeighbors;
    }

    @Override
    public void setBetterNeighborsRatio(double betterNeighborsRatio) {
        this.betterNeighborsRatio = betterNeighborsRatio;
    }

    @Override
    public void setAllNeighborsImprovingRatio(double allNeighborsImprovingRatio) {
        this.allNeighborsImprovingRatio = allNeighborsImprovingRatio;
    }

    @Override
    public void setBetterNeighborsImprovingRatio(double betterNeighborsImprovingRatio) {
        this.betterNeighborsImprovingRatio = betterNeighborsImprovingRatio;
    }

    @Override
    public void setReachedMakespan(double reachedMakespan) {
        this.reachedMakespan = reachedMakespan;
    }

    @Override
    public void updateLSAEvolution(double actualMakespan, long actualIterationNumberOfNeighbors) {

        long accNumberOfNeighbors = actualIterationNumberOfNeighbors +
                starts.stream().mapToInt(StartMetrics::numberOfGeneratedNeighbors).sum() +
                iterations.stream().mapToInt(IterationMetrics::numberOfGeneratedNeighbors).sum();

        localSearchEvolutionMetrics.update(
                runStartingTime,
                starts.size()+1,
                iterations.size() + 1,
                actualMakespan,
                accNumberOfNeighbors
        );
    }

    @Override
    public long getPeriodicTimeForMakespanEvolution() {
        return periodicTimeForMakespanEvolution;
    }


    @Override
    public String getStrategyName(){
        return strategyName;
    }

    @Override
    public String getOperatorsName() {
        return operatorsName;
    }

    public List<StartMetrics> getStarts() {
        return new ArrayList<>(starts);
    }

    public void setStarts(List<StartMetrics> starts) {
        this.starts = starts;
    }

    public LocalSearchEvolutionMetrics getEvolutionMetrics() {
        return localSearchEvolutionMetrics;
    }
}
