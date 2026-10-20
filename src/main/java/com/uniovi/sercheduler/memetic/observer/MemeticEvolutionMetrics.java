package com.uniovi.sercheduler.memetic.observer;

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
    private double bestMakespan;
    private final List<Double> actualMakespanEvolution;
    private final List<Double> bestMakespanEvolution;

    private final List<Long> numberOfBestSolutionsLSAOrigin;
    private final List<Long> numberOfBestSolutionsCrossMutationOrigin;

    private final List<BigDecimal> accMakespanBeforeLSAEvolution, accMakespanAfterLSAEvolution;
    private final List<BigDecimal> diffAccMakespanLSAEvolution, percentageAccMakespanLSAEvolution;

    private final List<Long> accLSAInvocationsEvolution, accLSAIterationsEvolution, accLSAImprovementsEvolution;


    public MemeticEvolutionMetrics(long periodicTimeForMakespanEvolution) {

        this.periodicTimeForMakespanEvolution = periodicTimeForMakespanEvolution;

        this.memeticIterationNumberList = new ArrayList<>();
        this.realInstants = new ArrayList<>();
        this.theoreticalInstants = new ArrayList<>();
        this.lastRecordedTime = -1;
        this.bestMakespan = Double.MAX_VALUE;
        this.bestMakespanEvolution = new ArrayList<>();
        this.actualMakespanEvolution = new ArrayList<>();

        this.numberOfBestSolutionsCrossMutationOrigin = new ArrayList<>();
        this.numberOfBestSolutionsLSAOrigin = new ArrayList<>();

        this.accMakespanBeforeLSAEvolution = new ArrayList<>();
        this.accMakespanAfterLSAEvolution = new ArrayList<>();
        this.diffAccMakespanLSAEvolution = new ArrayList<>();
        this.percentageAccMakespanLSAEvolution = new ArrayList<>();

        this.accLSAImprovementsEvolution = new ArrayList<>();
        this.accLSAIterationsEvolution = new ArrayList<>();
        this.accLSAInvocationsEvolution = new ArrayList<>();

    }

    public void update(long memeticStartingTime, int memeticIterationNumber, double actualMakespan,
                       long numberOfBestSolutionsCrossMutationOrigin, long numberOfBestSolutionsLSAOrigin,
                       BigDecimal accMakespanBeforeLSA, BigDecimal accMakespanAfterLSA,
                       long accLSAInvocations, long accLSAIterations, long accLSAImprovements){

        updateBestMakespan(actualMakespan);

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

        if (elapsedFromStart >= nextTheoreticalInstant)
            saveMetrics(
                    memeticIterationNumber,
                    elapsedFromStart,
                    nextTheoreticalInstant,
                    actualMakespan,
                    numberOfBestSolutionsCrossMutationOrigin,
                    numberOfBestSolutionsLSAOrigin,
                    accMakespanBeforeLSA,
                    accMakespanAfterLSA,
                    calculateDiffAccMakespanLSA(accMakespanBeforeLSA, accMakespanAfterLSA),
                    calculatePercentageAccMakespanLSA(accMakespanBeforeLSA, accMakespanAfterLSA),
                    accLSAInvocations,
                    accLSAIterations,
                    accLSAImprovements
            );
    }

    private BigDecimal calculateDiffAccMakespanLSA(BigDecimal accMakespanBeforeLSA, BigDecimal accMakespanAfterLSA){
        return accMakespanAfterLSA
                .subtract(accMakespanBeforeLSA)
                .abs();
    }

    private BigDecimal calculatePercentageAccMakespanLSA(BigDecimal accMakespanBeforeLSA, BigDecimal accMakespanAfterLSA){
        return accMakespanAfterLSA
                .divide(accMakespanBeforeLSA,
                        2, RoundingMode.HALF_UP);
    }

    private void updateBestMakespan(double actualMakespan){
        this.bestMakespan = Math.min(actualMakespan, this.bestMakespan);
    }

    private void saveMetrics(int memeticIterationNumber, long realInstant, long theoreticalInstant, double actualMakespan,
                             long numberOfBestSolutionsCrossMutationOrigin, long numberOfBestSolutionsLSAOrigin,
                             BigDecimal accMakespanBeforeLSA, BigDecimal accMakespanAfterLSA,
                             BigDecimal diffAccMakespanLSA, BigDecimal percentageAccMakespanLSA,
                             long accLSAInvocations, long accLSAIterations, long accLSAImprovements){

        this.memeticIterationNumberList.add(memeticIterationNumber);
        this.realInstants.add(realInstant);
        this.theoreticalInstants.add(theoreticalInstant);

        this.bestMakespanEvolution.add(this.bestMakespan);
        this.actualMakespanEvolution.add(actualMakespan);

        this.numberOfBestSolutionsLSAOrigin.add(numberOfBestSolutionsLSAOrigin);
        this.numberOfBestSolutionsCrossMutationOrigin.add(numberOfBestSolutionsCrossMutationOrigin);

        this.accMakespanBeforeLSAEvolution.add(accMakespanBeforeLSA);
        this.accMakespanAfterLSAEvolution.add(accMakespanAfterLSA);
        this.diffAccMakespanLSAEvolution.add(diffAccMakespanLSA);
        this.percentageAccMakespanLSAEvolution.add(percentageAccMakespanLSA);

        this.accLSAInvocationsEvolution.add(accLSAInvocations);
        this.accLSAIterationsEvolution.add(accLSAIterations);
        this.accLSAImprovementsEvolution.add(accLSAImprovements);
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

    public List<Long> getNumberOfBestSolutionsLSAOrigin() {
        return numberOfBestSolutionsLSAOrigin;
    }

    public List<Long> getNumberOfBestSolutionsCrossMutationOrigin() {
        return numberOfBestSolutionsCrossMutationOrigin;
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
}
