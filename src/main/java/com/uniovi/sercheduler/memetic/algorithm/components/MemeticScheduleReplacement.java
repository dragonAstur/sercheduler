package com.uniovi.sercheduler.memetic.algorithm.components;

import com.uniovi.sercheduler.dao.Objective;
import com.uniovi.sercheduler.jmetal.problem.SchedulePermutationSolution;
import com.uniovi.sercheduler.memetic.observer.MemeticObserver;
import org.uma.jmetal.component.catalogue.ea.replacement.Replacement;

import java.util.*;
import java.util.stream.Collectors;

public class MemeticScheduleReplacement implements MemeticReplacement<SchedulePermutationSolution> {

    Random random;
    Objective objective;

    public MemeticScheduleReplacement(Objective objective) {
        this(new Random(), objective);
    }

    public MemeticScheduleReplacement(Random random, Objective objective) {
        this.random = random;
        this.objective = objective;
    }

    /**
     * Executes the replacement. Uses a tournament 4:2.
     *
     * @param parents The list of parents.
     * @param children The list of children.
     * @return The new pool of solution.
     */
    @Override
    public List<SchedulePermutationSolution> replace(
            List<SchedulePermutationSolution> parents, List<SchedulePermutationSolution> children, MemeticObserver observer) {
        var replacement = new ArrayList<SchedulePermutationSolution>();
        for (int i = 0; i < parents.size(); i = i + 2) {
            var parent1 = parents.get(i);
            var parent2 = parents.get(i + 1);
            var child1 = children.get(i);
            var child2 = children.get(i + 1);
            var tournament = List.of(parent1, parent2, child1, child2);
            var result =
                    tournament.stream()
                            .sorted(
                                    Comparator.comparing(
                                            s -> s.getFitnessInfo().fitness().get(objective.objectiveName)))
                            .collect(
                                    Collectors.collectingAndThen(
                                            Collectors.toCollection(
                                                    () ->
                                                            new TreeSet<>(
                                                                    Comparator.comparing(
                                                                            s ->
                                                                                    s.getFitnessInfo()
                                                                                            .fitness()
                                                                                            .get(objective.objectiveName)))),
                                            ArrayList::new));

            if (result.size() == 1) {
                result.add(result.get(0));
            }
            replacement.addAll(result.subList(0, 2));
        }

        return replacement;
    }
}
