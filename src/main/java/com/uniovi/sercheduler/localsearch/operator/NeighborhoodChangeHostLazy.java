package com.uniovi.sercheduler.localsearch.operator;

import com.uniovi.sercheduler.dto.Host;
import com.uniovi.sercheduler.dto.InstanceData;
import com.uniovi.sercheduler.jmetal.problem.SchedulePermutationSolution;
import com.uniovi.sercheduler.localsearch.movement.ChangeHostMovement;
import com.uniovi.sercheduler.localsearch.movement.Movement;
import com.uniovi.sercheduler.service.PlanPair;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class NeighborhoodChangeHostLazy implements NeighborhoodOperatorLazy {

    private final InstanceData instanceData;

    public NeighborhoodChangeHostLazy(InstanceData instanceData) {
        this.instanceData = instanceData;
    }

    public Stream<GeneratedNeighbor> execute(SchedulePermutationSolution actualSolution) {

        List<PlanPair> plan = List.copyOf(actualSolution.getPlan());

        return IntStream.range(0, plan.size())
                .boxed()
                .flatMap(position ->
                    changeOneElementHost(plan, position).stream()
                            .map(neighborPlan ->
                                new SchedulePermutationSolution(
                                        actualSolution.variables().size(),
                                        actualSolution.objectives().length,
                                        null,
                                        neighborPlan,
                                        actualSolution.getArbiter()
                                ))
                            .map(generatedSolution ->{

                                List<Movement> movements = new ArrayList<>();

                                movements.add(new ChangeHostMovement(position,
                                        NeighborUtils.getParentsPositions(plan, position),
                                        NeighborUtils.getChildrenPositions(plan, position)));

                                return new GeneratedNeighbor(generatedSolution, movements);
                            })
                );
    }

    @Override
    public String getName() {
        return "change_host";
    }

    private List<List<PlanPair>> changeOneElementHost(List<PlanPair> plan, int position) {

        List<List<PlanPair>> neighbors = new ArrayList<>();

        for(var h : instanceData.hosts().values())
        {
            if(plan.get(position).host().equals(h))
                continue;
            neighbors.add(changeForOneSpecificHost(plan, position, h));
        }

        return neighbors;
    }

    private List<PlanPair> changeForOneSpecificHost(List<PlanPair> plan, int position, Host h) {
        List<PlanPair> newPlan = new ArrayList<>(List.copyOf(plan));
        newPlan.set(position, new PlanPair(plan.get(position).task(), h));
        return List.copyOf(newPlan);
    }

}
