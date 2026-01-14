package com.uniovi.sercheduler.memetic.observer;

import com.uniovi.sercheduler.localsearch.observer.Observer;

import java.util.ArrayList;

public class MemeticObserver implements Observer {

    private MemeticEvolutionMetrics memeticEvolutionMetrics;
    private long memeticStartingTime;
    private long memeticExecutionTime;
    private final String strategyName;
    private final String operatorsName;
    //private double reachedMakespan;
    private final long periodicTimeForMakespanEvolution;
    private int memeticIterationNumber;

    public MemeticObserver(String strategyName, String operatorsName, long periodicTimeForMakespanEvolution){

        this.periodicTimeForMakespanEvolution = periodicTimeForMakespanEvolution;

        this.memeticStartingTime = -1;
        this.memeticExecutionTime = -1;

        this.strategyName = strategyName;
        this.operatorsName = operatorsName;

        //this.reachedMakespan = -1;

        this.memeticIterationNumber = 1;

    }

    @Override
    public void startRun(long startingTime) {
        this.memeticStartingTime = startingTime;

        this.memeticEvolutionMetrics = new MemeticEvolutionMetrics(this.periodicTimeForMakespanEvolution);
    }

    @Override
    public void endRun() {

        this.memeticExecutionTime = System.currentTimeMillis() - getStartingTime();

    }

    public MemeticEvolutionMetrics getMemeticEvolutionMetrics() {
        return this.memeticEvolutionMetrics;
    }

    @Override
    public void endStart() {

    }

    @Override
    public long getStartingTime() {
        return this.memeticStartingTime;
    }

    @Override
    public void endMemeticIteration() {
        this.memeticIterationNumber += 1;
    }

    @Override
    public void endLSAIteration() {

    }

    @Override
    public void setNumberOfGeneratedNeighbors(int numberOfGeneratedNeighbors) {

    }

    @Override
    public void setBetterNeighborsRatio(double betterNeighborsRatio) {

    }

    @Override
    public void setAllNeighborsImprovingRatio(double allNeighborsImprovingRatio) {

    }

    @Override
    public void setBetterNeighborsImprovingRatio(double betterNeighborsImprovingRatio) {

    }

    @Override
    public void setReachedMakespan(double reachedMakespan) {

    }

    @Override
    public void updateLSAEvolution(double actualMakespan, long actualIterationNumberOfNeighbors) {

        //TODO: revisar qué makespan poner aquí
        this.memeticEvolutionMetrics.update(
                this.memeticStartingTime,
                this.memeticIterationNumber,
                lastRecordedMakespan()
        );
    }

    public double lastRecordedMakespan(){

        int lastMakespanRecordedPos = this.memeticEvolutionMetrics.getActualMakespanEvolution().size()-1;
        double lastMakespanRecorded = Double.MAX_VALUE;

        if(lastMakespanRecordedPos != -1)
            lastMakespanRecorded = this.memeticEvolutionMetrics.getActualMakespanEvolution().get(lastMakespanRecordedPos);

        return lastMakespanRecorded;
    }

    @Override
    public void updateMemeticEvolution(double actualMakespan, long actualLSAInvocations, long actualLSAImprovements) {

        //TODO: ¿número de individuos usados en búsqueda local? ¿número de individuos mejorados en búsqueda local?

        this.memeticEvolutionMetrics.update(this.memeticStartingTime, this.memeticIterationNumber, actualMakespan);
    }

    @Override
    public long getPeriodicTimeForMakespanEvolution() {
        return this.periodicTimeForMakespanEvolution;
    }

    @Override
    public String getStrategyName() {
        return this.strategyName;
    }

    @Override
    public String getOperatorsName() {
        return this.operatorsName;
    }
}
