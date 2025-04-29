package com.uniovi.sercheduler.localsearch.strategy;

import com.uniovi.sercheduler.jmetal.problem.SchedulePermutationSolution;
import com.uniovi.sercheduler.jmetal.problem.SchedulingProblem;
import com.uniovi.sercheduler.localsearch.evaluator.LocalsearchEvaluator;
import com.uniovi.sercheduler.localsearch.operator.GeneratedNeighbor;
import com.uniovi.sercheduler.localsearch.operator.NeighborhoodOperatorGlobal;
import com.uniovi.sercheduler.localsearch.operator.NeighborhoodOperatorLazy;
import com.uniovi.sercheduler.service.FitnessCalculatorSimple;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public class SimpleClimbingStrategy {

    public SchedulePermutationSolution execute(SchedulingProblem problem, NeighborhoodOperatorLazy neighborhoodOperator){

        //Generate an inicial random solution
        SchedulePermutationSolution actualSolution = problem.createSolution();

        //Evaluate this new created solution (this step is skipped in the pseudocode)
        FitnessCalculatorSimple fitnessCalculator = new FitnessCalculatorSimple(problem.getInstanceData());
        fitnessCalculator.calculateFitness(actualSolution);

        //Initialize the control variable, a variable for storing their neighbors and a
        boolean upgradeFound;
        Stream<GeneratedNeighbor> neighbors;
        Optional<GeneratedNeighbor> maybeBetterNeighbor;
        LocalsearchEvaluator evaluator = new LocalsearchEvaluator(fitnessCalculator.getComputationMatrix(), fitnessCalculator.getNetworkMatrix(), problem.getInstanceData());


        do{
            upgradeFound = false;

            //Lazy computation of all the neighbors
            neighbors = neighborhoodOperator.execute(actualSolution);

            final SchedulePermutationSolution finalActualSolution = actualSolution; //This is just for functional programming technical problems

            //Find the first neighbor that is better than the source
            maybeBetterNeighbor = neighbors
                    .filter(neighbor -> {
                        evaluator.evaluate(finalActualSolution, neighbor.generatedSolution(), neighbor.movements().get(neighbor.movements().size() - 1));

                        return neighbor.generatedSolution().getFitnessInfo().fitness().get("makespan") <
                                finalActualSolution.getFitnessInfo().fitness().get("makespan");
                    })
                    .findFirst();

            //If there is an improvement, record it and update the best neighbor
            if (maybeBetterNeighbor.isPresent()) {
                actualSolution = maybeBetterNeighbor.get().generatedSolution();
                upgradeFound = true;
            }

        } while(upgradeFound);

        return actualSolution;

    }

}
