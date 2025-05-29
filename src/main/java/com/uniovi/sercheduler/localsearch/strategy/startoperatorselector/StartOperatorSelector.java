package com.uniovi.sercheduler.localsearch.strategy.startoperatorselector;

import com.uniovi.sercheduler.localsearch.operator.NeighborhoodOperatorGlobal;
import com.uniovi.sercheduler.localsearch.operator.NeighborhoodOperatorLazy;

import java.util.List;

public interface StartOperatorSelector {

    List<NeighborhoodOperatorLazy> selectOperatorsLazy(List<NeighborhoodOperatorLazy> originalList);

    List<NeighborhoodOperatorGlobal> selectOperatorsGlobal(List<NeighborhoodOperatorGlobal> neighborhoodOperatorList);
}
