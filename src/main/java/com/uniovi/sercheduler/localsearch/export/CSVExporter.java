package com.uniovi.sercheduler.localsearch.export;

import com.uniovi.sercheduler.memetic.observer.MemeticObserver;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Workbook;

import java.io.FileWriter;
import java.io.IOException;

public class CSVExporter {


    public static void createMemeticCSV(String fileName){

        try (FileWriter writer = new FileWriter(fileName + ".csv", false)) {

            String newLine = "Strategy" +
                    ";" +
                    "Operator config" +
                    ";" +
                    "Algorithm iteration" +
                    ";" +
                    "Periodic time" +
                    ";" +
                    "Time instant" +
                    ";" +
                    "Actual makespan" +
                    ";" +
                    "Best makespan in this run\n";

            writer.write(newLine);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    public static void appendMemeticCSV(String fileName, MemeticObserver observer){

        try (FileWriter writer = new FileWriter(fileName + ".csv", true)) {

            StringBuilder newLine = new StringBuilder();

            for(int i = 0; i < observer.getMemeticEvolutionMetrics().getInstants().size(); i++){

                newLine.append(observer.getStrategyName())
                        .append(";")
                        .append(observer.getOperatorsName())
                        .append(";")
                        .append(observer.getMemeticEvolutionMetrics().getMemeticIterationNumberList().get(i))
                        .append(";")
                        .append(observer.getPeriodicTimeForMakespanEvolution() * (i+1))
                        .append(";")
                        .append(observer.getMemeticEvolutionMetrics().getInstants().get(i))
                        .append(";")
                        .append(observer.getMemeticEvolutionMetrics().getActualMakespanEvolution().get(i))
                        .append(";")
                        .append(observer.getMemeticEvolutionMetrics().getBestMakespanEvolution().get(i))
                        .append("\n");
            }

            writer.write(newLine.toString());

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}
