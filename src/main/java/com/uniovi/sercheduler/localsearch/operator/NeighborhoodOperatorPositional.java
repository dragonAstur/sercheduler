package com.uniovi.sercheduler.localsearch.operator;

public interface NeighborhoodOperatorPositional<Source, Result> extends NeighborhoodOperator  {

    Result execute(Source actualSolution, int position);

}
