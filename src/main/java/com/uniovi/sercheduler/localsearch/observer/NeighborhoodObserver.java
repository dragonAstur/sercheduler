package com.uniovi.sercheduler.localsearch.observer;

import java.util.ArrayList;
import java.util.List;

public class NeighborhoodObserver implements Observer {

    private final List<ExecutionMetrics> executions;

    private final String strategyName;
    private double totalReachedMakespan;
    private long executionTime;
    private int numberOfIterations;
    private List<Integer> numberOfGeneratedNeighborsList;
    private List<Double> reachedMakespanList;
    private List<Double> betterNeighborsRatioList;
    private List<Double> allNeighborsImprovingRatioList;
    private List<Double> betterNeighborsImprovingRatioList;

    public NeighborhoodObserver(String strategyName){
        this.strategyName = strategyName;
        this.executions = new ArrayList<>();
    }

    public List<ExecutionMetrics> getExecutions() {
        return executions;
    }

    public void executionEnded(){
        executions.add(
                new ExecutionMetrics(
                        strategyName,
                        totalReachedMakespan,
                        executionTime,
                        numberOfIterations,
                        new ArrayList<>(numberOfGeneratedNeighborsList),
                        new ArrayList<>(reachedMakespanList),
                        new ArrayList<>(betterNeighborsRatioList),
                        new ArrayList<>(allNeighborsImprovingRatioList),
                        new ArrayList<>(betterNeighborsImprovingRatioList)
                )
        );
    }

    public void executionStarted(){
        totalReachedMakespan = 0.0;
        executionTime = 0L;
        numberOfIterations = 0;
        numberOfGeneratedNeighborsList = new ArrayList<>();
        reachedMakespanList = new ArrayList<>();
        betterNeighborsRatioList = new ArrayList<>();
        allNeighborsImprovingRatioList = new ArrayList<>();
        betterNeighborsImprovingRatioList = new ArrayList<>();
    }

    public void setTotalReachedMakespan(double totalReachedMakespan) {
        this.totalReachedMakespan = totalReachedMakespan;
    }

    public void setExecutionTime(long executionTime) {
        this.executionTime = executionTime;
    }

    public void setNumberOfIterations(int numberOfIterations) {
        this.numberOfIterations = numberOfIterations;
    }

    public void addNumberOfGeneratedNeighbors(Integer value){
        numberOfGeneratedNeighborsList.add(value);
    }

    public void addReachedMakespan(Double value){
        reachedMakespanList.add(value);
    }

    public void addBetterNeighborsRatio(Double value){
        betterNeighborsRatioList.add(value);
    }

    public void addAllNeighborsImprovingRatio(Double value){
        allNeighborsImprovingRatioList.add(value);
    }

    public void addBetterNeighborsImprovingRatio(Double value){
        betterNeighborsImprovingRatioList.add(value);
    }

    @Override
    public String toString() {

        StringBuilder result = new StringBuilder("\n----------------------------------------------------------------------\n");

        for(int i = 0; i < executions.size(); i++){

            result.append("\n\nResults for execution number ")
                    .append(i + 1)
                    .append(":\n")
                    .append("\t--> Strategy used: ")
                    .append(executions.get(i).strategyName())
                    .append("\n")
                    .append("\t--> Best makespan reached: ")
                    .append(executions.get(i).totalReachedMakespan())
                    .append("\n")
                    .append("\t--> Executing time: ")
                    .append(executions.get(i).executionTime())
                    .append("\n").append("\t--> Number of local search iterations: ")
                    .append(executions.get(i).numberOfIterations())
                    .append("\n");

            result.append("\t--> Metrics for each iteration\n");

            for(int j = 0; j < executions.get(i).numberOfIterations()-1; j++){
                result.append("\t\tResults for iteration number ")
                        .append(j+1)
                        .append(":\n")
                        .append("\t\t\t--> Number of generated neighbors: ")
                        .append(executions.get(i).numberOfGeneratedNeighborsList().get(j))
                        .append("\n");
                if(!executions.get(i).betterNeighborsRatioList().isEmpty()) {
                    result.append("\t\t\t--> Percentage of neighbors that outperform their source solution: ")
                            .append(executions.get(i).betterNeighborsRatioList().get(j))
                            .append("\n")
                            .append("\t\t\t--> Improvement ratio from all neighbors: ")
                            .append(executions.get(i).allNeighborsImprovingRatioList().get(j))
                            .append("\n")
                            .append("\t\t\t--> Improvement ratio from neighbors that outperform their source solution: ")
                            .append(executions.get(i).betterNeighborsImprovingRatioList().get(j))
                            .append("\n");
                }
            }

        }

        result.append("\n----------------------------------------------------------------------\n");

        return result.toString();
    }

    public double avgReachedCost(){
        return executions.stream().mapToDouble(ExecutionMetrics::totalReachedMakespan).sum() / executions.size();
    }

    public double avgExecutionTime(){
        return executions.stream().mapToDouble(ExecutionMetrics::executionTime).sum() / executions.size();
    }

    public double avgIterations(){
        return executions.stream().mapToDouble(ExecutionMetrics::numberOfIterations).sum() / executions.size();
    }

    public double avgGeneratedNeighbors(){
        return executions.stream().mapToDouble(ExecutionMetrics::avgGeneratedNeighbors).sum() / executions.size();
    }

    public String getStrategyName(){
        return strategyName;
    }
}
