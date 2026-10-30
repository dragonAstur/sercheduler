package com.uniovi.sercheduler.memetic.observer;

import com.uniovi.sercheduler.jmetal.problem.SchedulePermutationSolution;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.math.RoundingMode;

public class MemeticEvolutionMetrics {

    private final List<Integer> memeticIterationNumberList;
    private final long periodicTimeForMakespanEvolution;
    private final List<Long> realInstants;
    private final List<Long> theoreticalInstants;
    private long lastRecordedTime;
    private final List<Double> actualMakespanEvolution;
    private final List<Double> bestMakespanEvolution;

    private final List<Double> ratioOfBestSolutionsGeneticOriginEvolution;
    private final List<Double> ratioOfBestSolutionsChangeHostOriginEvolution;
    private final List<Double> ratioOfBestSolutionsInsertionOriginEvolution;
    private final List<Double> ratioOfBestSolutionsSwapOriginEvolution;
    private final List<Double> ratioOfBestSolutionsSwapHostOriginEvolution;

    private final List<Long> numberOfBestSolutionsGeneticOriginEvolution;
    private final List<Long> numberOfBestSolutionsChangeHostOriginEvolution;
    private final List<Long> numberOfBestSolutionsInsertionOriginEvolution;
    private final List<Long> numberOfBestSolutionsSwapOriginEvolution;
    private final List<Long> numberOfBestSolutionsSwapHostOriginEvolution;

    private final List<BigDecimal> accMakespanBeforeLSAEvolution, accMakespanAfterLSAEvolution;
    private final List<BigDecimal> diffAccMakespanLSAEvolution, percentageAccMakespanLSAEvolution;

    private final List<Long> accLSAInvocationsEvolution, accLSAIterationsEvolution, accLSAImprovementsEvolution;

    private final List<Double> ratioIterationsInvocationsEvolution;

    private final List<Long> accEvaluatedNeighborsEvolution;

    private final List<Double> popAvgMakespanEvolution;


    public MemeticEvolutionMetrics(long periodicTimeForMakespanEvolution) {

        this.periodicTimeForMakespanEvolution = periodicTimeForMakespanEvolution;

        this.memeticIterationNumberList = new ArrayList<>();
        this.realInstants = new ArrayList<>();
        this.theoreticalInstants = new ArrayList<>();
        this.lastRecordedTime = -1;
        this.bestMakespanEvolution = new ArrayList<>();
        this.actualMakespanEvolution = new ArrayList<>();

        this.ratioOfBestSolutionsGeneticOriginEvolution = new ArrayList<>();
        this.ratioOfBestSolutionsChangeHostOriginEvolution = new ArrayList<>();
        this.ratioOfBestSolutionsInsertionOriginEvolution = new ArrayList<>();
        this.ratioOfBestSolutionsSwapOriginEvolution = new ArrayList<>();
        this.ratioOfBestSolutionsSwapHostOriginEvolution = new ArrayList<>();

        this.numberOfBestSolutionsGeneticOriginEvolution = new ArrayList<>();
        this.numberOfBestSolutionsChangeHostOriginEvolution = new ArrayList<>();
        this.numberOfBestSolutionsInsertionOriginEvolution = new ArrayList<>();
        this.numberOfBestSolutionsSwapOriginEvolution = new ArrayList<>();
        this.numberOfBestSolutionsSwapHostOriginEvolution = new ArrayList<>();

        this.accMakespanBeforeLSAEvolution = new ArrayList<>();
        this.accMakespanAfterLSAEvolution = new ArrayList<>();
        this.diffAccMakespanLSAEvolution = new ArrayList<>();
        this.percentageAccMakespanLSAEvolution = new ArrayList<>();

        this.accLSAImprovementsEvolution = new ArrayList<>();
        this.accLSAIterationsEvolution = new ArrayList<>();
        this.accLSAInvocationsEvolution = new ArrayList<>();

        this.accEvaluatedNeighborsEvolution = new ArrayList<>();

        this.ratioIterationsInvocationsEvolution = new ArrayList<>();

        this.popAvgMakespanEvolution = new ArrayList<>();
    }

