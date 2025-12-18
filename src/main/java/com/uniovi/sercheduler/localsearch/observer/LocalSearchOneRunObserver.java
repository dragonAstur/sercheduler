package com.uniovi.sercheduler.localsearch.observer;

import java.util.ArrayList;

/**
 * This observer just registers metrics for one run, instead of multiple runs like the class "LocalSearchObserver"
 */
public class LocalSearchOneRunObserver extends AbstractLocalSearchObserver {

    private RunMetrics runMetrics;

    public LocalSearchOneRunObserver(String strategyName, String operatorsName, long periodicTimeForMakespanEvolution) {
        super(strategyName, operatorsName, periodicTimeForMakespanEvolution);
    }

    @Override
    public void endRun() {
        if (getStarts().isEmpty())
            endStart();

        long executionTime = System.currentTimeMillis() - getStartingTime();

        this.runMetrics = new RunMetrics(getStrategyName(), getStarts(), executionTime, getEvolutionMetrics());

        setStarts( new ArrayList<>() );

        setRunStartingTime(-1);
    }

    public RunMetrics getRunMetrics() {
        return runMetrics;
    }
}
