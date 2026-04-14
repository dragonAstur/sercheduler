package com.uniovi.sercheduler.localsearch.algorithms.localsearchcomponents;

import com.uniovi.sercheduler.localsearch.operator.GeneratedNeighbor;

import java.util.Optional;
import java.util.stream.Stream;

public class NeighborLimiterImpl implements NeighborLimiter {

    private int neighborsLimit = -1;

    public NeighborLimiterImpl(int neighborsLimit){
        this.neighborsLimit = neighborsLimit;
    }

    @Override
    public Stream<GeneratedNeighbor> limitNeighborsNumber(Stream<GeneratedNeighbor> neighbors) {

        if(neighborsLimit <= 0) return neighbors;

        return neighbors.limit(neighborsLimit);
    }

    @Override
    public void setNeighborsLimit(int neighborsLimit) {
        this.neighborsLimit = neighborsLimit;
    }
}
