package com.uniovi.sercheduler.localsearch.observer;

import java.util.ArrayList;
import java.util.List;

public class LocalSearchEvolutionMetrics {

    private final List<Integer> startNumberList;

    private final List<Integer> iterationNumberList;

    private final long periodicTimeForMakespanEvolution;

    private final List<Long> realInstants;

    private final List<Long> theoreticalInstants;

    private final List<Double> bestMakespanEvolution;

    private final List<Double> actualMakespanEvolution;

    private long lastRecordedTime;
    private double bestMakespan;

    private final List<Long> accNumberOfNeighborsList;


    public LocalSearchEvolutionMetrics(long periodicTimeForMakespanEvolution){

        this.periodicTimeForMakespanEvolution = periodicTimeForMakespanEvolution;

        this.startNumberList = new ArrayList<>();
        this.iterationNumberList = new ArrayList<>();
        this.realInstants = new ArrayList<>();
        this.theoreticalInstants = new ArrayList<>();
        this.bestMakespanEvolution = new ArrayList<>();
        this.actualMakespanEvolution = new ArrayList<>();
        this.accNumberOfNeighborsList = new ArrayList<>();

        this.lastRecordedTime = -1;
        this.bestMakespan = Double.MAX_VALUE;
    }

    public void update(long runStartingTime,
                       int startNumber, int iterationNumber, double actualMakespan, long accNumberOfNeighbors){

        //TODO: se podrían perder ticks, es decir, si se llama con elapsedTimeFromStart = 5032 (por ejemplo),
        // y el último registrado es 2000, se almacenaría como 3000

        if(periodicTimeForMakespanEvolution <= 0L)
            return;

        long actualTime = System.currentTimeMillis();

        long elapsedFromStart = actualTime - runStartingTime;

        long lastTheoreticalInstant =
                theoreticalInstants.isEmpty()
                        ? 0
                        : theoreticalInstants.get(theoreticalInstants.size() - 1);

        long actualTheoreticalInstant = lastTheoreticalInstant + periodicTimeForMakespanEvolution;

        if (elapsedFromStart >= actualTheoreticalInstant) {

            saveMetrics(
                    startNumber,
                    iterationNumber,
                    elapsedFromStart,
                    actualTheoreticalInstant,
                    actualMakespan,
                    accNumberOfNeighbors
            );

        }

    }

    private void saveMetrics(int startNumber, int iterationNumber, long realInstant, long theoreticalInstant,
                             double actualMakespan, long accNumberOfNeighbors){

        startNumberList.add(startNumber);
        iterationNumberList.add(iterationNumber);
        realInstants.add(realInstant);
        theoreticalInstants.add(theoreticalInstant);

        bestMakespan = Math.min(actualMakespan, bestMakespan);

        bestMakespanEvolution.add(bestMakespan);
        actualMakespanEvolution.add(actualMakespan);
        accNumberOfNeighborsList.add(accNumberOfNeighbors);

    }

    public List<Double> getBestMakespanEvolution() {
        return bestMakespanEvolution;
    }

    public List<Long> getRealInstants() {
        return realInstants;
    }

    public List<Long> getTheoreticalInstants() {
        return theoreticalInstants;
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
