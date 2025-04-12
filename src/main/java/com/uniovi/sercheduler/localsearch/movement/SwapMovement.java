package com.uniovi.sercheduler.localsearch.movement;

public class SwapMovement implements Movement{

    private int initialPosition;
    private int finalPosition;

    public SwapMovement(int initialPosition, int finalPosition) {
        this.initialPosition = initialPosition;
        this.finalPosition = finalPosition;
    }

}
