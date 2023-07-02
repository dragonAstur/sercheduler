package com.uniovi.sercheduler.util;

import com.uniovi.sercheduler.dto.InstanceData;
import com.uniovi.sercheduler.parser.HostFileLoader;
import com.uniovi.sercheduler.parser.HostLoader;
import com.uniovi.sercheduler.parser.WorkflowFileLoader;
import com.uniovi.sercheduler.parser.WorkflowLoader;
import java.io.IOException;
import org.springframework.core.io.ClassPathResource;

public class LoadTestInstanceData {

  public static InstanceData loadCalculatorTest() {
    try {
      HostLoader hostLoader = new HostFileLoader(new UnitParser());
      WorkflowLoader workflowLoader = new WorkflowFileLoader();

      var hostsJson = new ClassPathResource("calculator/hosts.json").getFile();
      var hostsDao = hostLoader.readFromFile(hostsJson);
      var hosts = hostLoader.load(hostsDao);

      var workflowJson = new ClassPathResource("calculator/workflow.json").getFile();
      var workflowDao = workflowLoader.readFromFile(workflowJson);
      var workflow = workflowLoader.load(workflowDao);

      return new InstanceData(workflow, hosts);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}
