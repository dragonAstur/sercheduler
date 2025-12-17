package com.uniovi.sercheduler.localsearch.algorithms.localsearchcomponents;

public class UpgradeTermination extends AbstractTerminationCriterion {


    @Override
    public boolean isMet() {
        return !upgradeFound;
    }


}
