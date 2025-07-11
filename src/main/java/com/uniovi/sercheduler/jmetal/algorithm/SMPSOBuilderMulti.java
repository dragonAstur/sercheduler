package com.uniovi.sercheduler.jmetal.algorithm;

import org.uma.jmetal.component.algorithm.ParticleSwarmOptimizationAlgorithm;
import org.uma.jmetal.component.algorithm.multiobjective.SMPSOBuilder;
import org.uma.jmetal.component.catalogue.common.evaluation.Evaluation;
import org.uma.jmetal.component.catalogue.common.evaluation.impl.SequentialEvaluation;
import org.uma.jmetal.component.catalogue.common.solutionscreation.SolutionsCreation;
import org.uma.jmetal.component.catalogue.common.solutionscreation.impl.RandomSolutionsCreation;
import org.uma.jmetal.component.catalogue.common.termination.Termination;
import org.uma.jmetal.component.catalogue.common.termination.impl.TerminationByEvaluations;
import org.uma.jmetal.component.catalogue.pso.globalbestinitialization.GlobalBestInitialization;
import org.uma.jmetal.component.catalogue.pso.globalbestinitialization.impl.DefaultGlobalBestInitialization;
import org.uma.jmetal.component.catalogue.pso.globalbestselection.GlobalBestSelection;
import org.uma.jmetal.component.catalogue.pso.globalbestselection.impl.BinaryTournamentGlobalBestSelection;
import org.uma.jmetal.component.catalogue.pso.globalbestupdate.GlobalBestUpdate;
import org.uma.jmetal.component.catalogue.pso.globalbestupdate.impl.DefaultGlobalBestUpdate;
import org.uma.jmetal.component.catalogue.pso.inertiaweightcomputingstrategy.InertiaWeightComputingStrategy;
import org.uma.jmetal.component.catalogue.pso.inertiaweightcomputingstrategy.impl.ConstantValueStrategy;
import org.uma.jmetal.component.catalogue.pso.localbestinitialization.LocalBestInitialization;
import org.uma.jmetal.component.catalogue.pso.localbestinitialization.impl.DefaultLocalBestInitialization;
import org.uma.jmetal.component.catalogue.pso.localbestupdate.LocalBestUpdate;
import org.uma.jmetal.component.catalogue.pso.localbestupdate.impl.DefaultLocalBestUpdate;
import org.uma.jmetal.component.catalogue.pso.perturbation.Perturbation;
import org.uma.jmetal.component.catalogue.pso.perturbation.impl.FrequencySelectionMutationBasedPerturbation;
import org.uma.jmetal.component.catalogue.pso.positionupdate.PositionUpdate;
import org.uma.jmetal.component.catalogue.pso.positionupdate.impl.DefaultPositionUpdate;
import org.uma.jmetal.component.catalogue.pso.velocityinitialization.VelocityInitialization;
import org.uma.jmetal.component.catalogue.pso.velocityinitialization.impl.DefaultVelocityInitialization;
import org.uma.jmetal.component.catalogue.pso.velocityupdate.VelocityUpdate;
import org.uma.jmetal.component.catalogue.pso.velocityupdate.impl.ConstrainedVelocityUpdate;
import org.uma.jmetal.operator.mutation.MutationOperator;
import org.uma.jmetal.operator.mutation.impl.PolynomialMutation;
import org.uma.jmetal.problem.doubleproblem.DoubleProblem;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.archive.BoundedArchive;
import org.uma.jmetal.util.archive.impl.CrowdingDistanceArchive;
import org.uma.jmetal.util.comparator.dominanceComparator.impl.DefaultDominanceComparator;

public class SMPSOBuilderMulti {


    private final String name = "SMPSO-Multi";
    private SolutionsCreation<DoubleSolution> swarmInitialization;
    private Evaluation<DoubleSolution> evaluation;
    private Termination termination;
    private VelocityInitialization velocityInitialization;
    private LocalBestInitialization localBestInitialization;
    private GlobalBestInitialization globalBestInitialization;
    private InertiaWeightComputingStrategy inertiaWeightComputingStrategy;
    private VelocityUpdate velocityUpdate;
    private PositionUpdate positionUpdate;
    private Perturbation perturbation;
    private GlobalBestUpdate globalBestUpdate;
    private LocalBestUpdate localBestUpdate;
    private GlobalBestSelection globalBestSelection;
    private BoundedArchive<DoubleSolution> archive;

