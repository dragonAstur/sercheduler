package com.uniovi.sercheduler.localsearch.algorithms.localsearchcomponents;

public class UpgradeIterationAndTimeLimitTermination implements TerminationCriterion {

    private boolean upgradeFound;
    private long startingTime;
    private final long limitTime;
    private final long limitIteration;
    private long actualIteration;

    public UpgradeIterationAndTimeLimitTermination(long limitTime, long limitIteration){
        this.upgradeFound = false;
        this.limitTime = limitTime;
        this.limitIteration = limitIteration;
    }

    /**
     * This method checks if any of the termination conditions is met
     *
     * @return true if the algorithm can continue and false if the termination condition is met
     */
    @Override
    public boolean isMet(){
        return !upgradeFound || (System.currentTimeMillis() - startingTime) > limitTime || actualIteration > limitIteration;
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
