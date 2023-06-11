package com.uniovi.sercheduler.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;

public class WorkflowFileLoaderTest {

  WorkflowFileLoader workflowFileLoader = new WorkflowFileLoader();

  @Test
  void workflowJsonShouldLoad() throws IOException {

    var workflowJson = new ClassPathResource("workflow_test.json").getFile();
    var workflowDao = workflowFileLoader.readFromFile(workflowJson);
    var workflow = workflowFileLoader.load(workflowDao);

    assertEquals(10, workflow.size());

    assertEquals(5, workflow.get("task01").getChildren().size());

    // We need to check if the children have the up and down references
    assertEquals(
        workflow.get("task02"),
        workflow.get("task01").getChildren().stream()
            .filter(c -> "task02".equals(c.getName()))
            .findAny()
            .orElseThrow());
  }
}
