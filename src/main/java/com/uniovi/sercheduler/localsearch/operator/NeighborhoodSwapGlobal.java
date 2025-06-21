package com.uniovi.sercheduler.localsearch.operator;

import com.uniovi.sercheduler.jmetal.problem.SchedulePermutationSolution;
import com.uniovi.sercheduler.service.PlanPair;

import java.util.ArrayList;
import java.util.List;

public class NeighborhoodSwapGlobal implements NeighborhoodOperatorGlobal {

    private NeighborhoodSwapPositional swapPositional;

    public NeighborhoodSwapGlobal(){
        this.swapPositional = new NeighborhoodSwapPositional();
    }

    @Override
    public List<GeneratedNeighbor> execute(SchedulePermutationSolution actualSolution) {
        List<PlanPair> plan = List.copyOf(actualSolution.getPlan());

        List<GeneratedNeighbor> neighbors = new ArrayList<>();

        for(int i = 0; i < plan.size(); i++) {

            neighbors.addAll(
                    swapPositional.execute(actualSolution, i)
            );

        }

        return neighbors;
    }

    @Override
    public String getName() {
        return "swap";
    }
}
