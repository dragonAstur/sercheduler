package com.uniovi.sercheduler.service;

import com.uniovi.sercheduler.dto.Host;
import com.uniovi.sercheduler.dto.InstanceData;
import com.uniovi.sercheduler.dto.Task;
import java.util.Map;

/**
 * Implementation for calculating the makespan using DNC model.
 */
public class MakespanCalculatorSimple extends MakespanCalculator {
  public MakespanCalculatorSimple(InstanceData instanceData) {
    super(instanceData);
  }
}
