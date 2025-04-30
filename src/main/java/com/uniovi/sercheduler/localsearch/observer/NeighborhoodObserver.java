package com.uniovi.sercheduler.localsearch.observer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NeighborhoodObserver implements Observer {

    private final List<Map<String, Double>> values;

    private int iteration;
    private long executingTime;
    private double reachedCost;

    public NeighborhoodObserver(){
        this.values = new ArrayList<>();
        this.iteration = -1;
        this.executingTime = -1;
        this.reachedCost = -1;
    }

    public List<Map<String, Double>> getValues(){
        return new ArrayList<>(values);
    }

    private void addDictionary(){
        values.add(new HashMap<>());
    }

    public int getIteration(){
        return iteration+1;
    }

    public void newIteration(){
        iteration += 1;
        addDictionary();
    }

    public void reset(){
        iteration = -1;
    }

    public void setReachedCost(double reachedCost){
        this.reachedCost = reachedCost;
    }

    public void setExecutingTime(long executingTime){
        this.executingTime = executingTime;
    }

    public void setNeighborsNumber(double value){
        values.get(iteration).put("neighbors_number", value);
    }

    public void setBetterNeighborsRatio(double value){
        values.get(iteration).put("improving_neighbors_ratio", value);
    }

    public void setAllNeighborsImprovingRatio(double value){
        values.get(iteration).put("all_neighbors_improving_ratio", value);
    }

    public void setBetterNeighborsImprovingRatio(double value){
        values.get(iteration).put("better_neighbors_improving_ratio", value);
    }

    @Override
    public String toString() {
        return "NeighborhoodObserver{" +
                "iteration=" + getIteration() +
                ", executingTime=" + executingTime +
                ", reachedCost=" + reachedCost +
                '}';
    }
}
