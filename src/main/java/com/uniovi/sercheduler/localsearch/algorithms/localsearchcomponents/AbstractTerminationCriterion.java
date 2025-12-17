package com.uniovi.sercheduler.localsearch.algorithms.localsearchcomponents;

public abstract class AbstractTerminationCriterion implements TerminationCriterion{

    protected boolean upgradeFound;
    protected long startingTime;
    protected final long limitTime;
    protected final long limitIteration;
    protected long actualIteration;

    public AbstractTerminationCriterion(long limitTime, long limitIteration){
        this.upgradeFound = false;
        this.limitTime = limitTime;
        this.limitIteration = limitIteration;
        this.startingTime = -1;
        this.actualIteration = 0;
    }

    @Override
    public void setUpgradeFound(boolean upgradeFound) {
        this.upgradeFound = upgradeFound;
    }

    @Override
    public long startTimeCounter() {
        this.startingTime = System.currentTimeMillis();
        return startingTime;
    }

    @Override
    public boolean hasTimeExceeded() {
        return (System.currentTimeMillis() - startingTime) > limitTime;
    }

    @Override
    public void setActualIteration(long actualIteration) {
        this.actualIteration = actualIteration;
    }
}
