package com.uniovi.sercheduler.localsearch.algorithms.localsearchcomponents;

public class UpgradeAndTimeLimitTermination extends AbstractTerminationCriterion {


    public UpgradeAndTimeLimitTermination(long limitTime, long limitIteration) {
        super(limitTime, limitIteration);
    }

    @Override
    public boolean isMet(){
        return !upgradeFound || (System.currentTimeMillis() - startingTime) > limitTime;
    }


}
