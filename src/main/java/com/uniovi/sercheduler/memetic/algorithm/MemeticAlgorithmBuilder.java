package com.uniovi.sercheduler.memetic.algorithm;

import com.uniovi.sercheduler.jmetal.problem.SchedulePermutationSolution;
import com.uniovi.sercheduler.jmetal.problem.SchedulingProblem;
import com.uniovi.sercheduler.localsearch.algorithms.localsearchalgorithm.LocalSearchAlgorithm;
import com.uniovi.sercheduler.localsearch.algorithms.localsearchcomponents.InitialSolutionGeneratorSpecified;
import com.uniovi.sercheduler.localsearch.algorithms.localsearchcomponents.NeighborLimiter;
import com.uniovi.sercheduler.localsearch.algorithms.localsearchcomponents.TerminationCriterion;
import com.uniovi.sercheduler.localsearch.algorithms.localsearchcomponents.UpgradeIterationAndTimeLimitTermination;
import com.uniovi.sercheduler.localsearch.operator.NeighborhoodOperatorLazy;
import com.uniovi.sercheduler.memetic.algorithm.components.ElitistLsaApplier;
import com.uniovi.sercheduler.memetic.algorithm.components.LsaApplier;
import com.uniovi.sercheduler.memetic.algorithm.components.MemeticEvaluation;
import com.uniovi.sercheduler.memetic.algorithm.components.MemeticSequentialEvaluation;
import com.uniovi.sercheduler.memetic.observer.MemeticObserver;
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
import org.uma.jmetal.util.comparator.ObjectiveComparator;

import java.util.List;

public class MemeticAlgorithmBuilder {


    private final String name;
    private MemeticEvaluation<SchedulePermutationSolution> evaluation;
    private final SolutionsCreation<SchedulePermutationSolution> createInitialPopulation;
    private Termination termination;
    private Selection<SchedulePermutationSolution> selection;
    private Variation<SchedulePermutationSolution> variation;
    private Replacement<SchedulePermutationSolution> replacement;

    private InitialSolutionGeneratorSpecified initialSolutionGenerator;
    private final LocalSearchAlgorithm.Builder lsaBuilder;
    private final List<NeighborhoodOperatorLazy> operatorList;
    private MemeticObserver observer;
    private final String fileName;

    private LsaApplier lsaApplier;

    public MemeticAlgorithmBuilder(String name, SchedulingProblem problem, int populationSize,
                                   int offspringPopulationSize,
                                   CrossoverOperator<SchedulePermutationSolution> crossover,
                                   MutationOperator<SchedulePermutationSolution> mutation,
                                   Long limitTime, List<NeighborhoodOperatorLazy> operatorList,
                                   int lsaIterationsLimit, String fileName){

        this.name = name;
        this.fileName = fileName;
        this.createInitialPopulation = new RandomSolutionsCreation<>(problem, populationSize);
        this.replacement = new MuPlusLambdaReplacement<>(new ObjectiveComparator<>(0));
        this.variation = new CrossoverAndMutationVariation<>(offspringPopulationSize, crossover, mutation);
        this.selection = new NaryTournamentSelection<>(2, this.variation.getMatingPoolSize(), new ObjectiveComparator<>(0));
        this.termination = new TerminationByEvaluations(25000);
        this.evaluation = new MemeticSequentialEvaluation<>(problem);

        TerminationCriterion terminationCriterion = new UpgradeIterationAndTimeLimitTermination(limitTime, lsaIterationsLimit);
        this.initialSolutionGenerator = new InitialSolutionGeneratorSpecified();

        this.operatorList = operatorList;

        this.lsaBuilder = new LocalSearchAlgorithm.Builder(problem)
                .terminationCriterion(terminationCriterion)
                .initialSolutionGenerator(this.initialSolutionGenerator);
                //.operatorSelector(new RandomOperatorSelector())         //TODO: esto debería venir por comandos

        this.lsaApplier = new ElitistLsaApplier();

    }

    public MemeticAlgorithmBuilder setObserver(MemeticObserver observer){
        this.observer = observer;
        return this;
    }

    public MemeticAlgorithmBuilder setInitialSolutionGenerator(InitialSolutionGeneratorSpecified initialSolutionGenerator) {
        this.initialSolutionGenerator = initialSolutionGenerator;
        return this;
    }

    public MemeticAlgorithmBuilder setTermination(Termination termination) {
        this.termination = termination;
        return this;
    }

    public MemeticAlgorithmBuilder setNeighborLimiter(NeighborLimiter neighborLimiter){
        this.lsaBuilder.neighborLimiter(neighborLimiter);
        return this;
    }

    public MemeticAlgorithmBuilder setEvaluation(MemeticEvaluation<SchedulePermutationSolution> evaluation) {
        this.evaluation = evaluation;
        return this;
    }

    public MemeticAlgorithmBuilder setReplacement(Replacement<SchedulePermutationSolution> replacement) {
        this.replacement = replacement;
        return this;
    }

    public MemeticAlgorithmBuilder setSelection(Selection<SchedulePermutationSolution> selection) {
        this.selection = selection;
        return this;
    }

    public MemeticAlgorithmBuilder setVariation(Variation<SchedulePermutationSolution> variation) {
        this.variation = variation;
        return this;
    }

    public MemeticAlgorithmBuilder setLsaApplier(LsaApplier lsaApplier){
        this.lsaApplier = lsaApplier;
        return this;
    }


    public MemeticAlgorithm build() {
        return new MemeticAlgorithm(this.name, this.createInitialPopulation, this.evaluation, this.termination,
                this.selection, this.variation, this.replacement, this.lsaBuilder.build(), this.operatorList, this.observer,
                this.fileName, this.lsaApplier) {
            @Override
            public void updateProgress() {
                SchedulePermutationSolution bestFitnessSolution = this.population().stream().min(new ObjectiveComparator<>(0)).get();
                this.attributes().put("BEST_SOLUTION", bestFitnessSolution);
                super.updateProgress();
            }
        };
    }

}
