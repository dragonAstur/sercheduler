package com.uniovi.sercheduler.jmetal.algorithm;

import org.uma.jmetal.algorithm.Algorithm;
import org.uma.jmetal.component.catalogue.common.evaluation.Evaluation;
import org.uma.jmetal.component.catalogue.common.solutionscreation.SolutionsCreation;
import org.uma.jmetal.component.catalogue.common.termination.Termination;
import org.uma.jmetal.component.catalogue.pso.globalbestinitialization.GlobalBestInitialization;
import org.uma.jmetal.component.catalogue.pso.globalbestselection.GlobalBestSelection;
import org.uma.jmetal.component.catalogue.pso.globalbestupdate.GlobalBestUpdate;
import org.uma.jmetal.component.catalogue.pso.inertiaweightcomputingstrategy.InertiaWeightComputingStrategy;
import org.uma.jmetal.component.catalogue.pso.localbestinitialization.LocalBestInitialization;
import org.uma.jmetal.component.catalogue.pso.localbestupdate.LocalBestUpdate;
import org.uma.jmetal.component.catalogue.pso.perturbation.Perturbation;
import org.uma.jmetal.component.catalogue.pso.positionupdate.PositionUpdate;
import org.uma.jmetal.component.catalogue.pso.velocityinitialization.VelocityInitialization;
import org.uma.jmetal.component.catalogue.pso.velocityupdate.VelocityUpdate;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.archive.BoundedArchive;
import org.uma.jmetal.util.observable.Observable;
import org.uma.jmetal.util.observable.ObservableEntity;
import org.uma.jmetal.util.observable.impl.DefaultObservable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MultiParticleSwarmOptimizationAlgorithm implements Algorithm<List<DoubleSolution>>, ObservableEntity<Map<String, Object>> {

    private List<DoubleSolution> swarm;
    private double[][] speed;
    private DoubleSolution[] localBest;
    private BoundedArchive<DoubleSolution> globalBest;
    private Evaluation<DoubleSolution> evaluation;
    private SolutionsCreation<DoubleSolution> createInitialSwarm;
    private Termination termination;
    private VelocityInitialization velocityInitialization;
    private LocalBestInitialization localBestInitialization;
    private GlobalBestInitialization globalBestInitialization;
    private VelocityUpdate velocityUpdate;
    private PositionUpdate positionUpdate;
    private Perturbation perturbation;
    private GlobalBestUpdate globalBestUpdate;
    private LocalBestUpdate localBestUpdate;
    private InertiaWeightComputingStrategy inertiaWeightComputingStrategy;
    private GlobalBestSelection globalBestSelection;
    private Map<String, Object> attributes;
    private long initTime;
    private long totalComputingTime;
    private int evaluations;
    private Observable<Map<String, Object>> observable;
    private final String name;

    public MultiParticleSwarmOptimizationAlgorithm(String name, SolutionsCreation<DoubleSolution> createInitialSwarm, Evaluation<DoubleSolution> evaluation, Termination termination, VelocityInitialization velocityInitialization, LocalBestInitialization localBestInitialization, GlobalBestInitialization globalBestInitialization, InertiaWeightComputingStrategy inertiaWeightComputingStrategy, VelocityUpdate velocityUpdate, PositionUpdate positionUpdate, Perturbation perturbation, GlobalBestUpdate globalBestUpdate, LocalBestUpdate localBestUpdate, GlobalBestSelection globalBestSelection, BoundedArchive<DoubleSolution> globalBestArchive) {
        this.name = name;
        this.evaluation = evaluation;
        this.createInitialSwarm = createInitialSwarm;
        this.termination = termination;
        this.globalBest = globalBestArchive;
        this.velocityInitialization = velocityInitialization;
        this.localBestInitialization = localBestInitialization;
        this.globalBestInitialization = globalBestInitialization;
        this.inertiaWeightComputingStrategy = inertiaWeightComputingStrategy;
        this.velocityUpdate = velocityUpdate;
        this.positionUpdate = positionUpdate;
        this.perturbation = perturbation;
        this.globalBestUpdate = globalBestUpdate;
        this.localBestUpdate = localBestUpdate;
        this.globalBestSelection = globalBestSelection;
        this.observable = new DefaultObservable<>("Multi Particle Swarm Optimization Algorithm");
        this.attributes = new HashMap<>();
    }
    @Override
    public void run() {
        this.initTime = System.currentTimeMillis();
        this.swarm = this.createInitialSwarm.create();

        var initialEvaluatedSwarm = this.evaluation.evaluate(this.swarm);
        this.speed = this.velocityInitialization.initialize(this.swarm);
        this.localBest = this.localBestInitialization.initialize(initialEvaluatedSwarm);
        this.globalBest = this.globalBestInitialization.initialize(initialEvaluatedSwarm, this.globalBest);
        this.initProgress();

        while(!this.termination.isMet(this.attributes)) {
            this.speed = this.velocityUpdate.update(this.swarm, this.speed, this.localBest, this.globalBest, this.globalBestSelection, this.inertiaWeightComputingStrategy);
            this.swarm = this.positionUpdate.update(this.swarm, this.speed);
            this.swarm = this.perturbation.perturb(this.swarm);
            // We don't modify the particle only add it to the global best and local best
            var evaluatedSwarm = this.evaluation.evaluate(this.swarm);

            this.globalBest = this.globalBestUpdate.update(evaluatedSwarm, this.globalBest);
            this.localBest = this.localBestUpdate.update(evaluatedSwarm, this.localBest);
            this.updateProgress();
        }

        this.totalComputingTime = System.currentTimeMillis() - this.initTime;

    }


    protected void initProgress() {
        this.evaluations = this.swarm.size() * 2;
        this.globalBest.computeDensityEstimator();
        this.attributes.put("EVALUATIONS", this.evaluations);
        this.attributes.put("POPULATION", this.globalBest.solutions());
        this.attributes.put("COMPUTING_TIME", this.currentComputingTime());
    }

    protected void updateProgress() {
        this.evaluations += this.swarm.size() * 2;
        this.globalBest.computeDensityEstimator();
        this.attributes.put("EVALUATIONS", this.evaluations);
        this.attributes.put("POPULATION", this.globalBest.solutions());
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

    @Override
    public List<DoubleSolution> result() {
       return this.globalBest.solutions();
    }

    @Override
    public String name() {
        return this.name;
    }

    @Override
    public String description() {
        return "Multi Particle Optimization";
    }

    @Override
    public Observable<Map<String, Object>> observable() {
        return null;
    }
}
