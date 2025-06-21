package com.uniovi.sercheduler.localsearch.algorithms;

import com.uniovi.sercheduler.localsearch.observer.LocalSearchObserver;

public abstract class AbstractStrategy {

    private final LocalSearchObserver observer;

    public AbstractStrategy(LocalSearchObserver observer) {
        this.observer = observer;
    }

    protected LocalSearchObserver getObserver() {
        return observer;
    }

}
