package com.uniovi.sercheduler.localsearch.algorithms.localsearchcomponents;

import com.uniovi.sercheduler.localsearch.evaluator.LocalsearchEvaluator;
import com.uniovi.sercheduler.service.FitnessCalculator;

public interface LocalSearchEvaluatorGenerator {

    LocalsearchEvaluator createLocalSearchEvaluator(FitnessCalculator fitnessCalculator);
}
