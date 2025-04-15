package com.uniovi.sercheduler.localsearch.evaluator;

import com.uniovi.sercheduler.dto.Host;
import com.uniovi.sercheduler.dto.InstanceData;
import com.uniovi.sercheduler.dto.Task;
import com.uniovi.sercheduler.jmetal.problem.SchedulePermutationSolution;
import com.uniovi.sercheduler.localsearch.movement.ChangeHostMovement;
import com.uniovi.sercheduler.localsearch.movement.InsertionMovement;
import com.uniovi.sercheduler.localsearch.movement.SwapHostMovement;
import com.uniovi.sercheduler.localsearch.movement.SwapMovement;
import com.uniovi.sercheduler.service.PlanPair;
import com.uniovi.sercheduler.service.TaskSchedule;

import java.util.*;
import java.util.stream.Collectors;

public class LocalsearchEvaluator {

    private Map<String, Map<String, Double>> computationMatrix;
    private Map<String, Map<String, Long>> networkMatrix;

    private InstanceData instanceData;

    public LocalsearchEvaluator(Map<String, Map<String, Double>> computationMatrix, Map<String, Map<String, Long>> networkMatrix, InstanceData instanceData) {
        this.computationMatrix = new HashMap<>(computationMatrix);
        this.networkMatrix = new HashMap<>(networkMatrix);
        this.instanceData = instanceData;
    }

    public double computeEnhancementChangeHost(SchedulePermutationSolution originalSolution, SchedulePermutationSolution generatedSolution, ChangeHostMovement changeHostMovement){
        return originalSolution.getFitnessInfo().fitness().get("makespan")
                - computeHostAssignationTimeEffects(originalSolution, changeHostMovement)
                + computeHostAssignationTimeEffects(generatedSolution, changeHostMovement);
    }

    private double computeHostAssignationTimeEffects(SchedulePermutationSolution solution, ChangeHostMovement hostMovement){

        return computeDurationOfATask(solution.getPlan(), hostMovement.getPosition(), hostMovement.getParentsPositions())
                + computeChildrenCommunicationsDuration(solution.getPlan(), hostMovement.getPosition(), hostMovement.getChildrenPositions());
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

    public double computeEnhancementSwapHost(SchedulePermutationSolution originalSolution, SchedulePermutationSolution generatedSolution, SwapHostMovement swapHostMovement) {
        return originalSolution.getFitnessInfo().fitness().get("makespan")
                - computeFirstHostAssignationTimeEffects(originalSolution, swapHostMovement)
                + computeFirstHostAssignationTimeEffects(generatedSolution, swapHostMovement)
                - computeSecondHostAssignationTimeEffects(originalSolution, swapHostMovement)
                + computeSecondHostAssignationTimeEffects(generatedSolution, swapHostMovement);
    }

    private double computeFirstHostAssignationTimeEffects(SchedulePermutationSolution solution, SwapHostMovement swapHostMovement){

        return computeDurationOfATask(solution.getPlan(), swapHostMovement.getFirstPosition(), swapHostMovement.getFirstParentsPositions())
                + computeChildrenCommunicationsDuration(solution.getPlan(), swapHostMovement.getFirstPosition(), swapHostMovement.getFirstChildrenPositions());
    }

    private double computeSecondHostAssignationTimeEffects(SchedulePermutationSolution solution, SwapHostMovement swapHostMovement){

        return computeDurationOfATask(solution.getPlan(), swapHostMovement.getSecondPosition(), swapHostMovement.getSecondParentsPositions())
                + computeChildrenCommunicationsDuration(solution.getPlan(), swapHostMovement.getSecondPosition(), swapHostMovement.getSecondChildrenPositions());
    }

    public double computeEnhancementSwap(SchedulePermutationSolution originalSolution, SchedulePermutationSolution generatedSolution, SwapMovement swapMovement) {

        return originalSolution.getFitnessInfo().fitness().get("makespan")
                - computeNewMakespan(originalSolution, generatedSolution,
                Math.min(swapMovement.getFirstPosition(), swapMovement.getSecondPosition()));
    }



    public double computeNewMakespan(SchedulePermutationSolution originalSolution, SchedulePermutationSolution generatedSolution, int firstChangePosition){

        double makespan = 0D;

        Map<String, Double> available = new HashMap<>(instanceData.hosts().size());
        List<TaskSchedule> originalOrderedSchedule = new ArrayList<>(originalSolution.getFitnessInfo().schedule());
        Map<String, TaskSchedule> originalSchedule = originalOrderedSchedule.stream()
                                                        .collect(Collectors.toMap(ts -> ts.task().getName(), ts -> ts));
        Map<String, TaskSchedule> newSchedule = new HashMap<>(instanceData.workflow().size());

        for(int i = 0; i < generatedSolution.getPlan().size(); i ++){

            Task t = generatedSolution.getPlan().get(i).task();
            Host h = generatedSolution.getPlan().get(i).host();

            double duration = originalSchedule.get(t.getName()).eft() - originalSchedule.get(t.getName()).ast();

            if(i >= firstChangePosition){

                double readyHost = available.getOrDefault(h.getName(), 0D);
                double parentsMaxEft = computeParentsMaxEft(newSchedule, t);

                double newAst = Math.max(readyHost, parentsMaxEft);
                double newEft = newAst + duration;

                available.put(h.getName(), newEft);
                newSchedule.put(t.getName(), new TaskSchedule(t, newAst, newEft, h));

                makespan = Math.max(makespan, newEft);

            } else {
                double originalEft = originalSchedule.get(t.getName()).eft();
                double originalAst = originalSchedule.get(t.getName()).ast();

                available.put(h.getName(), originalEft);
                newSchedule.put(t.getName(), new TaskSchedule(t, originalAst, originalEft, h));

                makespan = Math.max(makespan, originalEft);
            }

        }

        /*var newOrderedSchedule =
                newSchedule.values().stream().sorted(Comparator.comparing(TaskSchedule::ast)).toList();

        generatedSolution.setFitnessInfo(
                new FitnessInfo(Map.of("makespan", makespan), newOrderedSchedule, "localsearch")
        );*/

        return makespan;
    }

    private double computeParentsMaxEft(Map<String, TaskSchedule> newSchedule, Task task) {
        return newSchedule.values().stream()
                .filter(ts -> task.getParents().contains(ts.task()))
                .mapToDouble(TaskSchedule::eft)
                .max()
                .orElse(0D);
    }


    public double computeEnhancementInsertion(SchedulePermutationSolution originalSolution, SchedulePermutationSolution generatedSolution, InsertionMovement insertionMovement) {
        return originalSolution.getFitnessInfo().fitness().get("makespan")
                - computeNewMakespan(originalSolution, generatedSolution,
                Math.min(insertionMovement.getInitialPosition(), insertionMovement.getFinalPosition()));
    }
}
