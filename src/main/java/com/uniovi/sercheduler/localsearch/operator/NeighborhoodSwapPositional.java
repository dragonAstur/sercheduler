package com.uniovi.sercheduler.localsearch.operator;

import com.uniovi.sercheduler.jmetal.problem.SchedulePermutationSolution;
import com.uniovi.sercheduler.localsearch.movement.Movement;
import com.uniovi.sercheduler.localsearch.movement.SwapMovement;
import com.uniovi.sercheduler.service.PlanPair;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.uniovi.sercheduler.localsearch.operator.NeighborUtils.getValidPositions;

public class NeighborhoodSwapPositional implements NeighborhoodOperatorPositional {

    @Override
    public List<GeneratedNeighbor> execute(SchedulePermutationSolution actualSolution, int position) {

        List<PlanPair> plan = List.copyOf(actualSolution.getPlan());

        int[] validPositions = getValidPositions(plan, position);

        List<GeneratedNeighbor> neighbors = new ArrayList<>();

        for(int newPosition : validPositions) {

            if(newPosition == position)
                continue;

            SchedulePermutationSolution generatedSolution = new SchedulePermutationSolution(
                    actualSolution.variables().size(),
                    actualSolution.objectives().length,
                    null,
                    swapWithOneSpecificPosition(plan, position, newPosition),
                    actualSolution.getArbiter()
            );

            List<Movement> movements = new ArrayList<>();
            movements.add(new SwapMovement(position, newPosition, NeighborUtils.getParentsPositions(plan, position)));
            neighbors.add(new GeneratedNeighbor(generatedSolution, movements));

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
