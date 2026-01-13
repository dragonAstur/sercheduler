package com.uniovi.sercheduler.memetic.algorithm.components;

import com.uniovi.sercheduler.jmetal.problem.SchedulePermutationSolution;
import com.uniovi.sercheduler.memetic.observer.MemeticObserver;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class MemeticScheduleSelection implements MemeticSelection<SchedulePermutationSolution> {
    Random random;

    public MemeticScheduleSelection() {
        this(new Random());
    }

    public MemeticScheduleSelection(Random random) {
        this.random = random;
    }

    /**
     * Select the solutions to be mutated.
     *
     * @param list The parents to select.
     * @return The same list but shuffled.
     */
    @Override
    public List<SchedulePermutationSolution> select(List<SchedulePermutationSolution> list, MemeticObserver observer) {
        var listToShuffle = new ArrayList<>(list);
        Collections.shuffle(listToShuffle, random);
        return listToShuffle;
    }
}
