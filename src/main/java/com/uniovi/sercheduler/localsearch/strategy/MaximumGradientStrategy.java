package com.uniovi.sercheduler.localsearch.strategy;

import com.uniovi.sercheduler.jmetal.problem.SchedulePermutationSolution;
import com.uniovi.sercheduler.jmetal.problem.SchedulingProblem;
import com.uniovi.sercheduler.localsearch.evaluator.LocalsearchEvaluator;
import com.uniovi.sercheduler.localsearch.operator.GeneratedNeighbor;
import com.uniovi.sercheduler.localsearch.operator.NeighborhoodOperatorGlobal;
import com.uniovi.sercheduler.service.FitnessCalculatorSimple;

import java.util.ArrayList;
import java.util.List;

public class MaximumGradientStrategy {

    public SchedulePermutationSolution execute(SchedulingProblem problem, NeighborhoodOperatorGlobal neighborhoodOperator){

        //Generate an inicial random solution
        SchedulePermutationSolution actualSolution = problem.createSolution();

        //Evaluate this new created solution (this step is skipped in the pseudocode)
        FitnessCalculatorSimple fitnessCalculator = new FitnessCalculatorSimple(problem.getInstanceData());
        fitnessCalculator.calculateFitness(actualSolution);

        //Initialize the control variable, a variable for storing their neighbors and a
        boolean upgradeFound;
        List<GeneratedNeighbor> neighborsList;
        SchedulePermutationSolution bestNeighbor;
        LocalsearchEvaluator evaluator = new LocalsearchEvaluator(fitnessCalculator.getComputationMatrix(), fitnessCalculator.getNetworkMatrix(), problem.getInstanceData());

        do{
            upgradeFound = false;

            //Generate new neighbors
            neighborsList = neighborhoodOperator.execute(actualSolution);

            //Take the best one
            bestNeighbor = selectBestNeighbor(actualSolution, neighborsList, evaluator);

            //If there is an improvement, record it and update the best neighbor
            if(bestNeighbor.getFitnessInfo().fitness().get("makespan") > actualSolution.getFitnessInfo().fitness().get("makespan")){
                actualSolution = bestNeighbor;
                upgradeFound = true;
            }

        } while(upgradeFound);

        return actualSolution;

    }

    private SchedulePermutationSolution selectBestNeighbor(SchedulePermutationSolution originalSolution, List<GeneratedNeighbor> neighborsList, LocalsearchEvaluator evaluator) {

        SchedulePermutationSolution bestSolution = originalSolution;
        double bestMakespan = originalSolution.getFitnessInfo().fitness().get("makespan");
        double neighborMakespan;
        SchedulePermutationSolution neighborSolution;

        for(GeneratedNeighbor neighbor : neighborsList){

            neighborSolution = neighbor.generatedSolution();
            //TODO: check if getLast() works
            evaluator.calculateFitnessInfo(originalSolution, neighborSolution, neighbor.movements().getLast());
            neighborMakespan = neighborSolution.getFitnessInfo().fitness().get("makespan");

            if(bestMakespan > neighborMakespan){
                bestMakespan = neighborMakespan;
                bestSolution = neighborSolution;
            }

        }

        return bestSolution;
    }

}
