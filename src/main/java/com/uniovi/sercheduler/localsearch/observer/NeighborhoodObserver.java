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

        String result = "\n----------------------------------------------------------------------\n";

        int iterationNumber;

        for(int i = 0; i < values.size(); i++){

            iterationNumber = i+1;

            result += "\n\nResults for iteration number " + iterationNumber + ":\n" +
                    "\t--> Best makespan reached: " + values.get(i).get("reached_cost") + "\n" +
                    "\t--> Executing time: " + values.get(i).get("executing_time") + "\n" +
                    "\t--> Number of local search iterations: " + values.get(i).get("local_search_iterations") + "\n" +
                    "\t--> Average generated neighbors: " + values.get(i).get("avg_neighbors_number") + "\n";


            if(values.get(i).containsKey("avg_better_neighbors_ratio")){
                result += "\t--> Average percentage of neighbors that outperform their source solution: " + values.get(i).get("avg_better_neighbors_ratio") + "%\n" +
                        "\t--> Average improvement ratio from all neighbors: " + values.get(i).get("avg_all_neighbors_improving_ratio") + "%\n" +
                        "\t--> Average improvement ratio from neighbors that outperform their source solution: " + values.get(i).get("avg_better_neighbors_improving_ratio") + "%\n";
            }
        }

        result += "\n----------------------------------------------------------------------\n";

        return result;
    }
}
