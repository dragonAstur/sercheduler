package com.uniovi.sercheduler.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.uniovi.sercheduler.parser.HostFileLoader;
import com.uniovi.sercheduler.parser.HostLoader;
import com.uniovi.sercheduler.parser.WorkflowFileLoader;
import com.uniovi.sercheduler.parser.WorkflowLoader;
import com.uniovi.sercheduler.util.UnitParser;
import java.io.IOException;
import java.util.AbstractMap;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;

public class MakespanCalculatorTest {

  // The tests uses the following graph
  /*
   *           │10
   *        ┌──▼─┐
   *    ┌───┤ T1 ├────┐
   *    │   └─┬──┘    │
   *    │18   │12     │8
   * ┌──▼─┐ ┌─▼──┐ ┌──▼─┐16
   * │ T2 │ │ T3 │ │ T4 ◄──
   * └──┬─┘ └─┬──┘ └──┬─┘
   *    │20   │24     │28
   *    │   ┌─▼──┐    │
   *    └───► T5 ◄────┘
   *        └────┘
   */
  @Test
  void calculateComputationMatrix() throws IOException {

    // Given a workflow of 5 tasks
    // Given a hosts list of 3

    HostLoader hostLoader = new HostFileLoader(new UnitParser());
    WorkflowLoader workflowLoader = new WorkflowFileLoader();

    var hostsJson = new ClassPathResource("calculator/hosts.json").getFile();
    var hostsDao = hostLoader.readFromFile(hostsJson);
    var hosts = hostLoader.load(hostsDao);

    var workflowJson = new ClassPathResource("calculator/workflow.json").getFile();
    var workflowDao = workflowLoader.readFromFile(workflowJson);
    var workflow = workflowLoader.load(workflowDao);

    // Then

    var expected =
        Map.ofEntries(
            new AbstractMap.SimpleEntry<>("task01", Map.of("HostA", 10D, "HostB", 5D, "HostC", 4D)),
            new AbstractMap.SimpleEntry<>(
                "task02", Map.of("HostA", 15D, "HostB", 7.5D, "HostC", 6D)),
            new AbstractMap.SimpleEntry<>(
                "task03", Map.of("HostA", 5D, "HostB", 2.5D, "HostC", 2D)),
            new AbstractMap.SimpleEntry<>(
                "task04", Map.of("HostA", 20D, "HostB", 10D, "HostC", 8D)),
            new AbstractMap.SimpleEntry<>(
                "task05", Map.of("HostA", 8D, "HostB", 4D, "HostC", 3.2D)));

    MakespanCalculator makespanCalculator = new MakespanCalculatorSimple(hosts, workflow);
    var result = makespanCalculator.calculateComputationMatrix(new UnitParser().parseUnits("1Gf"));

    assertEquals(expected, result);
  }

  @Test
  void calculateNetworkMatrix() throws IOException {

    // Given a workflow of 5 tasks
    // Given a hosts list of 3

    HostLoader hostLoader = new HostFileLoader(new UnitParser());
    WorkflowLoader workflowLoader = new WorkflowFileLoader();

    var hostsJson = new ClassPathResource("calculator/hosts.json").getFile();
    var hostsDao = hostLoader.readFromFile(hostsJson);
    var hosts = hostLoader.load(hostsDao);

    var workflowJson = new ClassPathResource("calculator/workflow.json").getFile();
    var workflowDao = workflowLoader.readFromFile(workflowJson);
    var workflow = workflowLoader.load(workflowDao);

    var expected =
        Map.ofEntries(
            new AbstractMap.SimpleEntry<>("task01", Map.of("task01", 80000000L)),
            new AbstractMap.SimpleEntry<>("task02", Map.of("task01", 144000000L, "task02", 0L)),
            new AbstractMap.SimpleEntry<>("task03", Map.of("task01", 96000000L, "task03", 0L)),
            new AbstractMap.SimpleEntry<>(
                "task04", Map.of("task01", 64000000L, "task04", 128000000L)),
            new AbstractMap.SimpleEntry<>(
                "task05",
                Map.of("task02", 160000000L, "task03", 192000000L, "task04", 224000000L, "task05", 0L)));

    MakespanCalculator makespanCalculator = new MakespanCalculatorSimple(hosts, workflow);
    Map<String, Map<String, Long>> result = makespanCalculator.calculateNetworkMatrix();

    assertEquals(expected, result);
  }
}
