package com.uniovi.sercheduler.localsearch.operator;

public interface NeighborhoodOperatorMulti<Result, Source> extends NeighborhoodOperator {

    Result execute(Source actualSolution, NeighborhoodOperator operator1, NeighborhoodOperator operator2);

}
