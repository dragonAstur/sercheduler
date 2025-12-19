package com.uniovi.sercheduler.memetic.algorithm;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

import com.uniovi.sercheduler.jmetal.problem.SchedulePermutationSolution;
import com.uniovi.sercheduler.localsearch.algorithms.localsearchalgorithm.LocalSearchAlgorithm;
import com.uniovi.sercheduler.localsearch.algorithms.localsearchcomponents.InitialSolutionGenerator;
import com.uniovi.sercheduler.localsearch.algorithms.localsearchcomponents.InitialSolutionGeneratorSpecified;
import com.uniovi.sercheduler.localsearch.algorithms.localsearchcomponents.TerminationCriterion;
import com.uniovi.sercheduler.localsearch.export.XLSXTableExporter;
import com.uniovi.sercheduler.localsearch.observer.LocalSearchObserver;
import com.uniovi.sercheduler.localsearch.operator.NeighborhoodOperatorLazy;
import com.uniovi.sercheduler.memetic.observer.MemeticObserver;
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

public class MemeticAlgorithm implements Algorithm<List<SchedulePermutationSolution>>, ObservableEntity<Map<String, Object>> {


    private List<SchedulePermutationSolution> population;
    private Evaluation<SchedulePermutationSolution> evaluation;
    private SolutionsCreation<SchedulePermutationSolution> createInitialPopulation;
    private Termination termination;
    private Selection<SchedulePermutationSolution> selection;
    private Variation<SchedulePermutationSolution> variation;
    private Replacement<SchedulePermutationSolution> replacement;
    private final Map<String, Object> attributes;
    private long initTime;
    private long totalComputingTime;
    private int evaluations;
    private final Observable<Map<String, Object>> observable;
    private final String name;

    private final LocalSearchAlgorithm lsa;

    private final List<NeighborhoodOperatorLazy> operatorList;
    private final MemeticObserver observer;

    private final String fileName;


    public MemeticAlgorithm(String name, SolutionsCreation<SchedulePermutationSolution> initialPopulationCreation, Evaluation<SchedulePermutationSolution> evaluation,
                            Termination termination, Selection<SchedulePermutationSolution> selection, Variation<SchedulePermutationSolution> variation, Replacement<SchedulePermutationSolution> replacement,
                            LocalSearchAlgorithm lsa, List<NeighborhoodOperatorLazy> operatorList, MemeticObserver observer,
                            String fileName) {
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

        this.lsa = lsa;
        this.operatorList = operatorList;

        this.fileName = fileName;
    }

    public void run() {

        this.initTime = lsa.startTimeCounter();

        observer.startRun(this.initTime);

        this.population = this.createInitialPopulation.create();
        this.population = this.evaluation.evaluate(this.population);
        initProgress();

        while(!this.termination.isMet(this.attributes)) {

            List<SchedulePermutationSolution> matingPopulation = this.selection.select(this.population);
            List<SchedulePermutationSolution> offspringPopulation = this.variation.variate(this.population, matingPopulation);
            offspringPopulation = this.evaluation.evaluate(offspringPopulation);
            this.population = this.replacement.replace(this.population, offspringPopulation);

            observer.updateMemeticEvolution(
                    this.population.get( getBestSolutionPos(this.population) ).getFitnessInfo().fitness().get("makespan"),
                    0,
                    0
            );

            applyLSAToBest(this.population, observer);
            this.updateProgress();

            this.observer.endMemeticIteration();
        }

        this.totalComputingTime = System.currentTimeMillis() - this.initTime;

        observer.endRun();

        XLSXTableExporter.appendMemeticEvolutionSheet(fileName, observer);

    }

    private void applyLSAToBest(List<SchedulePermutationSolution> population, MemeticObserver observer) {

        int bestIndex =
                getBestSolutionPos(population);

        //Usar el initial solution generator
        SchedulePermutationSolution bestSolution = population.get(bestIndex);

        observer.updateMemeticEvolution(
                bestSolution.getFitnessInfo().fitness().get("makespan"),
                0,
                0
        );

        this.lsa.setInitialSolution(bestSolution);

        SchedulePermutationSolution enhancedBestSolution = this.lsa.runLocalSearchLazy(this.operatorList,
                observer);

        population.set(bestIndex, enhancedBestSolution);

        observer.updateMemeticEvolution(
                enhancedBestSolution.getFitnessInfo().fitness().get("makespan"),
                0,
                0
        );

    }

    private static Integer getBestSolutionPos(List<SchedulePermutationSolution> population) {
        return IntStream.range(0, population.size())
                .boxed()
                .min(Comparator.comparingDouble(
                        i -> population.get(i)
                                .getFitnessInfo().fitness().get("makespan")
                ))
                .orElseThrow(() ->
                        new IllegalStateException("There was no element in the offspring population"));
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

    public List<SchedulePermutationSolution> result() {
        return this.population;
    }

    public void updatePopulation(List<SchedulePermutationSolution> newPopulation) {
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

    public List<SchedulePermutationSolution> population() {
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

    public void evaluation(Evaluation<SchedulePermutationSolution> evaluation) {
        this.evaluation = evaluation;
    }

    public Evaluation<SchedulePermutationSolution> evaluation() {
        return this.evaluation;
    }
}
