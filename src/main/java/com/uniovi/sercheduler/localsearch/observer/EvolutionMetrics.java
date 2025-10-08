package com.uniovi.sercheduler.localsearch.observer;

import java.util.ArrayList;
import java.util.List;

public class EvolutionMetrics {

    private List<Integer> startNumberList;

    private List<Integer> iterationNumberList;

    private final long periodicTimeForMakespanEvolution;

    private List<Long> instants;

    private List<Double> bestMakespanEvolution;

    private List<Double> actualMakespanEvolution;

    private long lastRecordedTime;
    private double bestMakespan;

    private List<Long> accNumberOfNeighborsList;


    public EvolutionMetrics(long periodicTimeForMakespanEvolution){

        this.periodicTimeForMakespanEvolution = periodicTimeForMakespanEvolution;

        this.startNumberList = new ArrayList<>();
        this.iterationNumberList = new ArrayList<>();
        this.instants = new ArrayList<>();
        this.bestMakespanEvolution = new ArrayList<>();
        this.actualMakespanEvolution = new ArrayList<>();
        this.accNumberOfNeighborsList = new ArrayList<>();

        this.lastRecordedTime = -1;
        this.bestMakespan = Double.MAX_VALUE;
    }

    public void update(long runStartingTime,
                       int startNumber, int iterationNumber, double actualMakespan, long accNumberOfNeighbors){

        if(periodicTimeForMakespanEvolution > 0L) {

            long actualTime = System.currentTimeMillis();

            lastRecordedTime = lastRecordedTime <= 0 ? runStartingTime : lastRecordedTime;

            long elapsedTime = actualTime - lastRecordedTime;

            if (elapsedTime >= periodicTimeForMakespanEvolution) {

                saveMetrics(
                        startNumber, iterationNumber,
                        actualTime - runStartingTime,
                        actualMakespan,
                        accNumberOfNeighbors
                );

                lastRecordedTime = actualTime;
            }

        }

    }

    private void saveMetrics(int startNumber, int iterationNumber, long instant, double actualMakespan, long accNumberOfNeighbors){

        startNumberList.add(startNumber);
        iterationNumberList.add(iterationNumber);
        instants.add(instant);

        bestMakespan = Math.min(actualMakespan, bestMakespan);

        bestMakespanEvolution.add(bestMakespan);
        actualMakespanEvolution.add(actualMakespan);
        accNumberOfNeighborsList.add(accNumberOfNeighbors);

    }

    public List<Double> getBestMakespanEvolution() {
        return bestMakespanEvolution;
    }

    public List<Long> getInstants() {
        return instants;
    }

    public List<Integer> getStartNumberList() {
        return startNumberList;
    }

    public List<Integer> getIterationNumberList() {
        return iterationNumberList;
    }

    public List<Double> getActualMakespanEvolution() {
        return actualMakespanEvolution;
    }

    public List<Long> getAccNumberOfNeighborsList() {
        return accNumberOfNeighborsList;
    }
}
