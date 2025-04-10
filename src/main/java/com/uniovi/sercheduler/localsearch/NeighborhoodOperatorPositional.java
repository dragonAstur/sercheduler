package com.uniovi.sercheduler.localsearch;

public interface NeighborhoodOperatorPositional<Source, Result>  {

    Result execute(Source actualSolution, int position);

}
