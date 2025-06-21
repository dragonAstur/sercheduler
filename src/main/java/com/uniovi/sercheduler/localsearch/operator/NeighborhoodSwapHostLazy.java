package com.uniovi.sercheduler.localsearch.operator;

import com.uniovi.sercheduler.jmetal.problem.SchedulePermutationSolution;
import com.uniovi.sercheduler.localsearch.movement.Movement;
import com.uniovi.sercheduler.localsearch.movement.SwapHostMovement;
import com.uniovi.sercheduler.service.PlanPair;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static com.uniovi.sercheduler.localsearch.operator.NeighborUtils.getValidPositions;

public class NeighborhoodSwapHostLazy implements NeighborhoodOperatorLazy{

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
                                        swapHostWithOneSpecificPosition(plan, position, otherPosition),
                                        actualSolution.getArbiter()
                                );

                                List<Movement> movements = new ArrayList<>();
                                movements.add(new SwapHostMovement(
                                        position,
                                        otherPosition,
                                        NeighborUtils.getParentsPositions(plan, position),
                                        NeighborUtils.getChildrenPositions(plan, position),
                                        NeighborUtils.getParentsPositions(plan, otherPosition),
                                        NeighborUtils.getChildrenPositions(plan, otherPosition)
                                ));

                                return new GeneratedNeighbor(generatedSolution, movements);
                            });
                });
    }

    @Override
    public String getName() {
        return "swap_host";
    }

    private List<PlanPair> swapHostWithOneSpecificPosition(List<PlanPair> plan, int position, int otherPosition)
    {
        List<PlanPair> newPlan = new ArrayList<>(List.copyOf(plan));

        //Lets (t1, h1) and (t2, h2) be two pairs, each on position and otherPosition respectively. We
        //want to create a new plan where (t1, h2) and (t2, h1) occupy position and otherPosition, again
        //respectively. So we create first this two pairs and then we put them where they belong.

        PlanPair firstPlanPair = new PlanPair(newPlan.get(position).task(), newPlan.get(otherPosition).host());
        PlanPair secondPlanPair = new PlanPair(newPlan.get(otherPosition).task(), newPlan.get(position).host());

        newPlan.set(position, firstPlanPair);
        newPlan.set(otherPosition, secondPlanPair);

        return List.copyOf(newPlan);

    }

}
