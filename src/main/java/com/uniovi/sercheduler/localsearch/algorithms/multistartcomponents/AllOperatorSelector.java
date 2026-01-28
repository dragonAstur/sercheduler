package com.uniovi.sercheduler.localsearch.algorithms.multistartcomponents;

import com.uniovi.sercheduler.localsearch.operator.NeighborhoodOperatorGlobal;
import com.uniovi.sercheduler.localsearch.operator.NeighborhoodOperatorLazy;

import java.util.List;

public class AllOperatorSelector implements OperatorSelector {

    @Override
    public List<NeighborhoodOperatorLazy> selectOperatorsLazy(List<NeighborhoodOperatorLazy> originalList) {
        return originalList;
    }

    @Override
    public List<NeighborhoodOperatorGlobal> selectOperatorsGlobal(List<NeighborhoodOperatorGlobal> originalList) {
        return originalList;
    }

}
