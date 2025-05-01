package com.uniovi.sercheduler.localsearch.strategy;

import com.uniovi.sercheduler.jmetal.problem.SchedulePermutationSolution;
import com.uniovi.sercheduler.jmetal.problem.SchedulingProblem;
import com.uniovi.sercheduler.localsearch.evaluator.LocalsearchEvaluator;
import com.uniovi.sercheduler.localsearch.observer.NeighborhoodObserver;
import com.uniovi.sercheduler.localsearch.operator.GeneratedNeighbor;
import com.uniovi.sercheduler.localsearch.operator.NeighborhoodOperatorGlobal;
import com.uniovi.sercheduler.service.FitnessCalculatorSimple;
import com.uniovi.sercheduler.service.FitnessInfo;

import java.util.List;

public class MaximumGradientStrategy extends AbstractStrategy {

    private double sumOfBetterNeighborsRatio = 0;
    private double sumOfAllNeighborsImprovingRatio = 0;
    private double sumOfBetterNeighborsImprovingRatio = 0;

    public MaximumGradientStrategy(NeighborhoodObserver observer) {
        super(observer);
    }

    public SchedulePermutationSolution execute(SchedulingProblem problem, NeighborhoodOperatorGlobal neighborhoodOperator){

        getObserver().newIteration();
        int localSearchIterations = 0;
        int numberOfGeneratedNeighborsSum = 0;
        long startingTime = System.currentTimeMillis();

        //Generate an inicial random solution
        SchedulePermutationSolution actualSolution = problem.createSolution();

        //Evaluate this new created solution (this step is skipped in the pseudocode)
        FitnessCalculatorSimple fitnessCalculator = new FitnessCalculatorSimple(problem.getInstanceData());
        FitnessInfo fitnessInfo = fitnessCalculator.calculateFitness(actualSolution);
        actualSolution.setFitnessInfo(fitnessInfo);

        //Initialize the control variable, a variable for storing their neighbors and a
        boolean upgradeFound;
        List<GeneratedNeighbor> neighborsList;
        SchedulePermutationSolution bestNeighbor;
        LocalsearchEvaluator evaluator = new LocalsearchEvaluator(fitnessCalculator.getComputationMatrix(), fitnessCalculator.getNetworkMatrix(), problem.getInstanceData());

        do{

            localSearchIterations++;

            upgradeFound = false;

            //Generate new neighbors
            neighborsList = neighborhoodOperator.execute(actualSolution);

            numberOfGeneratedNeighborsSum += neighborsList.size();

            //Take the best one
            bestNeighbor = selectBestNeighbor(actualSolution, neighborsList, evaluator);

            //If there is an improvement, record it and update the best neighbor
            if(bestNeighbor.getFitnessInfo().fitness().get("makespan") < actualSolution.getFitnessInfo().fitness().get("makespan")){
                actualSolution = bestNeighbor;
                upgradeFound = true;
            }

        } while(upgradeFound);

        getObserver().setExecutingTime(System.currentTimeMillis() - startingTime);
        getObserver().setLocalSearchIterations(localSearchIterations);
        getObserver().setReachedCost(actualSolution.getFitnessInfo().fitness().get("makespan"));
        getObserver().setAvgNeighborsNumber(numberOfGeneratedNeighborsSum * 1.0 / localSearchIterations);
        getObserver().setAvgBetterNeighborsRatio(sumOfBetterNeighborsRatio / localSearchIterations);
        getObserver().setAvgAllNeighborsImprovingRatio(sumOfAllNeighborsImprovingRatio / localSearchIterations);
        getObserver().setAvgBetterNeighborsImprovingRatio(sumOfBetterNeighborsImprovingRatio / localSearchIterations);

        return actualSolution;
    }

    private SchedulePermutationSolution selectBestNeighbor(SchedulePermutationSolution originalSolution, List<GeneratedNeighbor> neighborsList, LocalsearchEvaluator evaluator) {

        SchedulePermutationSolution bestSolution = originalSolution;
        double originalMakespan = originalSolution.getFitnessInfo().fitness().get("makespan");
        double bestMakespan = originalMakespan;
        double neighborMakespan;
        SchedulePermutationSolution neighborSolution;

        int numberOfBetterNeighbors = 0;
        double neighborImprovingRatio = 0.0;
        double allNeighborsImprovingRatioSum = 0.0;
        double betterNeighborsImprovingRatioSum = 0.0;

        for(GeneratedNeighbor neighbor : neighborsList){

            neighborSolution = neighbor.generatedSolution();
            evaluator.evaluate(originalSolution, neighborSolution, neighbor.movements().get(neighbor.movements().size() - 1));
            neighborMakespan = neighborSolution.getFitnessInfo().fitness().get("makespan");

            neighborImprovingRatio = (originalMakespan - neighborMakespan) / originalMakespan * 100;
            allNeighborsImprovingRatioSum += neighborImprovingRatio;

            if(neighborMakespan < originalMakespan){
                numberOfBetterNeighbors++;
                betterNeighborsImprovingRatioSum += neighborImprovingRatio;

                if(bestMakespan > neighborMakespan){
                    bestMakespan = neighborMakespan;
                    bestSolution = neighborSolution;
                }
            }

        }

        sumOfBetterNeighborsRatio += (numberOfBetterNeighbors * 1.00) / neighborsList.size();
        sumOfAllNeighborsImprovingRatio += allNeighborsImprovingRatioSum / neighborsList.size();
        if(numberOfBetterNeighbors > 0)
            sumOfBetterNeighborsImprovingRatio += betterNeighborsImprovingRatioSum / numberOfBetterNeighbors;

        return bestSolution;
    }

}
