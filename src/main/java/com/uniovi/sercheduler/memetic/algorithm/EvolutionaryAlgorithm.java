package com.uniovi.sercheduler.memetic.algorithm;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.uniovi.sercheduler.jmetal.problem.SchedulePermutationSolution;
import com.uniovi.sercheduler.localsearch.export.CSVExporter;
import com.uniovi.sercheduler.memetic.algorithm.components.*;
import com.uniovi.sercheduler.memetic.observer.MemeticObserver;
import org.uma.jmetal.algorithm.Algorithm;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.observable.Observable;
import org.uma.jmetal.util.observable.ObservableEntity;
import org.uma.jmetal.util.observable.impl.DefaultObservable;

public class EvolutionaryAlgorithm<S extends Solution<?>> implements Algorithm<List<S>>, ObservableEntity<Map<String, Object>> {
    private List<S> population;
    private MemeticEvaluation<S> evaluation;
    private MemeticSolutionsCreation<S> createInitialPopulation;
    private MemeticTermination termination;
    private MemeticSelection<S> selection;
    private MemeticVariation<S> variation;
    private MemeticReplacement<S> replacement;
    private final Map<String, Object> attributes;
    private long initTime;
    private long totalComputingTime;
    private int evaluations;
    private final Observable<Map<String, Object>> observable;
    private final String name;

    private MemeticObserver observer;
    private final String fileName;

    public EvolutionaryAlgorithm(String name, MemeticSolutionsCreation<S> initialPopulationCreation, MemeticEvaluation<S> evaluation,
                                 MemeticTermination termination, MemeticSelection<S> selection, MemeticVariation<S> variation,
                                 MemeticReplacement<S> replacement, MemeticObserver observer, String fileName) {
        this.name = name;
        this.createInitialPopulation = initialPopulationCreation;
        this.evaluation = evaluation;
        this.termination = termination;
        this.selection = selection;
        this.variation = variation;
        this.replacement = replacement;
        this.observable = new DefaultObservable<>("Evolutionary Algorithm");
        this.attributes = new HashMap<>();

        this.observer = observer;
        this.fileName = fileName;
    }

    public void run() {

        this.observer.startRun(System.currentTimeMillis());

        this.initTime = System.currentTimeMillis();
        this.population = this.createInitialPopulation.create();
        this.population = this.evaluation.evaluate(this.population, observer);
        this.initProgress();

        while(!this.termination.isMet(this.attributes)) {
            List<S> matingPopulation = this.selection.select(this.population, observer);
            List<S> offspringPopulation = this.variation.variate(this.population, matingPopulation, observer);
            offspringPopulation = this.evaluation.evaluate(offspringPopulation, observer);
            this.population = this.replacement.replace(this.population, offspringPopulation, observer);

            Solution<?> bestSolution = this.population.stream()
                    .min(Comparator.comparingDouble(x -> x.objectives()[0]))
                    .orElseThrow();

            if(bestSolution instanceof SchedulePermutationSolution bestScheduleSolution)
                observer.updateMemeticEvolution(bestScheduleSolution);

            this.updateProgress();

            this.observer.endMemeticIteration();
        }

        this.totalComputingTime = System.currentTimeMillis() - this.initTime;

        observer.endRun();

        Solution<?> bestSolution = this.population.stream()
                .min(Comparator.comparingDouble(x -> x.objectives()[0]))
                .orElseThrow();

        if(bestSolution instanceof SchedulePermutationSolution bestScheduleSolution)
            observer.updateMemeticEvolution(bestScheduleSolution);

        //XLSXTableExporter.appendMemeticEvolutionSheet(fileName, observer);
        CSVExporter.appendMemeticCSV(fileName, observer);
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

    public void termination(MemeticTermination termination) {
        this.termination = termination;
    }

    public MemeticTermination termination() {
        return this.termination;
    }

    public void evaluation(MemeticEvaluation<S> evaluation) {
        this.evaluation = evaluation;
    }

    public MemeticEvaluation<S> evaluation() {
        return this.evaluation;
    }
}
