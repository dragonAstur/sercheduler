package com.uniovi.sercheduler.localsearch.export;

import com.uniovi.sercheduler.memetic.observer.MemeticObserver;

import java.io.BufferedWriter;
import java.io.Closeable;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Locale;

public class CSVExporter implements MetricsListener, Closeable {

    private final BufferedWriter writer;

    private final String strategyName;
    private final String operatorsName;
    private final long periodicTimeForMakespanEvolution;

    public CSVExporter(
            String fileName,
            String strategyName,
            String operatorsName,
            long periodicTimeForMakespanEvolution) {

        this.strategyName = strategyName;
        this.operatorsName = operatorsName;
        this.periodicTimeForMakespanEvolution = periodicTimeForMakespanEvolution;

        try {

            this.writer = Files.newBufferedWriter(
                    Path.of(fileName + ".csv"),
                    StandardOpenOption.CREATE,
                    StandardOpenOption.APPEND
            );

            writeHeader();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void writeHeader() throws IOException {

        writer.write(
                "Strategy;" +
                        "Operator config;" +
                        "Algorithm iteration (generations);" +
                        "Periodic time;" +
                        "Periodic time (hours);" +
                        "Time instant;" +
                        "Actual makespan;" +
                        "Best makespan in this run;" +
                        "Pop. average makespan;" +
                        "Total makespan before LSA;" +
                        "Total makespan after LSA;" +
                        "Diff before-after LSA;" +
                        "Diff/before LSA;" +
                        "LSA invocations;" +
                        "LSA iterations;" +
                        "Iterations / Invocations;" +
                        "LSA improvements;" +
                        "Evaluated neighbors;" +
                        "Genetic origin;" +
                        "Genetic origin per iteration;" +
                        "N1 origin (Change Host);" +
                        "N1 origin per iteration;" +
                        "N2 origin (Insertion);" +
                        "N2 origin per iteration;" +
                        "N3 origin (Swap);" +
                        "N3 origin per iteration;" +
                        "N4 origin (Swap Host);" +
                        "N4 origin per iteration;" +
                        "Random origin\n"
        );

        writer.flush();
    }

    @Override
    public void onMetrics(
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

            double popAvgMakespan) {

        try {

            StringBuilder line = new StringBuilder();

            line.append(strategyName).append(";")
                    .append(operatorsName).append(";")
                    .append(memeticIterationNumber).append(";")
                    .append(theoreticalInstant).append(";")
                    .append(Formatter.millisecondsToHourFormat(theoreticalInstant)).append(";")
                    .append(realInstant).append(";")
                    .append(actualMakespan).append(";")
                    .append(bestMakespan).append(";")
                    .append(popAvgMakespan).append(";")
                    .append(accMakespanBeforeLSA).append(";")
                    .append(accMakespanAfterLSA).append(";")
                    .append(diffAccMakespanLSA).append(";")
                    .append(String.format(Locale.US, "%.6f", percentageAccMakespanLSA)).append(";")
                    .append(accLSAInvocations).append(";")
                    .append(accLSAIterations).append(";")
                    .append(ratioIterationsInvocations).append(";")
                    .append(accLSAImprovements).append(";")
                    .append(accEvaluatedNeighbors).append(";")
                    .append(numberOfBestSolutionsGeneticOrigin).append(";")
                    .append(ratioOfBestSolutionsGeneticOrigin).append(";")
                    .append(numberOfBestSolutionsChangeHostOrigin).append(";")
                    .append(ratioOfBestSolutionsChangeHostOrigin).append(";")
                    .append(numberOfBestSolutionsInsertionOrigin).append(";")
                    .append(ratioOfBestSolutionsInsertionOrigin).append(";")
                    .append(numberOfBestSolutionsSwapOrigin).append(";")
                    .append(ratioOfBestSolutionsSwapOrigin).append(";")
                    .append(numberOfBestSolutionsSwapHostOrigin).append(";")
                    .append(ratioOfBestSolutionsSwapHostOrigin).append(";")
                    .append(numberOfBestSolutionsRandomOrigin)
                    .append('\n');

            writer.write(line.toString());

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void close() throws IOException {
        writer.flush();
        writer.close();
    }
}

/*public class CSVExporter {


    public static void createMemeticCSV(String fileName){

        try (FileWriter writer = new FileWriter(fileName + ".csv", false)) {

            String newLine = "Strategy" +
                    ";" +
                    "Operator config" +
                    ";" +
                    "Algorithm iteration (generations)" +
                    ";" +
                    "Periodic time" +
                    ";" +
                    "Periodic time (hours)" +
                    ";" +
                    "Time instant" +
                    ";" +
                    "Actual makespan" +
                    ";" +
                    "Best makespan in this run" +
                    ";" +
                    "Pop. average makespan" +
                    ";" +
                    "Total makespan before LSA" +
                    ";" +
                    "Total makespan after LSA" +
                    ";" +
                    "Diff before-after LSA" +
                    ";" +
                    "Diff/before LSA" +
                    ";" +
                    "LSA invocations" +
                    ";" +
                    "LSA iterations" +
                    ";" +
                    "Iterations / Invocations" +
                    ";" +
                    "LSA improvements" +
                    ";" +
                    "Evaluated neighbors" +
                    ";" +
                    "Genetic origin" +
                    ";" +
                    "Genetic origin per iteration" +
                    ";" +
                    "N1 origin (Change Host)" +
                    ";" +
                    "N1 origin per iteration" +
                    ";" +
                    "N2 origin (Insertion)" +
                    ";" +
                    "N2 origin per iteration" +
                    ";" +
                    "N3 origin (Swap)" +
                    ";" +
                    "N3 origin per iteration" +
                    ";" +
                    "N4 origin (Swap Host)" +
                    ";" +
                    "N4 origin per iteration\n";

            writer.write(newLine);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    public static void appendMemeticCSV(String fileName, MemeticObserver observer){

        try (FileWriter writer = new FileWriter(fileName + ".csv", true)) {

            StringBuilder newLine = new StringBuilder();

            for(int i = 0; i < observer.getMemeticEvolutionMetrics().getRealInstants().size(); i++){

                newLine.append(observer.getStrategyName())
                        .append(";")
                        .append(observer.getOperatorsName())
                        .append(";")
                        .append(observer.getMemeticEvolutionMetrics().getMemeticIterationNumberList().get(i))
                        .append(";")
                        .append(observer.getPeriodicTimeForMakespanEvolution() * (i+1))
                        .append(";")
                        .append(
                                Formatter.millisecondsToHourFormat(
                                        observer.getPeriodicTimeForMakespanEvolution() * (i+1)
                                )
                        )
                        .append(";")
                        .append(observer.getMemeticEvolutionMetrics().getRealInstants().get(i))
                        .append(";")
                        .append(observer.getMemeticEvolutionMetrics().getActualMakespanEvolution().get(i))
                        .append(";")
                        .append(observer.getMemeticEvolutionMetrics().getBestMakespanEvolution().get(i))
                        .append(";")
                        .append(observer.getMemeticEvolutionMetrics().getPopAvgMakespanEvolution().get(i))
                        .append(";")
                        .append(observer.getMemeticEvolutionMetrics().getAccMakespanBeforeLSAEvolution().get(i))
                        .append(";")
                        .append(observer.getMemeticEvolutionMetrics().getAccMakespanAfterLSAEvolution().get(i))
                        .append(";")
                        .append(observer.getMemeticEvolutionMetrics().getDiffAccMakespanLSAEvolution().get(i))
                        .append(";")
                        .append(String.format(Locale.US, "%.6f",
                                        observer.getMemeticEvolutionMetrics().getPercentageAccMakespanLSAEvolution().get(i)
                                )
                        )
                        .append(";")
                        .append(observer.getMemeticEvolutionMetrics().getAccLSAInvocationsEvolution().get(i))
                        .append(";")
                        .append(observer.getMemeticEvolutionMetrics().getAccLSAIterationsEvolution().get(i))
                        .append(";")
                        .append(observer.getMemeticEvolutionMetrics().getRatioIterationsInvocationsEvolution().get(i))
                        .append(";")
                        .append(observer.getMemeticEvolutionMetrics().getAccLSAImprovementsEvolution().get(i))
                        .append(";")
                        .append(observer.getMemeticEvolutionMetrics().getAccEvaluatedNeighborsEvolution().get(i))
                        .append(";")
                        .append(observer.getMemeticEvolutionMetrics().getNumberOfBestSolutionsGeneticOriginEvolution().get(i))
                        .append(";")
                        .append(observer.getMemeticEvolutionMetrics().getRatioOfBestSolutionsGeneticOriginEvolution().get(i))
                        .append(";")
                        .append(observer.getMemeticEvolutionMetrics().getNumberOfBestSolutionsChangeHostOriginEvolution().get(i))
                        .append(";")
                        .append(observer.getMemeticEvolutionMetrics().getRatioOfBestSolutionsChangeHostOriginEvolution().get(i))
                        .append(";")
                        .append(observer.getMemeticEvolutionMetrics().getNumberOfBestSolutionsInsertionOriginEvolution().get(i))
                        .append(";")
                        .append(observer.getMemeticEvolutionMetrics().getRatioOfBestSolutionsInsertionOriginEvolution().get(i))
                        .append(";")
                        .append(observer.getMemeticEvolutionMetrics().getNumberOfBestSolutionsSwapOriginEvolution().get(i))
                        .append(";")
                        .append(observer.getMemeticEvolutionMetrics().getRatioOfBestSolutionsSwapOriginEvolution().get(i))
                        .append(";")
                        .append(observer.getMemeticEvolutionMetrics().getNumberOfBestSolutionsSwapHostOriginEvolution().get(i))
                        .append(";")
                        .append(observer.getMemeticEvolutionMetrics().getRatioOfBestSolutionsSwapHostOriginEvolution().get(i))
                        .append("\n");
            }

            writer.write(newLine.toString());

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}*/
