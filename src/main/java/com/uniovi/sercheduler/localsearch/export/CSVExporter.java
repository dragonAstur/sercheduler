package com.uniovi.sercheduler.localsearch.export;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import java.io.FileWriter;
import java.io.IOException;

public class CSVExporter {

    public static void createCSV(String fileName){

            createDataCSV(fileName);
            createResumeCSV(fileName);
            createDetailCSV(fileName);

    }

    private static void createDetailCSV(String fileName) {

        try (FileWriter writer = new FileWriter(fileName + "_detail.csv", true)) {

            StringBuilder newLine = new StringBuilder();

            newLine.append("Experiment")
                    .append(";")
                    .append("Run number")
                    .append(";")
                    .append("Iteration number")
                    .append(";")
                    .append("Best makespan reached")
                    .append(";")
                    .append("Improvement ratio with respect to last iteration")
                    .append(";")
                    .append("Generated neighbors")
                    .append(";")
                    .append("Average percentage of neighbors that outperform their source solution")
                    .append(";")
                    .append("Average improvement ratio from all neighbors")
                    .append(";")
                    .append("Average improvement ratio from neighbors that outperform their source solution")
                    .append("\n");

            writer.write(newLine.toString());

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    private static void createResumeCSV(String fileName) {

        try (FileWriter writer = new FileWriter(fileName + "_resume.csv", true)) {

            StringBuilder newLine = new StringBuilder();

            newLine.append("Method")
                    .append(";")
                    .append("Avg(Best_Mkp)")
                    .append(";")
                    .append("Avg(Exec_Time)")
                    .append(";")
                    .append("Avg(LS_Iters)")
                    .append(";")
                    .append("Avg(Neighb)")
                    .append("\n");

            writer.write(newLine.toString());

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    private static void createDataCSV(String fileName){

        try (FileWriter writer = new FileWriter(fileName + "_data.csv", true)) {

            StringBuilder newLine = new StringBuilder();

            newLine.append("Method")
                    .append(";")
                    .append("Run number")
                    .append(";")
                    .append("Best makespan reached")
                    .append(";")
                    .append("Executing time")
                    .append(";")
                    .append("Number of local search iterations")
                    .append(";")
                    .append("Average generated neighbors")
                    .append(";")
                    .append("Average percentage of neighbors that outperform their source solution")
                    .append(";")
                    .append("Average improvement ratio from all neighbors")
                    .append(";")
                    .append("Average improvement ratio from neighbors that outperform their source solution")
                    .append("\n");

            writer.write(newLine.toString());

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

}
