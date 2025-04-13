package com.uniovi.sercheduler.localsearch.evaluator;

import com.uniovi.sercheduler.dto.Host;
import com.uniovi.sercheduler.dto.Task;
import com.uniovi.sercheduler.jmetal.problem.SchedulePermutationSolution;
import com.uniovi.sercheduler.localsearch.movement.ChangeHostMovement;
import com.uniovi.sercheduler.service.PlanPair;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LocalsearchEvaluator {

    private Map<String, Map<String, Double>> computationMatrix;
    private Map<String, Map<String, Long>> networkMatrix;

    public LocalsearchEvaluator(Map<String, Map<String, Double>> computationMatrix, Map<String, Map<String, Long>> networkMatrix) {
        this.computationMatrix = new HashMap<>(computationMatrix);
        this.networkMatrix = new HashMap<>(networkMatrix);
    }

    public double computeEnhancementChangeHost(SchedulePermutationSolution originalSolution, SchedulePermutationSolution generatedSolution, ChangeHostMovement movement){
        return originalSolution.getFitnessInfo().fitness().get("makespan")
                - computeHostAssignationTimeEffects(originalSolution, movement)
                + computeHostAssignationTimeEffects(generatedSolution, movement);
    }

    private double computeHostAssignationTimeEffects(SchedulePermutationSolution solution, ChangeHostMovement hostMovement){

        return computeDurationOfATask(solution.getPlan(), hostMovement.getFinalPosition(), hostMovement.getParentPositions())
                + computeChildrenCommunicationsDuration(solution.getPlan(), hostMovement.getFinalPosition(), hostMovement.getChildrenPositions());
    }

    private double computeDurationOfATask(List<PlanPair> plan, int position, int[] parentsPositions) {

        Task task = plan.get(position).task();
        Host host = plan.get(position).host();

        double diskReadStagingTime =
                networkMatrix.get(task.getName()).get(task.getName()) / host.getDiskSpeed().doubleValue();
        double taskCommunicationsTime = computeParentsCommunicationsDuration(plan, position, parentsPositions);
        double computationTime = computationMatrix.get(task.getName()).get(host.getName());
        double diskWriteTime = task.getOutput().getSizeInBits() / host.getDiskSpeed().doubleValue();

        return diskReadStagingTime + taskCommunicationsTime + computationTime + diskWriteTime;
    }

    private double computeChildrenCommunicationsDuration(List<PlanPair> plan, int parentPos, int[] childrenPositions)
    {

        double childrenCommunicationsDuration = 0D;

        for(int childPos = 0; childPos < childrenPositions.length; childPos++){

            double slowestSpeed = findHostSpeed(plan.get(childPos).host(), plan.get(parentPos).host());

            childrenCommunicationsDuration +=
                    networkMatrix.get(plan.get(childPos).task().getName()).get(plan.get(parentPos).task().getName()) / slowestSpeed;
        }

        return childrenCommunicationsDuration;

    }

    public double computeParentsCommunicationsDuration(List<PlanPair> plan, int childPos, int[] parentsPositions){

        double parentsCommunicationsDuration = 0D;

        for(int parentPos = 0; parentPos < parentsPositions.length; parentPos++){

            double slowestSpeed = findHostSpeed(plan.get(childPos).host(), plan.get(parentPos).host());

            parentsCommunicationsDuration +=
                    networkMatrix.get(plan.get(childPos).task().getName()).get(plan.get(parentPos).task().getName()) / slowestSpeed;
        }

        return parentsCommunicationsDuration;
    }

    public Long findHostSpeed(Host host, Host parentHost) {

        if (host.getName().equals(parentHost.getName())) {
            return host.getDiskSpeed();
        }

        var bandwidth = Math.min(host.getNetworkSpeed(), parentHost.getNetworkSpeed());

        return Math.min(bandwidth, parentHost.getDiskSpeed());
    }

}