    public void update(long memeticStartingTime, int memeticIterationNumber, double bestMakespan, double actualMakespan,
                       long numberOfBestSolutionsGeneticOrigin,
                       long numberOfBestSolutionsChangeHostOrigin, long numberOfBestSolutionsInsertionOrigin,
                       long numberOfBestSolutionsSwapOrigin, long numberOfBestSolutionsSwapHostOrigin,
                       BigDecimal accMakespanBeforeLSA, BigDecimal accMakespanAfterLSA,
                       long accLSAInvocations, long accLSAIterations, long accLSAImprovements,
                       long accEvaluatedNeighbors,
                       double popAvgMakespan){

//        if(this.periodicTimeForMakespanEvolution > 0L) {
//
//            long actualTime = System.currentTimeMillis();
//
//            this.lastRecordedTime = this.lastRecordedTime <= 0 ? memeticStartingTime : this.lastRecordedTime;
//
//            long elapsedTime = actualTime - this.lastRecordedTime;
//
//            if (elapsedTime >= this.periodicTimeForMakespanEvolution) {
//
//                saveMetrics(
//                        memeticIterationNumber,
//                        actualTime - memeticStartingTime,
//                        actualMakespan
//                );
//
//                this.lastRecordedTime = actualTime;
//            }
//
//        }

        if(periodicTimeForMakespanEvolution <= 0L)
            return;

        long actualTime = System.currentTimeMillis();

        long elapsedFromStart = actualTime - memeticStartingTime;

        long lastTheoreticalInstant =
                theoreticalInstants.isEmpty()
                        ? 0
                        : theoreticalInstants.get(theoreticalInstants.size() - 1);

        long nextTheoreticalInstant = lastTheoreticalInstant + periodicTimeForMakespanEvolution;

        if (elapsedFromStart >= nextTheoreticalInstant) {

            BigDecimal diffAccMakespanLSA = calculateDiffAccMakespanLSA(accMakespanBeforeLSA, accMakespanAfterLSA);

            saveMetrics(
                    memeticIterationNumber,
                    elapsedFromStart,
                    nextTheoreticalInstant,
                    bestMakespan,
                    actualMakespan,
                    numberOfBestSolutionsGeneticOrigin,
                    calculateRatioOriginIteration(numberOfBestSolutionsGeneticOrigin, accLSAIterations),
                    numberOfBestSolutionsChangeHostOrigin,
                    calculateRatioOriginIteration(numberOfBestSolutionsChangeHostOrigin, accLSAIterations),
                    numberOfBestSolutionsInsertionOrigin,
                    calculateRatioOriginIteration(numberOfBestSolutionsInsertionOrigin, accLSAIterations),
                    numberOfBestSolutionsSwapOrigin,
                    calculateRatioOriginIteration(numberOfBestSolutionsSwapOrigin, accLSAIterations),
                    numberOfBestSolutionsSwapHostOrigin,
                    calculateRatioOriginIteration(numberOfBestSolutionsSwapHostOrigin, accLSAIterations),
                    accMakespanBeforeLSA,
                    accMakespanAfterLSA,
                    diffAccMakespanLSA,
                    calculatePercentageAccMakespanLSA(accMakespanBeforeLSA, diffAccMakespanLSA),
                    accLSAInvocations,
                    accLSAIterations,
                    calculateRatioIterationsInvocations(accLSAIterations, accLSAInvocations),
                    accLSAImprovements,
                    accEvaluatedNeighbors,
                    popAvgMakespan
            );
        }
    }

    private double calculateRatioOriginIteration(long numberOfBestSolutionsOrigin, long accLSAIterations){
        if(accLSAIterations == 0)
            return 0.0;

        return (double) (numberOfBestSolutionsOrigin / accLSAIterations);
    }

