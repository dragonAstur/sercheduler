package com.uniovi.sercheduler.localsearch.operator;

import com.uniovi.sercheduler.jmetal.problem.SchedulePermutationSolution;
import com.uniovi.sercheduler.localsearch.movement.Movement;
import com.uniovi.sercheduler.localsearch.movement.SwapMovement;
import com.uniovi.sercheduler.service.PlanPair;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static com.uniovi.sercheduler.localsearch.operator.NeighborUtils.getValidPositions;

public class NeighborhoodSwapLazy implements NeighborhoodOperatorLazy{

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
                                        swapWithOneSpecificPosition(plan, position, otherPosition),
                                        actualSolution.getArbiter()
                                );

                                List<Movement> movements = new ArrayList<>();
                                movements.add(new SwapMovement(position, otherPosition, NeighborUtils.getParentsPositions(plan, position)));
                                return new GeneratedNeighbor(generatedSolution, movements);

                            });
                });
    }

    @Override
    public String getName() {
        return "swap";
    }

    private List<PlanPair> swapWithOneSpecificPosition(List<PlanPair> plan, int position, int newPosition)
    {
        List<PlanPair> newPlan = new ArrayList<>(List.copyOf(plan));

        Collections.swap(newPlan, position, newPosition);

        return List.copyOf(newPlan);

    }

}
