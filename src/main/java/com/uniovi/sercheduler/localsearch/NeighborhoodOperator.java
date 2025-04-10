package com.uniovi.sercheduler.localsearch;

import org.uma.jmetal.operator.Operator;

public interface NeighborhoodOperator<Source, Result> extends Operator<Source, Result> {

    Result execute(Source actualSolution);
}
