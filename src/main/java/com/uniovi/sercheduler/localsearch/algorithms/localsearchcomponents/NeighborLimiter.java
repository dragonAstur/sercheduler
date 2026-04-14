package com.uniovi.sercheduler.localsearch.algorithms.localsearchcomponents;

import com.uniovi.sercheduler.localsearch.operator.GeneratedNeighbor;

import java.util.Optional;
import java.util.stream.Stream;

public interface NeighborLimiter {

    Stream<GeneratedNeighbor> limitNeighborsNumber(Stream<GeneratedNeighbor> neighbors);

    void setNeighborsLimit(int neighborsLimit);

}
