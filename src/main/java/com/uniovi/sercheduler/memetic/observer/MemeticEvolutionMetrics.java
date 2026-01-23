package com.uniovi.sercheduler.memetic.observer;

import java.util.ArrayList;
import java.util.List;

public class MemeticEvolutionMetrics {

    private final List<Integer> memeticIterationNumberList;
    private final long periodicTimeForMakespanEvolution;
    private final List<Long> instants;
    private long lastRecordedTime;
    private double bestMakespan;
    private final List<Double> actualMakespanEvolution;
    private final List<Double> bestMakespanEvolution;

    //TODO: ¿número de individuos usados en búsqueda local? ¿número de individuos mejorados en búsqueda local?


    public MemeticEvolutionMetrics(long periodicTimeForMakespanEvolution) {

        this.periodicTimeForMakespanEvolution = periodicTimeForMakespanEvolution;

        this.memeticIterationNumberList = new ArrayList<>();
        this.instants = new ArrayList<>();
        this.lastRecordedTime = -1;
        this.bestMakespan = Double.MAX_VALUE;
        this.bestMakespanEvolution = new ArrayList<>();
        this.actualMakespanEvolution = new ArrayList<>();

    }

    public void update(long memeticStartingTime, int memeticIterationNumber, double actualMakespan){

        updateBestMakespan(actualMakespan);

        if(this.periodicTimeForMakespanEvolution > 0L) {

            long actualTime = System.currentTimeMillis();

            this.lastRecordedTime = this.lastRecordedTime <= 0 ? memeticStartingTime : this.lastRecordedTime;

            long elapsedTime = actualTime - this.lastRecordedTime;

            if (elapsedTime >= this.periodicTimeForMakespanEvolution) {

                saveMetrics(
                        memeticIterationNumber,
                        actualTime - memeticStartingTime,
                        actualMakespan
                );

                this.lastRecordedTime = actualTime;
            }

        }
    }

    private void updateBestMakespan(double actualMakespan){
        this.bestMakespan = Math.min(actualMakespan, this.bestMakespan);
    }

    private void saveMetrics(int memeticIterationNumber, long instant, double actualMakespan){

        this.memeticIterationNumberList.add(memeticIterationNumber);
        this.instants.add(instant);

        this.bestMakespanEvolution.add(this.bestMakespan);
        this.actualMakespanEvolution.add(actualMakespan);

    }

    public List<Integer> getMemeticIterationNumberList() {
        return memeticIterationNumberList;
    }

    public List<Long> getInstants() {
        return instants;
    }

    public List<Double> getActualMakespanEvolution() {
        return actualMakespanEvolution;
    }

    public List<Double> getBestMakespanEvolution() {
        return bestMakespanEvolution;
    }
}
