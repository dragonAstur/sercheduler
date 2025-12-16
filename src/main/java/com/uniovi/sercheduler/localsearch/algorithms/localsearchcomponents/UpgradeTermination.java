package com.uniovi.sercheduler.localsearch.algorithms.localsearchcomponents;

public class UpgradeTermination implements TerminationCriterion {

    private boolean upgradeFound;

    public UpgradeTermination(){
        this.upgradeFound = false;
    }


    @Override
    public boolean isMet() {
        return !upgradeFound;
    }

    @Override
    public void setUpgradeFound(boolean upgradeFound) {
        this.upgradeFound = upgradeFound;
    }

    @Override
    public long startTimeCounter() {
        return System.currentTimeMillis();
    }

    @Override
    public boolean hasTimeExceeded() {
        return false;
    }

    @Override
    public void setActualIteration(long actualIteration) {

    }


}
