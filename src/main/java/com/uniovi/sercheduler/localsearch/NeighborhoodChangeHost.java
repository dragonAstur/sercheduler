package com.uniovi.sercheduler.localsearch;

import com.uniovi.sercheduler.dto.Host;
import com.uniovi.sercheduler.dto.InstanceData;
import com.uniovi.sercheduler.jmetal.problem.SchedulePermutationSolution;
import com.uniovi.sercheduler.service.PlanPair;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import static com.uniovi.sercheduler.localsearch.NeighborUtils.createNeighborSolutions;
import static com.uniovi.sercheduler.localsearch.NeighborUtils.getValidPositions;

public class NeighborhoodChangeHost implements NeighborhoodOperator<SchedulePermutationSolution, List<GeneratedNeighbor>>{

    private final InstanceData instanceData;

    public NeighborhoodChangeHost(InstanceData instanceData) {
        this.instanceData = instanceData;
    }

    @Override
    public List<GeneratedNeighbor> execute(SchedulePermutationSolution actualSolution) {

        List<PlanPair> plan = List.copyOf(actualSolution.getPlan());

        List<GeneratedNeighbor> neighbors = new ArrayList<>();

        for(int i = 0; i < plan.size(); i++) {

            int[] changedPlanPairs = new int[]{i};

            changeOneElementHost(plan, i).stream().map(neighborPlan ->
                    new SchedulePermutationSolution(
                        actualSolution.variables().size(),
                        actualSolution.objectives().length,
                        null,
                        neighborPlan,
                        actualSolution.getArbiter()
                    )
            ).map(generatedSolution ->
                    new GeneratedNeighbor(generatedSolution, changedPlanPairs, -1, -1)
            ).forEach(neighbors::add);

        }

        return neighbors;
    }

    private List<List<PlanPair>> changeOneElementHost(List<PlanPair> plan, int position) {

        List<List<PlanPair>> neighbors = new ArrayList<>();

        for(var h : instanceData.hosts().values())
        {
            changeForOneSpecificHost(plan, position, h);
        }

        return neighbors;
    }

    private List<PlanPair> changeForOneSpecificHost(List<PlanPair> plan, int position, Host h) {
        List<PlanPair> newPlan = new ArrayList<>(List.copyOf(plan));
        newPlan.set(position, new PlanPair(plan.get(position).task(), h));
        return List.copyOf(newPlan);
    }

    public List<List<PlanPair>> changehostToAllElements(List<PlanPair> plan) {

        List<List<PlanPair>> neighbors = new ArrayList<>();

        for(int i = 0; i < plan.size(); i++)
            changeOneElementHost(plan, i);

        return neighbors;
    }
}
