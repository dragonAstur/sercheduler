package com.uniovi.sercheduler.service;

import com.uniovi.sercheduler.dto.Host;
import com.uniovi.sercheduler.dto.Task;
import com.uniovi.sercheduler.dto.TaskFile;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public abstract class MakespanCalculator {

  Map<String, Host> hosts;
  Map<String, Task> workflow;

  public MakespanCalculator(Map<String, Host> hosts, Map<String, Task> workflow) {
    this.hosts = hosts;
    this.workflow = workflow;
  }

  public Map<String, Map<String, Double>> calculateComputationMatrix(Long referenceFlops) {

    return workflow.values().stream()
        .map(
            task ->
                Map.entry(
                    task.getName(),
                    hosts.values().stream()
                        .map(
                            host ->
                                Map.entry(
                                    host.getName(),
                                    task.getRuntime()
                                        * (referenceFlops / host.getFlops().doubleValue())))
                        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue))))
        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
  }

  public Map<String, Map<String, Long>> calculateNetworkMatrix() {

    return workflow.values().stream()
        .map(
            task ->
                Map.entry(
                    task,
                    task.getParents().stream()
                        .map(
                            parent -> {
                              Long bitsTransferred =
                                  parent.getOutput().getFiles().stream()
                                      .filter(
                                          f ->
                                              task.getInput().getFiles().stream()
                                                  .map(TaskFile::getName)
                                                  .toList()
                                                  .contains(f.getName()))
                                      .map(TaskFile::getSize)
                                      .reduce(0L, Long::sum);

                              return Map.entry(parent.getName(), bitsTransferred);
                            })
                        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue))))
        .map(
            entry -> {
                  // Do the staging
              Task task = entry.getKey();
              Map<String, Long> comms = entry.getValue();
              Long tasksBits = comms.values().stream().reduce(0L, Long::sum);
              var newComms =
                  Stream.concat(
                          comms.entrySet().stream(),
                          Stream.of(
                              Map.entry(
                                  task.getName(), task.getInput().getSizeInBits() - tasksBits)))
                      .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
              return Map.entry(task.getName(), newComms);
            })
        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

    // TODO: ADD staging values
  }
}
