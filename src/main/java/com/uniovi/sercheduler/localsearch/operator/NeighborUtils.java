package com.uniovi.sercheduler.localsearch.operator;

import com.uniovi.sercheduler.dto.Task;
import com.uniovi.sercheduler.jmetal.problem.SchedulePermutationSolution;
import com.uniovi.sercheduler.service.PlanPair;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class NeighborUtils {

    protected static List<SchedulePermutationSolution> createNeighborSolutions(SchedulePermutationSolution schedulePermutationSolution, List<List<PlanPair>> neighboursPlans) {

        List<SchedulePermutationSolution> neighbours = new ArrayList<>();

        for(List<PlanPair> plan : neighboursPlans)
        {
            neighbours.add(
                    new SchedulePermutationSolution(
                            schedulePermutationSolution.variables().size(),
                            schedulePermutationSolution.objectives().length,
                            null,
                            plan,
                            schedulePermutationSolution.getArbiter()
                    )
            );
        }

        return neighbours;
    }

    protected static int[] getValidPositions(List<PlanPair> plan, int position){

        int positionLeft = position;
        boolean parentFound = false;
        while (positionLeft >= 0 && !parentFound) {
            positionLeft--;
            parentFound = plan.get(position).task().getParents().contains(plan.get(positionLeft).task());
        }

        int positionRight = position;
        boolean childFound = false;

        while (positionRight < plan.size() - 1 && !childFound) {
            positionRight++;
            childFound = plan.get(position).task().getChildren().contains(plan.get(positionRight).task());
        }

        return IntStream.range(positionLeft+1, positionRight).toArray();
    }

    public static int[] getChildrenPositions(List<PlanPair> plan, int position){

        Set<Task> children = new HashSet<>(plan.get(position).task().getChildren());

        return IntStream.range(0, plan.size())
                .filter(    pos -> children.contains(   plan.get(pos).task()    )
        ).toArray();

    }

    public static int[] getParentsPositions(List<PlanPair> plan, int position){

        Set<Task> parents = new HashSet<>(plan.get(position).task().getParents());

        return IntStream.range(0, plan.size())
                .filter(    pos -> parents.contains(   plan.get(pos).task()    )
                ).toArray();

    }
}
