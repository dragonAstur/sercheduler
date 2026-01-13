package com.uniovi.sercheduler.memetic.algorithm;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.uma.jmetal.algorithm.Algorithm;
import org.uma.jmetal.component.catalogue.common.evaluation.Evaluation;
import org.uma.jmetal.component.catalogue.common.solutionscreation.SolutionsCreation;
import org.uma.jmetal.component.catalogue.common.termination.Termination;
import org.uma.jmetal.component.catalogue.ea.replacement.Replacement;
import org.uma.jmetal.component.catalogue.ea.selection.Selection;
import org.uma.jmetal.component.catalogue.ea.variation.Variation;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.observable.Observable;
import org.uma.jmetal.util.observable.ObservableEntity;
import org.uma.jmetal.util.observable.impl.DefaultObservable;

public class EvolutionaryAlgorithm<S extends Solution<?>> implements Algorithm<List<S>>, ObservableEntity<Map<String, Object>> {
    private List<S> population;
    private Evaluation<S> evaluation;
    private SolutionsCreation<S> createInitialPopulation;
    private Termination termination;
    private Selection<S> selection;
    private Variation<S> variation;
    private Replacement<S> replacement;
    private final Map<String, Object> attributes;
    private long initTime;
    private long totalComputingTime;
    private int evaluations;
    private final Observable<Map<String, Object>> observable;
    private final String name;

    public EvolutionaryAlgorithm(String name, SolutionsCreation<S> initialPopulationCreation, Evaluation<S> evaluation, Termination termination, Selection<S> selection, Variation<S> variation, Replacement<S> replacement) {
        this.name = name;
        this.createInitialPopulation = initialPopulationCreation;
        this.evaluation = evaluation;
        this.termination = termination;
        this.selection = selection;
        this.variation = variation;
        this.replacement = replacement;
        this.observable = new DefaultObservable<>("Evolutionary Algorithm");
        this.attributes = new HashMap<>();
    }

    public void run() {
        this.initTime = System.currentTimeMillis();
        this.population = this.createInitialPopulation.create();
        this.population = this.evaluation.evaluate(this.population);
        this.initProgress();

        while(!this.termination.isMet(this.attributes)) {
            List<S> matingPopulation = this.selection.select(this.population);
            List<S> offspringPopulation = this.variation.variate(this.population, matingPopulation);
            offspringPopulation = this.evaluation.evaluate(offspringPopulation);
            this.population = this.replacement.replace(this.population, offspringPopulation);
            this.updateProgress();
        }

        this.totalComputingTime = System.currentTimeMillis() - this.initTime;
    }

    protected void initProgress() {
        this.evaluations = this.population.size();
        this.attributes.put("EVALUATIONS", this.evaluations);
        this.attributes.put("POPULATION", this.population);
        this.attributes.put("COMPUTING_TIME", this.currentComputingTime());
    }

    protected void updateProgress() {
        this.evaluations += this.variation.getOffspringPopulationSize();
        this.attributes.put("EVALUATIONS", this.evaluations);
        this.attributes.put("POPULATION", this.population);
        this.attributes.put("COMPUTING_TIME", this.currentComputingTime());
        this.observable.setChanged();
        this.observable.notifyObservers(this.attributes);
        this.totalComputingTime = this.currentComputingTime();
    }

    public long currentComputingTime() {
        return System.currentTimeMillis() - this.initTime;
    }

    public int numberOfEvaluations() {
        return this.evaluations;
    }

    public long totalComputingTime() {
        return this.totalComputingTime;
    }

    public List<S> result() {
        return this.population;
    }

    public void updatePopulation(List<S> newPopulation) {
        this.population = newPopulation;
    }

    public String name() {
        return this.name;
    }

    public String description() {
        return "Evolutionary algorithm";
    }

    public Map<String, Object> attributes() {
        return this.attributes;
    }

    public List<S> population() {
        return this.population;
    }

    public Observable<Map<String, Object>> observable() {
        return this.observable;
    }

    public void termination(Termination termination) {
        this.termination = termination;
    }

    public Termination termination() {
        return this.termination;
    }

    public void evaluation(Evaluation<S> evaluation) {
        this.evaluation = evaluation;
    }

    public Evaluation<S> evaluation() {
        return this.evaluation;
    }
}
