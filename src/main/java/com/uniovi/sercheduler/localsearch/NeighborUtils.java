package com.uniovi.sercheduler.localsearch;

import com.uniovi.sercheduler.jmetal.problem.SchedulePermutationSolution;
import com.uniovi.sercheduler.service.PlanPair;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

public class NeighborUtils {

    protected static List<SchedulePermutationSolution> createNeighborSolutions(SchedulePermutationSolution schedulePermutationSolution, List<List<PlanPair>> neighboursPlans) {

        List<SchedulePermutationSolution> neighbours = new ArrayList<>();

        for(List<PlanPair> plan : neighboursPlans)
        {
            neighbours.add(
                    new SchedulePermutationSolution(
                            schedulePermutationSolution.variables().size(),
                            schedulePermutationSolution.objectives().length,
                            null,
                            plan,
                            schedulePermutationSolution.getArbiter()
                    )
            );
        }

        return neighbours;
    }

    protected static int[] getValidPositions(List<PlanPair> plan, int position){

        int positionLeft = position - 1;
        boolean parentFound = false;
        while (positionLeft >= 0 && !parentFound) {
            parentFound = plan.get(position).task().getParents().contains(plan.get(positionLeft).task());
            positionLeft--;
        }

        int positionRight = position + 1;
        boolean childFound = false;

        while (positionRight < plan.size() - 1 && !childFound) {
            childFound = plan.get(position).task().getChildren().contains(plan.get(positionRight).task());
            positionRight++;
        }

        return IntStream.range(positionLeft+1, positionRight).toArray();
    }
}
