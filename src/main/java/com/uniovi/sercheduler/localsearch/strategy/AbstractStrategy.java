package com.uniovi.sercheduler.localsearch.strategy;

import com.uniovi.sercheduler.localsearch.observer.NeighborhoodObserver;
import com.uniovi.sercheduler.localsearch.observer.Observer;

public abstract class AbstractStrategy {

    private NeighborhoodObserver observer;

    public AbstractStrategy(NeighborhoodObserver observer) {
        this.observer = observer;
    }

    protected NeighborhoodObserver getObserver(){
        return observer;
    }



}
