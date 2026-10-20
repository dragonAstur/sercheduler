package com.uniovi.sercheduler.localsearch.export;

import com.uniovi.sercheduler.memetic.observer.MemeticObserver;

import java.io.FileWriter;
import java.io.IOException;

public class CSVExporter {


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
                    "Time instant" +
                    ";" +
                    "Actual makespan" +
                    ";" +
                    "Best makespan in this run" +
                    ";" +
                    "Pop. average makespan" +
                    ";" +
                    "Number of generations" +
                    ";" +
                    "Total makespan before LSA" +
                    ";" +
                    "Total makespan after LSA" +
                    ";" +
                    "Diff before-after LSA" +
                    ";" +
                    "LSA invocations" +
                    ";" +
                    "LSA iterations" +
                    ";" +
                    "Evaluated neighbors" +
                    ";" +
                    "Best solution from LSA" +
                    ";" +
                    "Best solution from cross-mutation\n";

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
                        .append(observer.getMemeticEvolutionMetrics().getRealInstants().get(i))
                        .append(";")
                        .append(observer.getMemeticEvolutionMetrics().getActualMakespanEvolution().get(i))
                        .append(";")
                        .append(observer.getMemeticEvolutionMetrics().getBestMakespanEvolution().get(i))
                        .append(";")
                        //.append()
                        .append(";")
                        //.append()
                        .append(";")
                        .append(observer.getMemeticEvolutionMetrics().getAccMakespanBeforeLSAEvolution().get(i))
                        .append(";")
                        .append(observer.getMemeticEvolutionMetrics().getAccMakespanAfterLSAEvolution().get(i))
                        .append(";")
                        .append(observer.getMemeticEvolutionMetrics().getDiffAccMakespanLSAEvolution())
                        .append(";")
                        //.append()
                        .append(";")
                        //.append()
                        .append(";")
                        //.append()
                        .append(";")
                        .append(observer.getMemeticEvolutionMetrics().getNumberOfBestSolutionsLSAOrigin().get(i))
                        .append(";")
                        .append(observer.getMemeticEvolutionMetrics().getNumberOfBestSolutionsCrossMutationOrigin().get(i))
                        .append("\n");
            }

            writer.write(newLine.toString());

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}
