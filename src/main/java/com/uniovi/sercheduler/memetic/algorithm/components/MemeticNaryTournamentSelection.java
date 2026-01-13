package com.uniovi.sercheduler.memetic.algorithm.components;

import com.uniovi.sercheduler.memetic.observer.MemeticObserver;
import org.uma.jmetal.component.catalogue.ea.selection.Selection;
import org.uma.jmetal.component.util.RankingAndDensityEstimatorPreference;
import org.uma.jmetal.solution.Solution;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class MemeticNaryTournamentSelection<S extends Solution<?>> implements MemeticSelection<S> {
    private final org.uma.jmetal.operator.selection.impl.NaryTournamentSelection<S> selectionOperator;
    private final int matingPoolSize;
    private RankingAndDensityEstimatorPreference<S> preference;

    public MemeticNaryTournamentSelection(org.uma.jmetal.operator.selection.impl.NaryTournamentSelection<S> selection, int matingPoolSize) {
        this.matingPoolSize = matingPoolSize;
        this.selectionOperator = selection;
    }

    public MemeticNaryTournamentSelection(int tournamentSize, int matingPoolSize, Comparator<S> comparator) {
        this.selectionOperator = new org.uma.jmetal.operator.selection.impl.NaryTournamentSelection<>(tournamentSize, comparator);
        this.matingPoolSize = matingPoolSize;
        this.preference = null;
    }

    public MemeticNaryTournamentSelection(int tournamentSize, int matingPoolSize, RankingAndDensityEstimatorPreference<S> preference) {
        this.preference = preference;
        this.selectionOperator = new org.uma.jmetal.operator.selection.impl.NaryTournamentSelection<>(tournamentSize, preference.getComparator());
        this.matingPoolSize = matingPoolSize;
    }

    public List<S> select(List<S> solutionList, MemeticObserver observer) {
        if (null != this.preference) {
            this.preference.recompute(solutionList);
        }

        List<S> matingPool = new ArrayList<>(this.matingPoolSize);

        while(matingPool.size() < this.matingPoolSize) {
            matingPool.add(this.selectionOperator.execute(solutionList));
        }

        return matingPool;
    }
}