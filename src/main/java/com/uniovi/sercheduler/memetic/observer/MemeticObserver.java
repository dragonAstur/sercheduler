package com.uniovi.sercheduler.memetic.observer;

import com.uniovi.sercheduler.jmetal.problem.SchedulePermutationSolution;
import com.uniovi.sercheduler.jmetal.problem.SolutionOrigin;
import com.uniovi.sercheduler.localsearch.observer.Observer;

import java.math.BigDecimal;

public class MemeticObserver implements Observer {

    private MemeticEvolutionMetrics memeticEvolutionMetrics;
    private long memeticStartingTime;
    private long memeticExecutionTime;
    private final String strategyName;
    private final String operatorsName;
    //private double reachedMakespan;
    private final long periodicTimeForMakespanEvolution;
    private int memeticIterationNumber;

    private long numberOfBestSolutionsChangeHostOrigin;
    private long numberOfBestSolutionsInsertionOrigin;
    private long numberOfBestSolutionsSwapOrigin;
    private long numberOfBestSolutionsSwapHostOrigin;
    private long numberOfBestSolutionsGeneticOrigin;

    private BigDecimal accMakespanBeforeLSA, accMakespanAfterLSA;

    private BigDecimal actualMakespanBeforeLSA, actualMakespanAfterLSA;

    private long accLSAIterations, accLSAImprovements, accLSAInvocations;

    private long actualLSAIterations, actualLSAInvocations;

    private long accEvaluatedNeighbors;

    private double popAvgMakespan;

    public MemeticObserver(String strategyName, String operatorsName, long periodicTimeForMakespanEvolution){

        this.periodicTimeForMakespanEvolution = periodicTimeForMakespanEvolution;

        this.memeticStartingTime = -1;
        this.memeticExecutionTime = -1;

        this.strategyName = strategyName;
        this.operatorsName = operatorsName;

        //this.reachedMakespan = -1;

        this.memeticIterationNumber = 1;

        this.numberOfBestSolutionsGeneticOrigin = 0;
        this.numberOfBestSolutionsChangeHostOrigin = 0;
        this.numberOfBestSolutionsInsertionOrigin = 0;
        this.numberOfBestSolutionsSwapOrigin = 0;
        this.numberOfBestSolutionsSwapHostOrigin = 0;

        this.accMakespanBeforeLSA = BigDecimal.ZERO;
        this.accMakespanAfterLSA = BigDecimal.ZERO;

        this.accLSAImprovements = 0;
        this.accLSAIterations = 0;
        this.accLSAInvocations = 0;

        this.actualLSAIterations = 0;
        this.actualLSAInvocations = 0;

        this.accEvaluatedNeighbors = 0;

        this.actualMakespanBeforeLSA = BigDecimal.ZERO;
        this.actualMakespanAfterLSA = BigDecimal.ZERO;
    }

    @Override
    public void startRun(long startingTime) {
        this.memeticStartingTime = startingTime;

        this.memeticEvolutionMetrics = new MemeticEvolutionMetrics(this.periodicTimeForMakespanEvolution);
    }

    @Override
    public void endRun() {

        this.memeticExecutionTime = System.currentTimeMillis() - getStartingTime();

    }

    public MemeticEvolutionMetrics getMemeticEvolutionMetrics() {
        return this.memeticEvolutionMetrics;
    }

    @Override
    public void endStart() {

    }

    @Override
    public long getStartingTime() {
        return this.memeticStartingTime;
    }

    @Override
    public void endMemeticIteration() {
        this.memeticIterationNumber += 1;
    }

    @Override
    public void endLSAIteration() {

    }

    @Override
    public void setNumberOfGeneratedNeighbors(int numberOfGeneratedNeighbors) {

    }

    @Override
    public void setBetterNeighborsRatio(double betterNeighborsRatio) {

    }

    @Override
    public void setAllNeighborsImprovingRatio(double allNeighborsImprovingRatio) {

    }

    @Override
    public void setBetterNeighborsImprovingRatio(double betterNeighborsImprovingRatio) {

    }

    @Override
    public void setReachedMakespan(double reachedMakespan) {

    }

    @Override
    public void updateLSAEvolution(double actualMakespan, long actualIterationNumberOfNeighbors) {

        //TODO: revisar qué makespan poner aquí
        this.memeticEvolutionMetrics.update(
                this.memeticStartingTime,
                this.memeticIterationNumber,
                lastRecordedMakespan(),
                this.numberOfBestSolutionsGeneticOrigin,
                this.numberOfBestSolutionsChangeHostOrigin,
                this.numberOfBestSolutionsInsertionOrigin,
                this.numberOfBestSolutionsSwapOrigin,
                this.numberOfBestSolutionsSwapHostOrigin,
                this.accMakespanBeforeLSA,
                this.accMakespanAfterLSA,
                this.accLSAInvocations,
                this.accLSAIterations,
                this.accLSAImprovements,
                this.accEvaluatedNeighbors,
                this.popAvgMakespan
        );
    }

