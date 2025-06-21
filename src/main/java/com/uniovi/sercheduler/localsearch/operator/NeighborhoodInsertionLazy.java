package com.uniovi.sercheduler.localsearch.operator;

import com.uniovi.sercheduler.jmetal.problem.SchedulePermutationSolution;
import com.uniovi.sercheduler.localsearch.movement.InsertionMovement;
import com.uniovi.sercheduler.localsearch.movement.Movement;
import com.uniovi.sercheduler.service.PlanPair;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static com.uniovi.sercheduler.localsearch.operator.NeighborUtils.getValidPositions;

public class NeighborhoodInsertionLazy implements NeighborhoodOperatorLazy{

    @Override
    public Stream<GeneratedNeighbor> execute(SchedulePermutationSolution actualSolution) {

        List<PlanPair> plan = List.copyOf(actualSolution.getPlan());

        return IntStream.range(0, plan.size())
                .boxed()
                .flatMap(position -> {

                    int[] validPositions = getValidPositions(plan, position);

                    return Arrays.stream(validPositions)
                            .filter(otherPosition -> otherPosition != position)
                            .mapToObj(otherPosition -> {
                                SchedulePermutationSolution generatedSolution = new SchedulePermutationSolution(
                                        actualSolution.variables().size(),
                                        actualSolution.objectives().length,
                                        null,
                                        insertInOneSpecificPosition(plan, position, otherPosition),
                                        actualSolution.getArbiter()
                                );

                                int[] changedPlanPairs = position < otherPosition ?
                                        IntStream.rangeClosed(position, otherPosition).toArray() :
                                        IntStream.rangeClosed(otherPosition, position).toArray();

                                List<Movement> movements = new ArrayList<>();
                                movements.add(
                                        new InsertionMovement(changedPlanPairs, position, otherPosition, NeighborUtils.getParentsPositions(plan, position))
                                );

                                return new GeneratedNeighbor(generatedSolution, movements);
                            });
                });
    }

    @Override
    public String getName() {
        return "insertion";
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
