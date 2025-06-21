package com.uniovi.sercheduler.localsearch.algorithms.localsearchcomponents;

public class UpgradeAndTimeLimitTermination implements TerminationCriterion {

    private boolean upgradeFound;
    private long startingTime;
    private final long limitTime;

    public UpgradeAndTimeLimitTermination(long limitTime){
        this.upgradeFound = false;
        this.limitTime = limitTime;
    }

    @Override
    public boolean checkTerminationCondition(){
        return upgradeFound && (System.currentTimeMillis() - startingTime) < limitTime;
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


}
