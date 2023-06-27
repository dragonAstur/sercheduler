package com.uniovi.sercheduler.service;

import com.uniovi.sercheduler.dto.Host;
import com.uniovi.sercheduler.dto.Task;
import java.util.Map;

public class MakespanCalculatorSimple extends MakespanCalculator {
  public MakespanCalculatorSimple(Map<String, Host> hosts, Map<String, Task> tasks) {
    super(hosts, tasks);
  }
}
