package com.uniovi.sercheduler.localsearch.export;

import com.uniovi.sercheduler.localsearch.observer.NeighborhoodObserver;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class XLSXExporter {

    public static void createWorkbook(String fileName){

        try (Workbook workbook = new XSSFWorkbook()) {

            createDataSheet(workbook);

            createResumeSheet(workbook);

            createDetailSheet(workbook);

            // Write the workbook to a file
            try (FileOutputStream outputStream = new FileOutputStream(fileName + ".xlsx")) {
                workbook.write(outputStream);
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    public static void appendWorkbook(NeighborhoodObserver observer, String fileName){

        try (Workbook workbook = new XSSFWorkbook(new FileInputStream(fileName + ".xlsx"))) {

            appendDataSheet(workbook, observer);

            appendResumeSheet(workbook, observer);

            appendDetailSheet(workbook, observer);

            try (FileOutputStream outputStream = new FileOutputStream(fileName + ".xlsx")) {
                workbook.write(outputStream);
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }


    }

    private static void appendDetailSheet(Workbook workbook, NeighborhoodObserver observer) {
        Sheet sheet = workbook.getSheet("Detail");

        int rowCounter = sheet.getLastRowNum() + 1;
        Row row;

        for(int run = 0; run < observer.getExecutions().size(); run++){

            for(int iteration = 0; iteration < observer.getExecutions().get(run).numberOfIterations(); iteration++){

                row = sheet.createRow(rowCounter++);

                row.createCell(0).setCellValue("??");
                row.createCell(1).setCellValue(run + 1);
                row.createCell(2).setCellValue(iteration + 1);
                row.createCell(3).setCellValue(observer.getExecutions().get(run).reachedMakespanList().get(iteration));
                row.createCell(4).setCellValue(observer.getExecutions().get(run).improvementRatioWithRespectToLastIteration().get(iteration));
                row.createCell(5).setCellValue(observer.getExecutions().get(run).numberOfGeneratedNeighborsList().get(iteration));

                if (observer.getExecutions().get(run).betterNeighborsRatioList().isEmpty()) {
                    row.createCell(6).setCellValue("-");
                    row.createCell(7).setCellValue("-");
                    row.createCell(8).setCellValue("-");
                } else {
                    row.createCell(6).setCellValue(observer.getExecutions().get(run).betterNeighborsRatioList().get(iteration));
                    row.createCell(7).setCellValue(observer.getExecutions().get(run).allNeighborsImprovingRatioList().get(iteration));
                    row.createCell(8).setCellValue(observer.getExecutions().get(run).betterNeighborsImprovingRatioList().get(iteration));
                }
            }
        }
    }

    private static void appendResumeSheet(Workbook workbook, NeighborhoodObserver observer) {

        Sheet sheet = workbook.getSheet("Resume");

        Row row = sheet.createRow( sheet.getLastRowNum() + 1 );

        row.createCell(0).setCellValue(observer.getStrategyName());
        row.createCell(1).setCellValue(observer.avgReachedCost());
        row.createCell(2).setCellValue(observer.avgExecutionTime());
        row.createCell(3).setCellValue(observer.avgIterations());
        row.createCell(4).setCellValue(observer.avgGeneratedNeighbors());
    }

    private static void appendDataSheet(Workbook workbook, NeighborhoodObserver observer) {

        Sheet sheet = workbook.getSheet("Data");

        int rowCounter = sheet.getLastRowNum() + 1;

        for(int i = 0; i < observer.getExecutions().size(); i++) {

            Row row = sheet.createRow(rowCounter + i);
            row.createCell(0).setCellValue(observer.getExecutions().get(i).strategyName());
            row.createCell(1).setCellValue(i + 1);
            row.createCell(2).setCellValue(observer.getExecutions().get(i).totalReachedMakespan());
            row.createCell(3).setCellValue(observer.getExecutions().get(i).executionTime());
            row.createCell(4).setCellValue(observer.getExecutions().get(i).numberOfIterations());
            row.createCell(5).setCellValue(observer.getExecutions().get(i).avgGeneratedNeighbors());

            if (observer.getExecutions().get(i).betterNeighborsRatioList().isEmpty()) {
                row.createCell(6).setCellValue("-");
                row.createCell(7).setCellValue("-");
                row.createCell(8).setCellValue("-");
            } else {
                row.createCell(6).setCellValue(observer.getExecutions().get(i).avgBetterNeighborsRatio());
                row.createCell(7).setCellValue(observer.getExecutions().get(i).avgAllNeighborsImprovingRatio());
                row.createCell(8).setCellValue(observer.getExecutions().get(i).avgBetterNeighborsImprovingRatio());
            }
        }

    }

    private static void createDetailSheet(Workbook workbook) {
        Sheet sheet = workbook.createSheet("Detail");

        Row headerRow = sheet.createRow(2);
        headerRow.createCell(0).setCellValue("Experiment");
        headerRow.createCell(1).setCellValue("Run number");
        headerRow.createCell(2).setCellValue("Iteration number");
        headerRow.createCell(3).setCellValue("Best makespan reached");
        headerRow.createCell(4).setCellValue("Improvement ratio with respect to last iteration");
        headerRow.createCell(5).setCellValue("Generated neighbors");
        headerRow.createCell(6).setCellValue("Average percentage of neighbors that outperform their source solution");
        headerRow.createCell(7).setCellValue("Average improvement ratio from all neighbors");
        headerRow.createCell(8).setCellValue("Average improvement ratio from neighbors that outperform their source solution");

    }

    private static void createResumeSheet(Workbook workbook) {
        Sheet sheet = workbook.createSheet("Resume");

        Row headerRow = sheet.createRow(2);
        headerRow.createCell(0).setCellValue("Method");
        headerRow.createCell(1).setCellValue("Avg(Best_Mkp)");
        headerRow.createCell(2).setCellValue("Avg(Exec_Time)");
        headerRow.createCell(3).setCellValue("Avg(LS_Iters)");
        headerRow.createCell(4).setCellValue("Avg(Neighb)");

    }

    private static void createDataSheet(Workbook workbook){

        Sheet sheet = workbook.createSheet("Data");

        Row headerRow = sheet.createRow(0);
        headerRow.createCell(0).setCellValue("Method");
        headerRow.createCell(1).setCellValue("Run number");
        headerRow.createCell(2).setCellValue("Best makespan reached");
        headerRow.createCell(3).setCellValue("Executing time");
        headerRow.createCell(4).setCellValue("Number of local search iterations");
        headerRow.createCell(5).setCellValue("Average generated neighbors");

        headerRow.createCell(6).setCellValue("Average percentage of neighbors that outperform their source solution");
        headerRow.createCell(7).setCellValue("Average improvement ratio from all neighbors");
        headerRow.createCell(8).setCellValue("Average improvement ratio from neighbors that outperform their source solution");

    }
}
