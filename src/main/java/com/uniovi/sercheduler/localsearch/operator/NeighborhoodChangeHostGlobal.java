package com.uniovi.sercheduler.localsearch.operator;

import com.uniovi.sercheduler.dto.Host;
import com.uniovi.sercheduler.dto.InstanceData;
import com.uniovi.sercheduler.jmetal.problem.SchedulePermutationSolution;
import com.uniovi.sercheduler.localsearch.movement.ChangeHostMovement;
import com.uniovi.sercheduler.localsearch.movement.Movement;
import com.uniovi.sercheduler.service.PlanPair;

import java.util.ArrayList;
import java.util.List;

public class NeighborhoodChangeHostGlobal implements NeighborhoodOperatorGlobal {

    private final NeighborhoodChangeHostPositional changeHostPositional;

    public NeighborhoodChangeHostGlobal(InstanceData instanceData) {
        this.changeHostPositional = new NeighborhoodChangeHostPositional(instanceData);
    }

    @Override
    public List<GeneratedNeighbor> execute(SchedulePermutationSolution actualSolution) {

        List<PlanPair> plan = List.copyOf(actualSolution.getPlan());

        List<GeneratedNeighbor> neighbors = new ArrayList<>();

        for(int i = 0; i < plan.size(); i++) {

            neighbors.addAll(
                    changeHostPositional.execute(actualSolution, i)
            );

        }

        return neighbors;
    }

    @Override
    public String getName() {
        return "change_host";
    }
}
