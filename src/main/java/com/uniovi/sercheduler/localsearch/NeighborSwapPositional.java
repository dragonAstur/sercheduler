package com.uniovi.sercheduler.localsearch;

import com.uniovi.sercheduler.jmetal.problem.SchedulePermutationSolution;
import com.uniovi.sercheduler.service.PlanPair;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.IntStream;

import static com.uniovi.sercheduler.localsearch.NeighborUtils.createNeighborSolutions;
import static com.uniovi.sercheduler.localsearch.NeighborUtils.getValidPositions;

public class NeighborSwapPositional implements NeighborhoodOperatorPositional<SchedulePermutationSolution, List<GeneratedNeighbor>> {

    @Override
    public List<GeneratedNeighbor> execute(SchedulePermutationSolution actualSolution, int position) {

        List<PlanPair> plan = List.copyOf(actualSolution.getPlan());

        int[] validPositions = getValidPositions(plan, position);

        List<GeneratedNeighbor> neighbors = new ArrayList<>();

        for(int newPosition : validPositions) {

            if(newPosition == position)
                continue;

            int[] changedPlanPairs = position < newPosition ?
                    IntStream.rangeClosed(position, newPosition).toArray() :
                    IntStream.rangeClosed(newPosition, position).toArray();

            SchedulePermutationSolution generatedSolution = new SchedulePermutationSolution(
                    actualSolution.variables().size(),
                    actualSolution.objectives().length,
                    null,
                    swapWithOneSpecificPosition(plan, position, newPosition),
                    actualSolution.getArbiter()
            );

            neighbors.add(new GeneratedNeighbor(generatedSolution, changedPlanPairs, position, newPosition));

        }

        return neighbors;
    }

    private List<PlanPair> swapWithOneSpecificPosition(List<PlanPair> plan, int position, int newPosition)
    {
        List<PlanPair> newPlan = new ArrayList<>(List.copyOf(plan));

        Collections.swap(newPlan, position, newPosition);

        return List.copyOf(newPlan);

    }


}
