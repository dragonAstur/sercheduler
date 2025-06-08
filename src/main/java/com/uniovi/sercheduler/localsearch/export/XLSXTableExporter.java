package com.uniovi.sercheduler.localsearch.export;

import com.uniovi.sercheduler.localsearch.observer.LocalSearchObserver;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

public class XLSXTableExporter {

    public static void createWorkbook(String fileName){

        try (Workbook workbook = new XSSFWorkbook()) {

            createSummarySheet(fileName, workbook);
            createBKPercentageSheet(fileName, workbook);

            // Write the workbook to a file
            try (FileOutputStream outputStream = new FileOutputStream(fileName + ".xlsx")) {
                workbook.write(outputStream);
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    public static void createInstanceSheet(String fileName, String instanceName){

        try (Workbook workbook = new XSSFWorkbook(new FileInputStream(fileName + ".xlsx"))) {

            Sheet sheet = workbook.createSheet(instanceName);
            Row headerRow = sheet.createRow(0);

            headerRow.createCell(1).setCellValue("Run");

            for(int i = 1; i <= 30; i++)
                headerRow.createCell(i+1).setCellValue(i);

            headerRow.createCell(32).setCellValue("");
            headerRow.createCell(33).setCellValue("min");
            headerRow.createCell(34).setCellValue("avg");
            headerRow.createCell(35).setCellValue("max");
            headerRow.createCell(36).setCellValue("standard deviation");

            headerRow.createCell(38).setCellValue("avg starts per run");
            headerRow.createCell(39).setCellValue("avg evaluated neighbors per run");

            headerRow.createCell(41).setCellValue("Time for finding best solution");
            headerRow.createCell(42).setCellValue("Generated neighbors for finding best solution");

            // Write the workbook to a file
            try (FileOutputStream outputStream = new FileOutputStream(fileName + ".xlsx")) {
                workbook.write(outputStream);
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    public static void appendInstanceSheet(String fileName, String instanceName, LocalSearchObserver observer, String operatorLabel){

        try (Workbook workbook = new XSSFWorkbook(new FileInputStream(fileName + ".xlsx"))) {

            Sheet sheet = workbook.getSheet(instanceName);

            Row row = sheet.createRow( sheet.getLastRowNum() + 1 );

            row.createCell(0).setCellValue(observer.getStrategyName());
            row.createCell(1).setCellValue(operatorLabel);

            for(int i = 1; i <= 30; i++)
                row.createCell(i+1).setCellValue(observer.getRuns().get(i-1).minStartsReachedMakespan());

            row.createCell(32).setCellValue("");
            row.createCell(33).setCellValue(observer.getBestMinReachedMakespan());
            row.createCell(34).setCellValue(observer.getAvgMinReachedMakespan());
            row.createCell(35).setCellValue(observer.getWorstMinReachedMakespan());
            row.createCell(36).setCellValue(observer.standardDeviation());


            row.createCell(38).setCellValue(observer.avgStartsPerRun());
            row.createCell(39).setCellValue(observer.avgGeneratedNeighborsMultiStart());

            row.createCell(41).setCellValue(observer.getTimeForFindingBestSolution());
            row.createCell(42).setCellValue(observer.getGeneratedNeighborsForFindingBestSolution());

            // Write the workbook to a file
            try (FileOutputStream outputStream = new FileOutputStream(fileName + ".xlsx")) {
                workbook.write(outputStream);
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    private static void createSummarySheet(String fileName, Workbook workbook){

        Sheet sheet = workbook.createSheet("Summary");
        Row headerRow = sheet.createRow(0);

        headerRow.createCell(1).setCellValue("DHC");
        headerRow.createCell(17).setCellValue("HC");

        headerRow = sheet.createRow(1);

        headerRow.createCell(0).setCellValue("Instances");

        headerRow.createCell(1).setCellValue("N1");
        headerRow.createCell(2).setCellValue("N2");
        headerRow.createCell(3).setCellValue("N3");
        headerRow.createCell(4).setCellValue("N4");
        headerRow.createCell(5).setCellValue("N1 U N2");
        headerRow.createCell(6).setCellValue("N1 U N3");
        headerRow.createCell(7).setCellValue("N1 U N4");
        headerRow.createCell(8).setCellValue("N2 U N3");
        headerRow.createCell(9).setCellValue("N2 U N4");
        headerRow.createCell(10).setCellValue("N3 U N4");
        headerRow.createCell(11).setCellValue("N1 U N2 U N3");
        headerRow.createCell(12).setCellValue("N1 U N2 U N4");
        headerRow.createCell(13).setCellValue("N1 U N3 U N4");
        headerRow.createCell(14).setCellValue("N2 U N3 U N4");
        headerRow.createCell(15).setCellValue("N1 U N2 U N3 U N4");
        headerRow.createCell(16).setCellValue("VNS (random choice)");

        headerRow.createCell(17).setCellValue("N1");
        headerRow.createCell(18).setCellValue("N2");
        headerRow.createCell(19).setCellValue("N3");
        headerRow.createCell(20).setCellValue("N4");
        headerRow.createCell(21).setCellValue("N1 U N2");
        headerRow.createCell(22).setCellValue("N1 U N3");
        headerRow.createCell(23).setCellValue("N1 U N4");
        headerRow.createCell(24).setCellValue("N2 U N3");
        headerRow.createCell(25).setCellValue("N2 U N4");
        headerRow.createCell(26).setCellValue("N3 U N4");
        headerRow.createCell(27).setCellValue("N1 U N2 U N3");
        headerRow.createCell(28).setCellValue("N1 U N2 U N4");
        headerRow.createCell(29).setCellValue("N1 U N3 U N4");
        headerRow.createCell(30).setCellValue("N2 U N3 U N4");
        headerRow.createCell(31).setCellValue("N1 U N2 U N3 U N4");
        headerRow.createCell(32).setCellValue("VNS (random choice)");

        headerRow.createCell(34).setCellValue("Execution time limit (milliseconds)");

    }


    public static void appendSummarySheet(String fileName, String instanceName, List<Double> avgMakespanList, long timeLimit){

        try (Workbook workbook = new XSSFWorkbook(new FileInputStream(fileName + ".xlsx"))) {

            Sheet sheet = workbook.getSheet("Summary");

            Row row = sheet.createRow(sheet.getLastRowNum() + 1);

            row.createCell(0).setCellValue(instanceName);

            for(int i = 0; i < avgMakespanList.size(); i++)
                row.createCell(i+1).setCellValue( avgMakespanList.get(i) );

            row.createCell(34).setCellValue(timeLimit);

            // Write the workbook to a file
            try (FileOutputStream outputStream = new FileOutputStream(fileName + ".xlsx")) {
                workbook.write(outputStream);
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }


    private static void createBKPercentageSheet(String fileName, Workbook workbook){

        Sheet sheet = workbook.createSheet("%BK");
        Row headerRow = sheet.createRow(0);

        headerRow.createCell(1).setCellValue("DHC");
        headerRow.createCell(17).setCellValue("HC");

        headerRow = sheet.createRow(1);

        headerRow.createCell(0).setCellValue("Instances");

        headerRow.createCell(1).setCellValue("N1");
        headerRow.createCell(2).setCellValue("N2");
        headerRow.createCell(3).setCellValue("N3");
        headerRow.createCell(4).setCellValue("N4");
        headerRow.createCell(5).setCellValue("N1 U N2");
        headerRow.createCell(6).setCellValue("N1 U N3");
        headerRow.createCell(7).setCellValue("N1 U N4");
        headerRow.createCell(8).setCellValue("N2 U N3");
        headerRow.createCell(9).setCellValue("N2 U N4");
        headerRow.createCell(10).setCellValue("N3 U N4");
        headerRow.createCell(11).setCellValue("N1 U N2 U N3");
        headerRow.createCell(12).setCellValue("N1 U N2 U N4");
        headerRow.createCell(13).setCellValue("N1 U N3 U N4");
        headerRow.createCell(14).setCellValue("N2 U N3 U N4");
        headerRow.createCell(15).setCellValue("N1 U N2 U N3 U N4");
        headerRow.createCell(16).setCellValue("VNS (random choice)");

        headerRow.createCell(17).setCellValue("N1");
        headerRow.createCell(18).setCellValue("N2");
        headerRow.createCell(19).setCellValue("N3");
        headerRow.createCell(20).setCellValue("N4");
        headerRow.createCell(21).setCellValue("N1 U N2");
        headerRow.createCell(22).setCellValue("N1 U N3");
        headerRow.createCell(23).setCellValue("N1 U N4");
        headerRow.createCell(24).setCellValue("N2 U N3");
        headerRow.createCell(25).setCellValue("N2 U N4");
        headerRow.createCell(26).setCellValue("N3 U N4");
        headerRow.createCell(27).setCellValue("N1 U N2 U N3");
        headerRow.createCell(28).setCellValue("N1 U N2 U N4");
        headerRow.createCell(29).setCellValue("N1 U N3 U N4");
        headerRow.createCell(30).setCellValue("N2 U N3 U N4");
        headerRow.createCell(31).setCellValue("N1 U N2 U N3 U N4");
        headerRow.createCell(32).setCellValue("VNS (random choice)");

        headerRow.createCell(34).setCellValue("Execution time limit (milliseconds)");

    }

    public static void appendBKPercentageSheet(String fileName, String instanceName, List<Double> bestKnownPercentageList, long timeLimit){

        try (Workbook workbook = new XSSFWorkbook(new FileInputStream(fileName + ".xlsx"))) {

            Sheet sheet = workbook.getSheet("%BK");

            Row row = sheet.createRow(sheet.getLastRowNum() + 1);

            row.createCell(0).setCellValue(instanceName);

            for(int i = 0; i < bestKnownPercentageList.size(); i++)
                row.createCell(i+1).setCellValue( bestKnownPercentageList.get(i) );

            row.createCell(34).setCellValue(timeLimit);

            // Write the workbook to a file
            try (FileOutputStream outputStream = new FileOutputStream(fileName + ".xlsx")) {
                workbook.write(outputStream);
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

}
