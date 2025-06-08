package com.uniovi.sercheduler.localsearch.observer;

import java.util.ArrayList;
import java.util.List;

public class LocalSearchObserver implements Observer {

    private final List<RunMetrics> runs;

    private final String strategyName;


    private List<StartMetrics> starts;


    private List<IterationMetrics> iterations;


    private double reachedMakespan;
    private int numberOfGeneratedNeighbors;
    private double betterNeighborsRatio;
    private double betterNeighborsImprovingRatio;
    private double allNeighborsImprovingRatio;
    private double reachedMakespanImprovingRatioWithRespectLastIteration;

    private long timeForFindingBestSolution;
    private int generatedNeighborsForFindingBestSolution;

    public LocalSearchObserver(String strategyName, String instanceName){
        this.strategyName = strategyName;
        this.runs = new ArrayList<>();

        starts = new ArrayList<>();

        iterations = new ArrayList<>();

        reachedMakespan = -1;
        numberOfGeneratedNeighbors = -1;
        betterNeighborsRatio = -1;
        betterNeighborsImprovingRatio = -1;
        allNeighborsImprovingRatio = -1; //This could be possible, be careful
        reachedMakespanImprovingRatioWithRespectLastIteration = -1;
    }

    public List<RunMetrics> getRuns() {
        return new ArrayList<>(runs);
    }

    public int numberOfRuns(){
        return runs.size();
    }

    public void endRun(long executionTime) {

        if (starts.isEmpty())
            endStart();

        runs.add(
                new RunMetrics(strategyName, starts, executionTime)
        );

        checkBestSolution();

        starts = new ArrayList<>();
    }

    public void endStart(){
        starts.add(
                new StartMetrics(iterations)
        );

        iterations = new ArrayList<>();
    }

    public void endIteration(){
        iterations.add(
                new IterationMetrics(
                        reachedMakespan,
                        numberOfGeneratedNeighbors,
                        betterNeighborsRatio,
                        betterNeighborsImprovingRatio,
                        allNeighborsImprovingRatio,
                        reachedMakespanImprovingRatioWithRespectLastIteration
                )
        );

        reachedMakespan = -1;
        numberOfGeneratedNeighbors = -1;
        betterNeighborsRatio = -1;
        betterNeighborsImprovingRatio = -1;
        allNeighborsImprovingRatio = -1; //This could be possible, be careful
        reachedMakespanImprovingRatioWithRespectLastIteration = -1;
    }

    public String getStrategyName(){
        return strategyName;
    }

    public double getAvgExecutionTime(){
        return runs.stream().mapToDouble(RunMetrics::executionTime).average().orElse(-1);
    }

    public double avgIterationsMonoStart(){
        return runs.stream().map(RunMetrics::monoStart).mapToDouble(StartMetrics::numberOfIterations).average().orElse(-1);
    }

    public double avgStartsPerRun(){

        return runs.stream().mapToInt(RunMetrics::numberOfStarts).average().orElse(-1);
    }

    public double avgGeneratedNeighborsMonoStart(){
        return runs.stream().map(RunMetrics::monoStart).mapToDouble(StartMetrics::numberOfGeneratedNeighbors).average().orElse(-1);
    }

    public double avgGeneratedNeighborsMultiStart(){
        int totalGeneratedNeighbors = 0;
        for(RunMetrics run : runs)
            totalGeneratedNeighbors += run.starts().stream().mapToInt(StartMetrics::numberOfGeneratedNeighbors).sum();

        return totalGeneratedNeighbors * 1.0 / numberOfRuns();
    }

    public double getBestMinReachedMakespan(){
        return runs.stream().mapToDouble(RunMetrics::minStartsReachedMakespan).min().orElse(-1);
    }

    public double getWorstMinReachedMakespan(){
        return runs.stream().mapToDouble(RunMetrics::minStartsReachedMakespan).max().orElse(-1);
    }

    public double getAvgMinReachedMakespan(){
        return runs.stream().mapToDouble(RunMetrics::minStartsReachedMakespan).average().orElse(-1);
    }

    public double standardDeviation(){

        final double avgReachedMakespan = getAvgMinReachedMakespan();

        return Math.sqrt(
                runs.stream()
                        .mapToDouble(RunMetrics::minStartsReachedMakespan)
                        .map(makespan -> Math.pow(makespan - avgReachedMakespan, 2)).average().orElse(-1)
        );
    }

    public void setReachedMakespan(double reachedMakespan){
        this.reachedMakespan = reachedMakespan;
    }

    public void setBetterNeighborsRatio(double betterNeighborsRatio) {
        this.betterNeighborsRatio = betterNeighborsRatio;
    }

    public void setAllNeighborsImprovingRatio(double allNeighborsImprovingRatio) {
        this.allNeighborsImprovingRatio = allNeighborsImprovingRatio;
    }

    public void setBetterNeighborsImprovingRatio(double betterNeighborsImprovingRatio) {
        this.betterNeighborsImprovingRatio = betterNeighborsImprovingRatio;
    }

    public void setNumberOfGeneratedNeighbors(int numberOfGeneratedNeighbors){
        this.numberOfGeneratedNeighbors = numberOfGeneratedNeighbors;
    }

    private void checkBestSolution() {

        if(runs.get(numberOfRuns()-1).minStartsReachedMakespan() == getBestMinReachedMakespan()){
            computeTimeForFindingBestSolution();
            computeGeneratedNeighborsForFindingBestSolution();
        }

    }

    public void computeTimeForFindingBestSolution(){
        this.timeForFindingBestSolution = runs.stream().mapToLong(RunMetrics::executionTime).sum();
    }

    public void computeGeneratedNeighborsForFindingBestSolution(){
        this.generatedNeighborsForFindingBestSolution = runs.stream().mapToInt(RunMetrics::numberOfGeneratedNeighbors).sum();
    }

    public long getTimeForFindingBestSolution(){
        return timeForFindingBestSolution;
    }

    public int getGeneratedNeighborsForFindingBestSolution(){
        return generatedNeighborsForFindingBestSolution;
    }





    /*
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
                    .append(executions.get(i).bestReachedMakespan())
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
    }*/
}
