package com.uniovi.sercheduler.localsearch.operator;

import org.uma.jmetal.operator.Operator;

public interface NeighborhoodOperatorHost<Source, Result> extends Operator<Source, Result>, NeighborhoodOperator {

    Result execute(Source actualSolution);
}
