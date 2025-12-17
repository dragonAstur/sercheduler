package com.uniovi.sercheduler.localsearch.algorithms.localsearchcomponents;

public class UpgradeTermination extends AbstractTerminationCriterion {


    public UpgradeTermination(long limitTime, long limitIteration) {
        super(limitTime, limitIteration);
    }

    @Override
    public boolean isMet() {
        return !upgradeFound;
    }


}
