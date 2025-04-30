package com.uniovi.sercheduler.localsearch.observer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NeighborhoodObserver implements Observer {

    private final List<Map<String, Double>> values;

    private int iteration;

    public NeighborhoodObserver(){
        this.values = new ArrayList<>();
        this.iteration = -1;
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

    public void setReachedCost(double reachedCost){
        values.get(iteration).put("reached_cost", reachedCost);
    }

    public void setExecutingTime(double executingTime){
        values.get(iteration).put("executing_time", executingTime);
    }

    public void setAvgNeighborsNumber(double value){
        values.get(iteration).put("avg_neighbors_number", value);
    }

    public void setAvgBetterNeighborsRatio(double value){
        values.get(iteration).put("avg_better_neighbors_ratio", value);
    }

    public void setAvgAllNeighborsImprovingRatio(double value){
        values.get(iteration).put("avg_all_neighbors_improving_ratio", value);
    }

    public void setAvgBetterNeighborsImprovingRatio(double value){
        values.get(iteration).put("avg_better_neighbors_improving_ratio", value);
    }

    public void setLocalSearchIterations(double value){
        values.get(iteration).put("local_search_iterations", value);
    }

    @Override
    public String toString() {
        return "Developing...";
    }
}
