package com.uniovi.sercheduler.memetic.algorithm.components;

import org.uma.jmetal.component.catalogue.common.termination.Termination;

import java.util.Map;

public interface MemeticTermination {
    boolean isMet(Map<String, Object> var1);
}
