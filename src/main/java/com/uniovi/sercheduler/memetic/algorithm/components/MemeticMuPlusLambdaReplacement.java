package com.uniovi.sercheduler.memetic.algorithm.components;

import org.uma.jmetal.component.catalogue.ea.replacement.Replacement;
import org.uma.jmetal.solution.Solution;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class MemeticMuPlusLambdaReplacement<S extends Solution<?>> implements Replacement<S> {
    protected Comparator<S> comparator;

    public MemeticMuPlusLambdaReplacement(Comparator<S> comparator) {
        this.comparator = comparator;
    }

    public List<S> replace(List<S> population, List<S> offspringPopulation) {
        List<S> jointPopulation = new ArrayList();
        jointPopulation.addAll(population);
        jointPopulation.addAll(offspringPopulation);
        jointPopulation.sort(this.comparator);

        while(jointPopulation.size() > population.size()) {
            jointPopulation.remove(jointPopulation.size() - 1);
        }

        return jointPopulation;
    }
}
