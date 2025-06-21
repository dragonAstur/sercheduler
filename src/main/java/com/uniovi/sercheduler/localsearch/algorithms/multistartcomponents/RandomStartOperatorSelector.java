package com.uniovi.sercheduler.localsearch.algorithms.multistartcomponents;

import com.uniovi.sercheduler.localsearch.operator.NeighborhoodOperatorGlobal;
import com.uniovi.sercheduler.localsearch.operator.NeighborhoodOperatorLazy;

import java.util.List;
import java.util.Random;

public class RandomStartOperatorSelector implements StartOperatorSelector{

    private final Random random = new Random();

    @Override
    public List<NeighborhoodOperatorLazy> selectOperatorsLazy(List<NeighborhoodOperatorLazy> originalList) {

        return List.of(
                originalList.get( random.nextInt(0, originalList.size()) )
        );
    }

    @Override
    public List<NeighborhoodOperatorGlobal> selectOperatorsGlobal(List<NeighborhoodOperatorGlobal> originalList) {
        return List.of(
                originalList.get( random.nextInt(0, originalList.size()) )
        );
    }
}
