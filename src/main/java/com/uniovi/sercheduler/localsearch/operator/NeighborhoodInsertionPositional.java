package com.uniovi.sercheduler.localsearch.operator;

import com.uniovi.sercheduler.jmetal.problem.SchedulePermutationSolution;
import com.uniovi.sercheduler.localsearch.movement.InsertionMovement;
import com.uniovi.sercheduler.localsearch.movement.Movement;
import com.uniovi.sercheduler.service.PlanPair;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import static com.uniovi.sercheduler.localsearch.operator.NeighborUtils.getValidPositions;


public class NeighborhoodInsertionPositional implements NeighborhoodOperatorPositional {

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
                    insertInOneSpecificPosition(plan, position, newPosition),
                    actualSolution.getArbiter()
            );

            List<Movement> movements = new ArrayList<>();
            movements.add(
                    new InsertionMovement(changedPlanPairs, position, newPosition, NeighborUtils.getParentsPositions(plan, position))
            );
            neighbors.add(new GeneratedNeighbor(generatedSolution, movements));

        }

        return neighbors;

    }

    private List<PlanPair> insertInOneSpecificPosition(List<PlanPair> plan, int position, int newPosition){

        List<PlanPair> newPlan = new ArrayList<>(List.copyOf(plan));

        if (newPosition < position) {
            for (int i = position - 1; i >= newPosition && i >= 0; i--) {
                newPlan.set(i + 1, newPlan.get(i));
            }
        } else if (newPosition > position) {
            for (int i = position + 1; i <= newPosition; i++) {
                newPlan.set(i - 1, newPlan.get(i));
            }
        }

        newPlan.set(newPosition, plan.get(position));

        return List.copyOf(newPlan);
    }


}
