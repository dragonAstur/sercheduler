package com.uniovi.sercheduler.util;

import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class CsvUtils {

  public static void writeMapsToCsvTransposed(
      String filename, List<Map<String, Integer>> maps, List<String> mapNames) throws IOException {
    if (maps.size() != mapNames.size()) {
      throw new IllegalArgumentException("The number of maps and map names must match.");
    }

    // Collect all unique keys from all maps
    Set<String> allKeys = new LinkedHashSet<>();
    for (Map<String, Integer> map : maps) {
      allKeys.addAll(map.keySet());
    }

    try (FileWriter csvWriter = new FileWriter(filename)) {
      // Write the header row with all keys
      csvWriter.append("Map");
      for (String key : allKeys) {
        csvWriter.append(",").append(key);
      }
      csvWriter.append("\n");

      // Iterate over each map and write the map name and values for each key
      for (int i = 0; i < maps.size(); i++) {
        csvWriter.append(mapNames.get(i)); // Use the provided map name
        Map<String, Integer> map = maps.get(i);
        for (String key : allKeys) {
          csvWriter.append(",").append(map.getOrDefault(key, 0).toString());
        }
        csvWriter.append("\n");
      }

      csvWriter.flush();
    }
  }

    public static void writeArrayToCSV(String[] array, String fileName) {
        try (FileWriter csvWriter = new FileWriter(fileName)) {
            for (int i = 0; i < array.length; i++) {
                csvWriter.append(i + 1 + "," + array[i] + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
