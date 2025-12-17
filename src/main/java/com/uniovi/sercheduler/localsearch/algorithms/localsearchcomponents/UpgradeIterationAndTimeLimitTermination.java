package com.uniovi.sercheduler.localsearch.algorithms.localsearchcomponents;

public class UpgradeIterationAndTimeLimitTermination extends AbstractTerminationCriterion {


    public UpgradeIterationAndTimeLimitTermination(long limitTime, long limitIteration) {
        super(limitTime, limitIteration);
    }

    /**
     * This method checks if any of the termination conditions is met
     *
     * @return true if the algorithm can continue and false if the termination condition is met
     */
    @Override
    public boolean isMet(){
        return !this.upgradeFound || (System.currentTimeMillis() - startingTime) > limitTime || actualIteration >= limitIteration;
    }



}