    private double calculateRatioIterationsInvocations(long accLSAIterations, long accLSAInvocations){

        if(accLSAInvocations == 0)
            return 0.0;

        return (double) ( (accLSAIterations - accLSAInvocations) / accLSAInvocations);
    }

    private BigDecimal calculateDiffAccMakespanLSA(BigDecimal accMakespanBeforeLSA, BigDecimal accMakespanAfterLSA){
        return accMakespanAfterLSA
                .subtract(accMakespanBeforeLSA)
                .abs();
    }


    private BigDecimal calculatePercentageAccMakespanLSA(BigDecimal accMakespanBeforeLSA, BigDecimal diffAccMakespanLSA){

        if(accMakespanBeforeLSA.compareTo(BigDecimal.ZERO) <= 0)
            return BigDecimal.ZERO;

        return diffAccMakespanLSA
                .divide(accMakespanBeforeLSA,
                        6, RoundingMode.HALF_UP);
    }



    private void saveMetrics(int memeticIterationNumber, long realInstant, long theoreticalInstant,
                             double bestMakespan, double actualMakespan,
                             long numberOfBestSolutionsGeneticOrigin, double ratioOfBestSolutionsGeneticOrigin,
                             long numberOfBestSolutionsChangeHostOrigin, double ratioOfBestSolutionsChangeHostOrigin,
                             long numberOfBestSolutionsInsertionOrigin,  double ratioOfBestSolutionsInsertionOrigin,
                             long numberOfBestSolutionsSwapOrigin, double ratioOfBestSolutionsSwapOrigin,
                             long numberOfBestSolutionsSwapHostOrigin, double ratioOfBestSolutionsSwapHostOrigin,
                             BigDecimal accMakespanBeforeLSA, BigDecimal accMakespanAfterLSA,
                             BigDecimal diffAccMakespanLSA, BigDecimal percentageAccMakespanLSA,
                             long accLSAInvocations, long accLSAIterations, double ratioIterationsInvocations,
                             long accLSAImprovements,
                             long accEvaluatedNeighbors,
                             double popAvgMakespan){

        this.memeticIterationNumberList.add(memeticIterationNumber);
        this.realInstants.add(realInstant);
        this.theoreticalInstants.add(theoreticalInstant);

        this.bestMakespanEvolution.add(bestMakespan);
        this.actualMakespanEvolution.add(actualMakespan);

        this.numberOfBestSolutionsGeneticOriginEvolution.add(numberOfBestSolutionsGeneticOrigin);
        this.ratioOfBestSolutionsGeneticOriginEvolution.add(ratioOfBestSolutionsGeneticOrigin);

        this.numberOfBestSolutionsChangeHostOriginEvolution.add(numberOfBestSolutionsChangeHostOrigin);
        this.ratioOfBestSolutionsChangeHostOriginEvolution.add(ratioOfBestSolutionsChangeHostOrigin);

        this.numberOfBestSolutionsInsertionOriginEvolution.add(numberOfBestSolutionsInsertionOrigin);
        this.ratioOfBestSolutionsInsertionOriginEvolution.add(ratioOfBestSolutionsInsertionOrigin);

        this.numberOfBestSolutionsSwapOriginEvolution.add(numberOfBestSolutionsSwapOrigin);
        this.ratioOfBestSolutionsSwapOriginEvolution.add(ratioOfBestSolutionsSwapOrigin);

        this.numberOfBestSolutionsSwapHostOriginEvolution.add(numberOfBestSolutionsSwapHostOrigin);
        this.ratioOfBestSolutionsSwapHostOriginEvolution.add(ratioOfBestSolutionsSwapHostOrigin);

        this.accMakespanBeforeLSAEvolution.add(accMakespanBeforeLSA);
        this.accMakespanAfterLSAEvolution.add(accMakespanAfterLSA);
        this.diffAccMakespanLSAEvolution.add(diffAccMakespanLSA);
        this.percentageAccMakespanLSAEvolution.add(percentageAccMakespanLSA);

        this.accLSAInvocationsEvolution.add(accLSAInvocations);
        this.accLSAIterationsEvolution.add(accLSAIterations);

        this.ratioIterationsInvocationsEvolution.add(ratioIterationsInvocations);

        this.accLSAImprovementsEvolution.add(accLSAImprovements);

        this.accEvaluatedNeighborsEvolution.add(accEvaluatedNeighbors);

        this.popAvgMakespanEvolution.add(popAvgMakespan);
    }

