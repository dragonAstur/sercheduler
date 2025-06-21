package com.uniovi.sercheduler.localsearch.operator;

import com.uniovi.sercheduler.jmetal.problem.SchedulePermutationSolution;
import com.uniovi.sercheduler.service.PlanPair;

import java.util.ArrayList;
import java.util.List;

public class NeighborhoodInsertionGlobal implements NeighborhoodOperatorGlobal {

    private NeighborhoodInsertionPositional insertionPositional;

    public NeighborhoodInsertionGlobal(){
        this.insertionPositional = new NeighborhoodInsertionPositional();
    }

    @Override
    public List<GeneratedNeighbor> execute(SchedulePermutationSolution actualSolution) {
        List<PlanPair> plan = List.copyOf(actualSolution.getPlan());

        List<GeneratedNeighbor> neighbors = new ArrayList<>();

        for(int i = 0; i < plan.size(); i++) {

            neighbors.addAll(
                    insertionPositional.execute(actualSolution, i)
            );

        }

        return neighbors;
    }

    @Override
    public String getName() {
        return "insertion";
    }
}
