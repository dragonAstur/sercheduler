package com.uniovi.sercheduler.localsearch.movement;

public class ChangeHostMovement implements Movement{

    int position;
    int[] childrenPositions;

    public ChangeHostMovement(int position, int[] childrenPositions) {
        this.position = position;
        this.childrenPositions = childrenPositions;
    }
}
