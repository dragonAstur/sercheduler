package com.uniovi.sercheduler.parser;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.uniovi.sercheduler.dao.ScientificWorkflowDao;
import com.uniovi.sercheduler.dao.WorkflowDao;
import com.uniovi.sercheduler.dto.Direction;
import com.uniovi.sercheduler.dto.FileList;
import com.uniovi.sercheduler.dto.Task;
import com.uniovi.sercheduler.dto.TaskFile;
import com.uniovi.sercheduler.expception.WorkflowLoadException;
import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

/** Loader of workflows from files. */
@Primary
@Service
public class WorkflowFileLoader implements WorkflowLoader {

  /**
   * Loads the workflow from a file.
   *
   * @param workflowDao Object in SWF commons format.
   * @return The List of files.
   */
  @Override
  public Map<String, Task> load(WorkflowDao workflowDao) {
    Map<String, Task> workflowTmp =
        workflowDao.tasks().stream()
            .map(
                t -> {
                  var inputFiles =
                      t.files().stream()
                          .filter(f -> f.link().equals(Direction.INPUT.link))
                          .map(f -> new TaskFile(f.name(), Direction.INPUT, f.size() * 8))
                          .toList();

                  var outputFiles =
                      t.files().stream()
                          .filter(f -> f.link().equals(Direction.OUTPUT.link))
                          .map(f -> new TaskFile(f.name(), Direction.OUTPUT, f.size() * 8))
                          .toList();

                  // Need to calculate the total bits transfered

                  var inputBits = inputFiles.stream().map(TaskFile::getSize).reduce(0L, Long::sum);
                  var outputBits =
                      outputFiles.stream().map(TaskFile::getSize).reduce(0L, Long::sum);
                  return new Task(
                      t.name(),
                      t.runtime(),
                      Collections.emptyList(),
                      Collections.emptyList(),
                      new FileList(inputFiles, inputBits),
                      new FileList(outputFiles, outputBits));
                })
            .collect(Collectors.toMap(Task::getName, Function.identity()));

    // In the second iteration we can assign the already created tasks.
    return workflowDao.tasks().stream()
        .map(
            t -> {
              var task = workflowTmp.get(t.name());
              var parents = t.parents().stream().map(workflowTmp::get).toList();
              var children = t.children().stream().map(workflowTmp::get).toList();

              task.setParents(parents);
              task.setChildren(children);

              return task;
            })
        .collect(Collectors.toMap(Task::getName, Function.identity()));
  }

  /**
   * Reads a Json workflow from a file.
   *
   * @param workflowJson Json containing a workflow.
   * @return The parsed object.
   */
  @Override
  public WorkflowDao readFromFile(File workflowJson) {

    ObjectMapper mapper = new ObjectMapper();
    mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    try {
      return mapper.readValue(workflowJson, ScientificWorkflowDao.class).workflow();
    } catch (IOException e) {
      throw new WorkflowLoadException(workflowJson.getName(), e);
    }
  }
}