    public List<Integer> getMemeticIterationNumberList() {
        return memeticIterationNumberList;
    }

    public List<Long> getRealInstants() {
        return realInstants;
    }
    public List<Long> getTheoreticalInstants() {
        return theoreticalInstants;
    }

    public List<Double> getActualMakespanEvolution() {
        return actualMakespanEvolution;
    }

    public List<Double> getBestMakespanEvolution() {
        return bestMakespanEvolution;
    }


    public List<BigDecimal> getAccMakespanBeforeLSAEvolution() {
        return accMakespanBeforeLSAEvolution;
    }

    public List<BigDecimal> getAccMakespanAfterLSAEvolution() {
        return accMakespanAfterLSAEvolution;
    }

    public List<BigDecimal> getDiffAccMakespanLSAEvolution() {
        return diffAccMakespanLSAEvolution;
    }

    public List<BigDecimal> getPercentageAccMakespanLSAEvolution() {
        return percentageAccMakespanLSAEvolution;
    }

    public List<Long> getAccLSAIterationsEvolution() {
        return accLSAIterationsEvolution;
    }

    public List<Long> getAccLSAImprovementsEvolution() {
        return accLSAImprovementsEvolution;
    }

    public List<Long> getAccLSAInvocationsEvolution(){
        return accLSAInvocationsEvolution;
    }

    public List<Long> getAccEvaluatedNeighborsEvolution() {
        return accEvaluatedNeighborsEvolution;
    }

    public List<Double> getRatioIterationsInvocationsEvolution() {
        return ratioIterationsInvocationsEvolution;
    }

    public List<Double> getPopAvgMakespanEvolution() {
        return popAvgMakespanEvolution;
    }

    public List<Double> getRatioOfBestSolutionsGeneticOriginEvolution() {
        return ratioOfBestSolutionsGeneticOriginEvolution;
    }

    public List<Double> getRatioOfBestSolutionsChangeHostOriginEvolution() {
        return ratioOfBestSolutionsChangeHostOriginEvolution;
    }

    public List<Double> getRatioOfBestSolutionsInsertionOriginEvolution() {
        return ratioOfBestSolutionsInsertionOriginEvolution;
    }

    public List<Double> getRatioOfBestSolutionsSwapOriginEvolution() {
        return ratioOfBestSolutionsSwapOriginEvolution;
    }

    public List<Double> getRatioOfBestSolutionsSwapHostOriginEvolution() {
        return ratioOfBestSolutionsSwapHostOriginEvolution;
    }

    public List<Long> getNumberOfBestSolutionsGeneticOriginEvolution() {
        return numberOfBestSolutionsGeneticOriginEvolution;
    }

    public List<Long> getNumberOfBestSolutionsChangeHostOriginEvolution() {
        return numberOfBestSolutionsChangeHostOriginEvolution;
    }

    public List<Long> getNumberOfBestSolutionsInsertionOriginEvolution() {
        return numberOfBestSolutionsInsertionOriginEvolution;
    }

    public List<Long> getNumberOfBestSolutionsSwapOriginEvolution() {
        return numberOfBestSolutionsSwapOriginEvolution;
    }

    public List<Long> getNumberOfBestSolutionsSwapHostOriginEvolution() {
        return numberOfBestSolutionsSwapHostOriginEvolution;
    }
}
