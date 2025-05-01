package com.uniovi.sercheduler.localsearch.export;

import com.uniovi.sercheduler.localsearch.observer.NeighborhoodObserver;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileOutputStream;
import java.io.IOException;

public class Exporter {

    public static void export(NeighborhoodObserver observer, String fileName){

        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("hoja");

            Row headerRow = sheet.createRow(0);
            headerRow.createCell(0).setCellValue("Iteration number");
            headerRow.createCell(1).setCellValue("Best makespan reached");
            headerRow.createCell(2).setCellValue("Executing time");
            headerRow.createCell(3).setCellValue("Number of local search iterations");
            headerRow.createCell(4).setCellValue("Average generated neighbors");

            headerRow.createCell(5).setCellValue("Average percentage of neighbors that outperform their source solution");
            headerRow.createCell(6).setCellValue("Average improvement ratio from all neighbors");
            headerRow.createCell(7).setCellValue("Average improvement ratio from neighbors that outperform their source solution");

            for(int i = 0; i < observer.getValues().size(); i++) {

                Row row = sheet.createRow(i + 1);
                row.createCell(0).setCellValue(i+1);
                row.createCell(1).setCellValue(observer.getValues().get(i).get("reached_cost"));
                row.createCell(2).setCellValue(observer.getValues().get(i).get("executing_time"));
                row.createCell(3).setCellValue(observer.getValues().get(i).get("local_search_iterations"));
                row.createCell(4).setCellValue(observer.getValues().get(i).get("avg_neighbors_number"));

                if (observer.getValues().get(i).containsKey("avg_better_neighbors_ratio")){
                    row.createCell(5).setCellValue(observer.getValues().get(i).get("avg_better_neighbors_ratio"));
                    row.createCell(6).setCellValue(observer.getValues().get(i).get("avg_all_neighbors_improving_ratio"));
                    row.createCell(7).setCellValue(observer.getValues().get(i).get("avg_better_neighbors_improving_ratio"));
                } else {
                    row.createCell(5).setCellValue("-");
                    row.createCell(6).setCellValue("-");
                    row.createCell(7).setCellValue("-");
                }
            }

            // Write the workbook to a file
            try (FileOutputStream outputStream = new FileOutputStream(fileName + ".xlsx")) {
                workbook.write(outputStream);
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}
