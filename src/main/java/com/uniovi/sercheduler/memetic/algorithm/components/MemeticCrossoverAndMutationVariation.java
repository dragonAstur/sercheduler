package com.uniovi.sercheduler.memetic.algorithm.components;

import com.uniovi.sercheduler.memetic.observer.MemeticObserver;
import org.uma.jmetal.operator.crossover.CrossoverOperator;
import org.uma.jmetal.operator.mutation.MutationOperator;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.errorchecking.Check;
import org.uma.jmetal.util.errorchecking.JMetalException;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class MemeticCrossoverAndMutationVariation<S extends Solution<?>> implements MemeticVariation<S> {
    private CrossoverOperator<S> crossover;
    private MutationOperator<S> mutation;
    private int matingPoolSize;
    private int offspringPopulationSize;

    public MemeticCrossoverAndMutationVariation(int offspringPopulationSize, CrossoverOperator<S> crossover, MutationOperator<S> mutation) {
        this.crossover = crossover;
        this.mutation = mutation;
        this.offspringPopulationSize = offspringPopulationSize;
        this.matingPoolSize = offspringPopulationSize * crossover.numberOfRequiredParents() / crossover.numberOfGeneratedChildren();
        int remainder = this.matingPoolSize % crossover.numberOfRequiredParents();
        if (remainder != 0) {
            this.matingPoolSize += remainder;
        }

    }

    public List<S> variate(List<S> population, List<S> matingPopulation, MemeticObserver observer) {
        int numberOfParents = this.crossover.numberOfRequiredParents();
        this.checkNumberOfParents(matingPopulation, numberOfParents);
        List<S> offspringPopulation = new ArrayList(this.offspringPopulationSize);

        for(int i = 0; i < this.matingPoolSize; i += numberOfParents) {
            List<S> parents = new ArrayList(numberOfParents);

            for(int j = 0; j < numberOfParents; ++j) {
                parents.add(matingPopulation.get(i + j));
            }

            List<S> offspring = crossover.execute(parents);

            for (S s : offspring) {
                mutation.execute(s);
                offspringPopulation.add(s);
                if (offspringPopulation.size() == offspringPopulationSize) {
                    break;
                }
            }

            observer.updateMemeticEvolution(
                    observer.lastRecordedMakespan(),
                    0,
                    0
            );
        }

        boolean var10000 = offspringPopulation.size() == this.offspringPopulationSize;
        int var10001 = offspringPopulation.size();
        Check.that(var10000, "The size of theoffspring population is not correct: " + var10001 + " instead of " + this.offspringPopulationSize);
        return offspringPopulation;
    }

    private void checkNumberOfParents(List<S> population, int numberOfParentsForCrossover) {
        if (population.size() % numberOfParentsForCrossover != 0) {
            int var10002 = population.size();
            throw new JMetalException("Wrong number of parents: the remainder if the population size (" + var10002 + ") is not divisible by " + numberOfParentsForCrossover);
        }
    }

    public int getMatingPoolSize() {
        return this.matingPoolSize;
    }

    public int getOffspringPopulationSize() {
        return this.offspringPopulationSize;
    }
}