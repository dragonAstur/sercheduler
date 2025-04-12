package com.uniovi.sercheduler.localsearch.movement;

public class InsertionMovement implements Movement{

    private int[] changedPlanPairs;
    private int initialPosition;
    private int finalPosition;

    public InsertionMovement(int[] changedPlanPairs, int initialPosition, int finalPosition) {
        this.changedPlanPairs = changedPlanPairs;
        this.initialPosition = initialPosition;
        this.finalPosition = finalPosition;
    }
}
