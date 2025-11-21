package com.uniovi.sercheduler.service.core;

import com.uniovi.sercheduler.dto.Host;
import com.uniovi.sercheduler.dto.InstanceData;

import com.uniovi.sercheduler.util.UnitParser;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.AbstractMap;
import java.util.Map;

import static com.uniovi.sercheduler.util.LoadTestInstanceData.loadCalculatorTest;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class SchedulingHelperTest {

  // The tests use the following graph
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

    InstanceData instanceData = loadCalculatorTest();

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

    var result =
        SchedulingHelper.calculateComputationMatrix(instanceData, UnitParser.parseUnits("1Gf"));

    assertEquals(expected, result);
  }

  @Test
  void calculateNetworkMatrix() throws IOException {

    // Given a workflow of 5 tasks
    // Given a hosts list of 3
    InstanceData instanceData = loadCalculatorTest();

    var expected =
        Map.ofEntries(
            new AbstractMap.SimpleEntry<>("task01", Map.of("task01", 80000000L)),
            new AbstractMap.SimpleEntry<>("task02", Map.of("task01", 144000000L, "task02", 0L)),
            new AbstractMap.SimpleEntry<>("task03", Map.of("task01", 96000000L, "task03", 0L)),
            new AbstractMap.SimpleEntry<>(
                "task04", Map.of("task01", 64000000L, "task04", 128000000L)),
            new AbstractMap.SimpleEntry<>(
                "task05",
                Map.of(
                    "task02",
                    160000000L,
                    "task03",
                    192000000L,
                    "task04",
                    224000000L,
                    "task05",
                    0L)));

    Map<String, Map<String, Long>> result = SchedulingHelper.calculateNetworkMatrix(instanceData);

    assertEquals(expected, result);
  }

  @Test
  void findHostSpeedSame() {
    var hosts =
        Map.of(
            "HostA",
            new Host("HostA", 100L, 100L, 50L, 0.9D, 0.1D),
            "HostB",
            new Host("HostB", 100L, 25L, 100L, 1.8D, 0.2D));

    var expected = 100L;

    var result = SchedulingHelper.findHostSpeed(hosts.get("HostA"), hosts.get("HostA"));

    assertEquals(expected, result);
  }

  @Test
  void findHostSpeedParentSlowDisk() {
    var hosts =
        Map.of(
            "HostA",
            new Host("HostA", 100L, 100L, 50L, 0.9D, 0.1D),
            "HostB",
            new Host("HostB", 100L, 25L, 100L, 1.8D, 0.2D));

    var expected = 25L;

    var result = SchedulingHelper.findHostSpeed(hosts.get("HostA"), hosts.get("HostB"));

    assertEquals(expected, result);
  }

  @Test
  void findHostSpeedParentSlowNetwork() {
    var hosts =
        Map.of(
            "HostA",
            new Host("HostA", 100L, 100L, 50L, 0.9D, 0.1D),
            "HostB",
            new Host("HostB", 100L, 25L, 100L, 1.8D, 0.2D));

    var expected = 50L;

    var result = SchedulingHelper.findHostSpeed(hosts.get("HostB"), hosts.get("HostA"));

    assertEquals(expected, result);
  }

  @Test
  void findHostSpeedCurrentSlowNetwork() {
    var hosts =
        Map.of(
            "HostA",
            new Host("HostA", 100L, 100L, 50L, 0.9D, 0.1D),
            "HostB",
            new Host("HostB", 100L, 10L, 25L, 1.8D, 0.2D));

    var expected = 25L;

    var result = SchedulingHelper.findHostSpeed(hosts.get("HostB"), hosts.get("HostA"));

    assertEquals(expected, result);
  }
}
