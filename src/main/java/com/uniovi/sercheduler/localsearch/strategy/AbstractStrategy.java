package com.uniovi.sercheduler.localsearch.strategy;

import com.uniovi.sercheduler.localsearch.observer.NeighborhoodObserver;

public abstract class AbstractStrategy {

    private final NeighborhoodObserver observer;

    public AbstractStrategy(NeighborhoodObserver observer) {
        this.observer = observer;
    }

    protected NeighborhoodObserver getObserver(){
        return observer;
    }



}