    public SMPSOBuilderMulti(DoubleProblem problem, int swarmSize) {
        this.swarmInitialization = new RandomSolutionsCreation(problem, swarmSize);
        this.evaluation = new SequentialEvaluation(problem);
        this.termination = new TerminationByEvaluations(25000);
        this.velocityInitialization = new DefaultVelocityInitialization();
        this.localBestInitialization = new DefaultLocalBestInitialization();
        this.globalBestInitialization = new DefaultGlobalBestInitialization();
        this.archive = new CrowdingDistanceArchive(swarmSize);
        this.globalBestSelection = new BinaryTournamentGlobalBestSelection(this.archive.comparator());
        double r1Min = (double)0.0F;
        double r1Max = (double)1.0F;
        double r2Min = (double)0.0F;
        double r2Max = (double)1.0F;
        double c1Min = (double)1.5F;
        double c1Max = (double)2.5F;
        double c2Min = (double)1.5F;
        double c2Max = (double)2.5F;
        double weight = 0.1;
        this.inertiaWeightComputingStrategy = new ConstantValueStrategy(weight);
        this.velocityUpdate = new ConstrainedVelocityUpdate(r1Min, r1Max, r2Min, r2Max, c1Min, c1Max, c2Min, c2Max, problem);
        double velocityChangeWhenLowerLimitIsReached = (double)-1.0F;
        double velocityChangeWhenUpperLimitIsReached = (double)-1.0F;
        this.positionUpdate = new DefaultPositionUpdate(velocityChangeWhenLowerLimitIsReached, velocityChangeWhenUpperLimitIsReached, problem.variableBounds());
        int frequencyOfMutation = 6;
        MutationOperator<DoubleSolution> mutationOperator = new PolynomialMutation((double)1.0F / (double)problem.numberOfVariables(), (double)20.0F);
        this.perturbation = new FrequencySelectionMutationBasedPerturbation(mutationOperator, frequencyOfMutation);
        this.globalBestUpdate = new DefaultGlobalBestUpdate();
        this.localBestUpdate = new DefaultLocalBestUpdate(new DefaultDominanceComparator<>());
    }

    public SMPSOBuilderMulti setTermination(Termination termination) {
        this.termination = termination;
        return this;
    }

    public SMPSOBuilderMulti setArchive(BoundedArchive<DoubleSolution> archive) {
        this.archive = archive;
        return this;
    }

    public SMPSOBuilderMulti setEvaluation(Evaluation<DoubleSolution> evaluation) {
        this.evaluation = evaluation;
        return this;
    }

    public SMPSOBuilderMulti setPerturbation(Perturbation perturbation) {
        this.perturbation = perturbation;
        return this;
    }

    public SMPSOBuilderMulti setPositionUpdate(PositionUpdate positionUpdate) {
        this.positionUpdate = positionUpdate;
        return this;
    }

    public SMPSOBuilderMulti setGlobalBestSelection(GlobalBestSelection globalBestSelection) {
        this.globalBestSelection = globalBestSelection;
        return this;
    }

    public SMPSOBuilderMulti setGlobalBestInitialization(GlobalBestInitialization globalBestInitialization) {
        this.globalBestInitialization = globalBestInitialization;
        return this;
    }

    public SMPSOBuilderMulti setGlobalBestUpdate(GlobalBestUpdate globalBestUpdate) {
        this.globalBestUpdate = globalBestUpdate;
        return this;
    }

    public SMPSOBuilderMulti setLocalBestUpdate(LocalBestUpdate localBestUpdate) {
        this.localBestUpdate = localBestUpdate;
        return this;
    }

    public MultiParticleSwarmOptimizationAlgorithm build() {
        return new MultiParticleSwarmOptimizationAlgorithm(this.name, this.swarmInitialization, this.evaluation, this.termination, this.velocityInitialization, this.localBestInitialization, this.globalBestInitialization, this.inertiaWeightComputingStrategy, this.velocityUpdate, this.positionUpdate, this.perturbation, this.globalBestUpdate, this.localBestUpdate, this.globalBestSelection, this.archive);
    }

}
