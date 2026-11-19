package com.uniovi.sercheduler.localsearch.export;

public interface MetricsListener {

    void onMetrics(
            int memeticIterationNumber,
            long realInstant,
            long theoreticalInstant,

            double bestMakespan,
            double actualMakespan,

            long numberOfBestSolutionsRandomOrigin,

            long numberOfBestSolutionsGeneticOrigin,
            double ratioOfBestSolutionsGeneticOrigin,

            long numberOfBestSolutionsChangeHostOrigin,
            double ratioOfBestSolutionsChangeHostOrigin,

            long numberOfBestSolutionsInsertionOrigin,
            double ratioOfBestSolutionsInsertionOrigin,

            long numberOfBestSolutionsSwapOrigin,
            double ratioOfBestSolutionsSwapOrigin,

            long numberOfBestSolutionsSwapHostOrigin,
            double ratioOfBestSolutionsSwapHostOrigin,

            double accMakespanBeforeLSA,
            double accMakespanAfterLSA,

            double diffAccMakespanLSA,
            double percentageAccMakespanLSA,

            long accLSAInvocations,
            long accLSAIterations,
            double ratioIterationsInvocations,

            long accLSAImprovements,
            long accEvaluatedNeighbors,

            double popAvgMakespan
    );

}
