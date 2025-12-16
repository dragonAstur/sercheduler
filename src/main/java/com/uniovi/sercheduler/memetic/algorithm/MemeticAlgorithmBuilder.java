package com.uniovi.sercheduler.memetic.algorithm;

import com.uniovi.sercheduler.jmetal.problem.SchedulePermutationSolution;
import com.uniovi.sercheduler.jmetal.problem.SchedulingProblem;
import com.uniovi.sercheduler.localsearch.algorithms.localsearchalgorithm.LocalSearchAlgorithm;
import com.uniovi.sercheduler.localsearch.algorithms.localsearchcomponents.InitialSolutionGeneratorSpecified;
import com.uniovi.sercheduler.localsearch.algorithms.localsearchcomponents.TerminationCriterion;
import com.uniovi.sercheduler.localsearch.algorithms.localsearchcomponents.UpgradeAndTimeLimitTermination;
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

public class MemeticAlgorithmBuilder {

    private String name;
    private Evaluation<SchedulePermutationSolution> evaluation;
    private SolutionsCreation<SchedulePermutationSolution> createInitialPopulation;
    private Termination termination;
    private Selection<SchedulePermutationSolution> selection;
    private Variation<SchedulePermutationSolution> variation;
    private Replacement<SchedulePermutationSolution> replacement;

    private TerminationCriterion terminationCriterion;
    private InitialSolutionGeneratorSpecified initialSolutionGenerator;
    private LocalSearchAlgorithm lsa;

    public MemeticAlgorithmBuilder(String name, SchedulingProblem problem, int populationSize,
                                   int offspringPopulationSize,
                                   CrossoverOperator<SchedulePermutationSolution> crossover,
                                   MutationOperator<SchedulePermutationSolution> mutation,
                                   Long limitTime){

        this.name = name;
        this.createInitialPopulation = new RandomSolutionsCreation<>(problem, populationSize);
        this.replacement = new MuPlusLambdaReplacement<>(new ObjectiveComparator<>(0));
        this.variation = new CrossoverAndMutationVariation<>(offspringPopulationSize, crossover, mutation);
        this.selection = new NaryTournamentSelection<>(2, this.variation.getMatingPoolSize(), new ObjectiveComparator<>(0));
        this.termination = new TerminationByEvaluations(25000);
        this.evaluation = new SequentialEvaluation<>(problem);

        this.terminationCriterion = new UpgradeAndTimeLimitTermination(limitTime);
        this.initialSolutionGenerator = new InitialSolutionGeneratorSpecified();

        this.lsa = new LocalSearchAlgorithm.Builder(problem)
                .terminationCriterion(terminationCriterion)
                .initialSolutionGenerator(initialSolutionGenerator)
                .build();

    }

    public MemeticAlgorithmBuilder setTermination(Termination termination) {
        this.termination = termination;
        return this;
    }

    public MemeticAlgorithmBuilder setEvaluation(Evaluation<SchedulePermutationSolution> evaluation) {
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

    public MemeticAlgorithm build() {
        return new MemeticAlgorithm(this.name, this.createInitialPopulation, this.evaluation, this.termination,
                this.selection, this.variation, this.replacement, this.lsa, this.terminationCriterion, this.initialSolutionGenerator) {
            @Override
            public void updateProgress() {
                SchedulePermutationSolution bestFitnessSolution = this.population().stream().min(new ObjectiveComparator<>(0)).get();
                this.attributes().put("BEST_SOLUTION", bestFitnessSolution);
                super.updateProgress();
            }
        };
    }

}
