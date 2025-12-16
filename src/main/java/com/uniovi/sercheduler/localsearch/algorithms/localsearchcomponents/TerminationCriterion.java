package com.uniovi.sercheduler.localsearch.algorithms.localsearchcomponents;

public interface TerminationCriterion {

    boolean isMet();

    void setUpgradeFound(boolean upgradeFound);

    long startTimeCounter();

    boolean hasTimeExceeded();

    void setActualIteration(long actualIteration);
}
