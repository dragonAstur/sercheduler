package com.uniovi.sercheduler.localsearch.algorithms.localsearchcomponents;

public interface TerminationCriterion {

    boolean checkTerminationCondition();

    void setUpgradeFound(boolean upgradeFound);

    long startTimeCounter();
}
