package com.uniovi.sercheduler.localsearch.export;

import com.uniovi.sercheduler.localsearch.observer.NeighborhoodObserver;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class XLSXTableExporter {

    public static void createWorkbook(String fileName){

        try (Workbook workbook = new XSSFWorkbook()) {

            // Write the workbook to a file
            try (FileOutputStream outputStream = new FileOutputStream(fileName + ".xlsx")) {
                workbook.write(outputStream);
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    public static void createInstanceSheet(String fileName, String instanceName){

        try (Workbook workbook = new XSSFWorkbook()) {

            Sheet sheet = workbook.createSheet(instanceName);
            Row headerRow = sheet.createRow(0);

            headerRow.createCell(1).setCellValue("Execution");

            for(int i = 1; i <= 30; i++)
                headerRow.createCell(i+1).setCellValue(i);

            headerRow.createCell(32).setCellValue("");
            headerRow.createCell(33).setCellValue("min");
            headerRow.createCell(34).setCellValue("avg");
            headerRow.createCell(35).setCellValue("max");
            headerRow.createCell(36).setCellValue("standard deviation");

            // Write the workbook to a file
            try (FileOutputStream outputStream = new FileOutputStream(fileName + ".xlsx")) {
                workbook.write(outputStream);
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    public static void appendInstanceSheet(String fileName, String instanceName, NeighborhoodObserver observer, String operatorLabel){

        try (Workbook workbook = new XSSFWorkbook(new FileInputStream(fileName + ".xlsx"))) {

            Sheet sheet = workbook.getSheet(instanceName);

            Row row = sheet.createRow( sheet.getLastRowNum() + 1 );

            row.createCell(0).setCellValue(observer.getStrategyName());
            row.createCell(1).setCellValue(operatorLabel);

            for(int i = 1; i <= 30; i++)
                row.createCell(i+1).setCellValue(observer.getExecutions().get(i-1).bestReachedMakespan());

            row.createCell(32).setCellValue("");
            row.createCell(33).setCellValue(observer.getBestReachedMakespan());
            row.createCell(34).setCellValue(observer.avgReachedCost());
            row.createCell(35).setCellValue(observer.getWorstReachedMakespan());
            row.createCell(36).setCellValue(observer.standardDeviation());

            // Write the workbook to a file
            try (FileOutputStream outputStream = new FileOutputStream(fileName + ".xlsx")) {
                workbook.write(outputStream);
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

}
