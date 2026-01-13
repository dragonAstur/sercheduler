package com.uniovi.sercheduler.memetic.algorithm;

import com.uniovi.sercheduler.memetic.algorithm.components.*;
import com.uniovi.sercheduler.memetic.observer.MemeticObserver;
import org.uma.jmetal.component.catalogue.common.evaluation.Evaluation;
import org.uma.jmetal.component.catalogue.common.evaluation.impl.SequentialEvaluation;
import org.uma.jmetal.component.catalogue.common.solutionscreation.SolutionsCreation;
import org.uma.jmetal.component.catalogue.common.solutionscreation.impl.RandomSolutionsCreation;
import org.uma.jmetal.component.catalogue.common.termination.Termination;
import org.uma.jmetal.component.catalogue.common.termination.impl.TerminationByEvaluations;
import org.uma.jmetal.component.catalogue.ea.replacement.Replacement;
import org.uma.jmetal.component.catalogue.ea.replacement.impl.MuPlusLambdaReplacement;
import org.uma.jmetal.component.catalogue.ea.selection.Selection;
import org.uma.jmetal.component.catalogue.ea.selection.impl.NaryTournamentSelection;
import org.uma.jmetal.component.catalogue.ea.variation.Variation;
import org.uma.jmetal.component.catalogue.ea.variation.impl.CrossoverAndMutationVariation;
import org.uma.jmetal.operator.crossover.CrossoverOperator;
import org.uma.jmetal.operator.mutation.MutationOperator;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.comparator.ObjectiveComparator;

public class GeneticAlgorithmBuilder<S extends Solution<?>> {
    private String name;
    private MemeticEvaluation<S> evaluation;
    private MemeticSolutionsCreation<S> createInitialPopulation;
    private MemeticTermination termination;
    private MemeticSelection<S> selection;
    private MemeticVariation<S> variation;
    private MemeticReplacement<S> replacement;

    private MemeticObserver observer;
    private final String fileName;

    public GeneticAlgorithmBuilder(String name, Problem<S> problem, int populationSize, int offspringPopulationSize,
                                   CrossoverOperator<S> crossover, MutationOperator<S> mutation, String fileName) {
        this.name = name;
        this.fileName = fileName;
        this.createInitialPopulation = new MemeticRandomSolutionsCreation<>(problem, populationSize);
        this.replacement = new MemeticMuPlusLambdaReplacement<>(new ObjectiveComparator<>(0));
        this.variation = new MemeticCrossoverAndMutationVariation<>(offspringPopulationSize, crossover, mutation);
        this.selection = new MemeticNaryTournamentSelection<>(2, this.variation.getMatingPoolSize(), new ObjectiveComparator<>(0));
        this.termination = new MemeticTerminationByEvaluations(25000);
        this.evaluation = new MemeticSequentialEvaluation<>(problem);
    }

    public GeneticAlgorithmBuilder<S> setObserver(MemeticObserver observer){
        this.observer = observer;
        return this;
    }

    public GeneticAlgorithmBuilder<S> setTermination(MemeticTermination termination) {
        this.termination = termination;
        return this;
    }

    public GeneticAlgorithmBuilder<S> setEvaluation(MemeticEvaluation<S> evaluation) {
        this.evaluation = evaluation;
        return this;
    }

    public GeneticAlgorithmBuilder<S> setReplacement(MemeticReplacement<S> replacement) {
        this.replacement = replacement;
        return this;
    }

    public GeneticAlgorithmBuilder<S> setSelection(MemeticSelection<S> selection) {
        this.selection = selection;
        return this;
    }

    public GeneticAlgorithmBuilder<S> setVariation(MemeticVariation<S> variation) {
        this.variation = variation;
        return this;
    }

    public EvolutionaryAlgorithm<S> build() {
        return new EvolutionaryAlgorithm<S>(this.name, this.createInitialPopulation, this.evaluation, this.termination,
                this.selection, this.variation, this.replacement, this.observer, this.fileName) {
            public void updateProgress() {
                S bestFitnessSolution = population().stream().min(new ObjectiveComparator<>(0)).get();
                this.attributes().put("BEST_SOLUTION", bestFitnessSolution);
                super.updateProgress();
            }
        };
    }
}