    public double lastRecordedMakespan(){

        int lastMakespanRecordedPos = this.memeticEvolutionMetrics.getActualMakespanEvolution().size()-1;
        double lastMakespanRecorded = Double.MAX_VALUE;

        if(lastMakespanRecordedPos != -1)
            lastMakespanRecorded = this.memeticEvolutionMetrics.getActualMakespanEvolution().get(lastMakespanRecordedPos);

        return lastMakespanRecorded;
    }

    @Override
    public void updateMemeticEvolution(double actualMakespan) {

        this.memeticEvolutionMetrics.update(this.memeticStartingTime, this.memeticIterationNumber, actualMakespan,
                this.numberOfBestSolutionsGeneticOrigin,
                this.numberOfBestSolutionsChangeHostOrigin,
                this.numberOfBestSolutionsInsertionOrigin,
                this.numberOfBestSolutionsSwapOrigin,
                this.numberOfBestSolutionsSwapHostOrigin,
                this.accMakespanBeforeLSA, this.accMakespanAfterLSA,
                this.accLSAInvocations, this.accLSAIterations, this.accLSAImprovements,
                this.accEvaluatedNeighbors,
                this.popAvgMakespan);
    }

    @Override
    public long getPeriodicTimeForMakespanEvolution() {
        return this.periodicTimeForMakespanEvolution;
    }

    @Override
    public String getStrategyName() {
        return this.strategyName;
    }

    @Override
    public String getOperatorsName() {
        return this.operatorsName;
    }

    public void updateBestSolutionsOrigins(SchedulePermutationSolution bestSolution){
        switch (bestSolution.getOrigin()){
            case CHANGE_HOST:
                this.numberOfBestSolutionsChangeHostOrigin += 1;
                break;
            case INSERTION:
                this.numberOfBestSolutionsInsertionOrigin += 1;
                break;
            case SWAP:
                this.numberOfBestSolutionsSwapOrigin += 1;
                break;
            case SWAP_HOST:
                this.numberOfBestSolutionsSwapHostOrigin += 1;
                break;
            case RANDOM:
            case CROSS_MUTATION:
                this.numberOfBestSolutionsGeneticOrigin += 1;
                break;
            default:
                throw new RuntimeException("No se pudo determinar el origen de la solución");
        }
    }

    public void updateAccMakespanBeforeAndAfterLSA(){
        this.accMakespanBeforeLSA = this.accMakespanBeforeLSA.add(
                this.actualMakespanBeforeLSA
        );

        this.accMakespanAfterLSA = this.accMakespanAfterLSA.add(
                this.actualMakespanAfterLSA
        );

        this.actualMakespanBeforeLSA = BigDecimal.ZERO;
        this.actualMakespanAfterLSA = BigDecimal.ZERO;
    }

    public void addActualMakespanAfterLSA(double makespanAfterLSA){
        this.actualMakespanAfterLSA = this.actualMakespanAfterLSA.add(BigDecimal.valueOf(makespanAfterLSA));
    }

    public void addActualMakespanBeforeLSA(double makespanBeforeLSA){
        this.actualMakespanBeforeLSA = this.actualMakespanBeforeLSA.add(BigDecimal.valueOf(makespanBeforeLSA));
    }

    @Override
    public void incrementLSAImprovement(){
        this.accLSAImprovements += 1;
    }

    @Override
    public void incrementLSAIteration(){
        this.actualLSAIterations += 1;
    }

    @Override
    public void incrementLSAInvocation(){
        this.actualLSAInvocations += 1;
    }

    @Override
    public void updateLSAInvocationsAndIterations(){
        this.accLSAInvocations += this.actualLSAInvocations;
        this.accLSAIterations += this.actualLSAIterations;

        this.actualLSAInvocations = 0;
        this.actualLSAIterations = 0;
    }

    @Override
    public void incrementEvaluatedNeighbor() {
        this.accEvaluatedNeighbors += 1;
    }

    public void addLSAImprovements(long numberOfImprovements){
        this.accLSAImprovements += numberOfImprovements;
    }

    public void addLSAInvocations(long numberOfIterations){
        this.accLSAIterations += numberOfIterations;
    }

    public void updatePopAvgMakespan(double newPopAvgMakespan){
        this.popAvgMakespan = newPopAvgMakespan;
    }

}
