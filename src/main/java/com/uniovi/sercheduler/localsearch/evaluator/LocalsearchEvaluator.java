package com.uniovi.sercheduler.localsearch.evaluator;

import com.uniovi.sercheduler.dto.Host;
import com.uniovi.sercheduler.dto.InstanceData;
import com.uniovi.sercheduler.dto.Task;
import com.uniovi.sercheduler.jmetal.problem.SchedulePermutationSolution;
import com.uniovi.sercheduler.localsearch.movement.*;
import com.uniovi.sercheduler.localsearch.operator.NeighborUtils;
import com.uniovi.sercheduler.service.FitnessInfo;
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

    public void evaluate(SchedulePermutationSolution originalSolution, SchedulePermutationSolution generatedSolution, Movement movement){

        if(originalSolution.getFitnessInfo() == null)
            throw new IllegalArgumentException("The solution must have been evaluated first.");

        Map<String, TaskSchedule> originalSchedule = obtainOriginalSchedule(originalSolution);

        if(movement.changedHostPositions().length != 0)
            updateOriginalScheduleDurations(originalSchedule, generatedSolution.getPlan(), movement.changedHostPositions());

        generatedSolution.setFitnessInfo(
                computeNewFitnessInfo(originalSchedule, generatedSolution.getPlan(), movement.getFirstChangePosition())
        );
    }


    public double computeMakespanEnhancement(SchedulePermutationSolution originalSolution, SchedulePermutationSolution generatedSolution, Movement movement){

        if(originalSolution.getFitnessInfo() == null)
            throw new IllegalArgumentException("The solution must have been evaluated first.");

        Map<String, TaskSchedule> originalSchedule = obtainOriginalSchedule(originalSolution);

        if(movement.changedHostPositions().length != 0)
            updateOriginalScheduleDurations(originalSchedule, generatedSolution.getPlan(), movement.changedHostPositions());

        return originalSolution.getFitnessInfo().fitness().get("makespan")
                - computeNewFitnessInfo(originalSchedule, generatedSolution.getPlan(), movement.getFirstChangePosition()).fitness().get("makespan");
    }

    private void updateOriginalScheduleDurations(Map<String, TaskSchedule> originalSchedule, List<PlanPair> plan, int[] changedHostPositions) {

        for(int taskPos : changedHostPositions){

            Task task = plan.get(taskPos).task();

            int[] taskParentsPos = NeighborUtils.getParentsPositions(plan, taskPos);

            double newEft = originalSchedule.get(task.getName()).ast()
                    + computeDurationOfATask(plan, taskPos, taskParentsPos);

            TaskSchedule originalTaskSchedule = originalSchedule.get(task.getName());

            originalSchedule.put(task.getName(),
                new TaskSchedule(originalTaskSchedule.task(), originalTaskSchedule.ast(), newEft, originalTaskSchedule.host()));

            //Updating children communications with this task
            for(int childPos : NeighborUtils.getChildrenPositions(plan, taskPos)){

                Task childTask = plan.get(childPos).task();

                TaskSchedule originalChildTaskSchedule = originalSchedule.get(childTask.getName());

                double oldCommunicationTime = computeCommunicationTime(originalChildTaskSchedule.task(), originalChildTaskSchedule.host(),
                        originalTaskSchedule.task(), originalTaskSchedule.host());

                double newCommunicationTime = computeCommunicationTime(plan.get(childPos).task(), plan.get(childPos).host(),
                        plan.get(taskPos).task(), plan.get(taskPos).host());


                //----------------------- Changed lines -----------------------
//                double newChildDuration = computeDurationOfATask(plan, childPos, NeighborUtils.getParentsPositions(plan, childPos)) - oldCommunicationTime + newCommunicationTime;
//
//                double newChildEft = originalChildTaskSchedule.ast() + newChildDuration;

                //------------------------ Changed lines ---------------------

                double newChildEft = originalChildTaskSchedule.eft() - oldCommunicationTime + newCommunicationTime;

                originalSchedule.put(childTask.getName(),
                        new TaskSchedule(originalChildTaskSchedule.task(), originalChildTaskSchedule.ast(), newChildEft, originalChildTaskSchedule.host()));
            }
        }
    }

    private double computeCommunicationTime(Task childTask, Host childHost, Task parentTask, Host parentHost){

        double slowestSpeed = findHostSpeed(childHost, parentHost);

        return networkMatrix.get(childTask.getName()).get(parentTask.getName()) / slowestSpeed;

    }


    private static Map<String, TaskSchedule> obtainOriginalSchedule(SchedulePermutationSolution originalSolution) {
        List<TaskSchedule> originalOrderedSchedule = new ArrayList<>(originalSolution.getFitnessInfo().schedule());
        return originalOrderedSchedule.stream().collect(Collectors.toMap(ts -> ts.task().getName(), ts -> ts));
    }

    private FitnessInfo computeNewFitnessInfo(Map<String, TaskSchedule> originalSchedule, List<PlanPair> newPlan, int firstChangePosition){

        double newMakespan = 0D;

        Map<String, Double> available = new HashMap<>(instanceData.hosts().size());
        Map<String, TaskSchedule> newSchedule = new HashMap<>(instanceData.workflow().size());

        for(int i = 0; i < newPlan.size(); i ++){

            Task t = newPlan.get(i).task();
            Host h = newPlan.get(i).host();

            double duration = originalSchedule.get(t.getName()).eft() - originalSchedule.get(t.getName()).ast();

            if(i >= firstChangePosition){

                double readyHost = available.getOrDefault(h.getName(), 0D);
                double parentsMaxEft = computeParentsMaxEft(newSchedule, t);

                double newAst = Math.max(readyHost, parentsMaxEft);
                double newEft = newAst + duration;

                available.put(h.getName(), newEft);
                newSchedule.put(t.getName(), new TaskSchedule(t, newAst, newEft, h));

                newMakespan = Math.max(newMakespan, newEft);

            } else {
                double originalEft = originalSchedule.get(t.getName()).eft();
                double originalAst = originalSchedule.get(t.getName()).ast();

                available.put(h.getName(), originalEft);
                newSchedule.put(t.getName(), new TaskSchedule(t, originalAst, originalEft, h));

                newMakespan = Math.max(newMakespan, originalEft);
            }

        }

        var newOrderedSchedule =
                newSchedule.values().stream().sorted(Comparator.comparing(TaskSchedule::ast)).toList();

        return new FitnessInfo(
                Map.of("makespan", newMakespan, "energy", 0.0), newOrderedSchedule, "incremental evaluator");
    }

    private double computeParentsMaxEft(Map<String, TaskSchedule> newSchedule, Task task) {
        return newSchedule.values().stream()
                .filter(ts -> task.getParents().contains(ts.task()))
                .mapToDouble(TaskSchedule::eft)
                .max()
                .orElse(0D);
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

    public double computeParentsCommunicationsDuration(List<PlanPair> plan, int position, int[] parentsPositions){

        double parentsCommunicationsDuration = 0D;

        for (int parentsPosition : parentsPositions) {

            var slowestSpeed = findHostSpeed(plan.get(position).host(), plan.get(parentsPosition).host());

            parentsCommunicationsDuration +=
                    networkMatrix.get(plan.get(position).task().getName()).get(plan.get(parentsPosition).task().getName()) / slowestSpeed.doubleValue();
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
