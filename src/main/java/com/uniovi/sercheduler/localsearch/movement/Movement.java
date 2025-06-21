package com.uniovi.sercheduler.localsearch.movement;

public interface Movement {

    int getFirstChangePosition();

    int[] changedHostPositions();
}
