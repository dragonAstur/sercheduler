package com.uniovi.sercheduler.localsearch.observer;

import java.util.ArrayList;

public class LocalSearchRunObserver extends AbstractLocalSearchObserver {

    private RunMetrics runMetrics;

    public LocalSearchRunObserver(String strategyName, String operatorsName, long periodicTimeForMakespanEvolution) {
        super(strategyName, operatorsName, periodicTimeForMakespanEvolution);
    }

    @Override
    public void endRun() {
        if (getStarts().isEmpty())
            endStart();

        long executionTime = System.currentTimeMillis() - getRunStartingTime();

        this.runMetrics = new RunMetrics(getStrategyName(), getStarts(), executionTime, getEvolutionMetrics());

        setStarts( new ArrayList<>() );

        setRunStartingTime(-1);
    }

    public RunMetrics getRunMetrics() {
        return runMetrics;
    }
}
