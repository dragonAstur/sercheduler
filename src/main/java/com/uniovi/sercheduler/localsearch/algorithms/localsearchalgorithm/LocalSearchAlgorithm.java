package com.uniovi.sercheduler.localsearch.algorithms.localsearchalgorithm;

import com.uniovi.sercheduler.jmetal.problem.SchedulePermutationSolution;
import com.uniovi.sercheduler.jmetal.problem.SchedulingProblem;
import com.uniovi.sercheduler.localsearch.algorithms.localsearchcomponents.*;
import com.uniovi.sercheduler.localsearch.evaluator.LocalsearchEvaluator;
import com.uniovi.sercheduler.localsearch.observer.NeighborhoodObserver;
import com.uniovi.sercheduler.localsearch.operator.GeneratedNeighbor;
import com.uniovi.sercheduler.localsearch.operator.NeighborhoodOperatorGlobal;
import com.uniovi.sercheduler.localsearch.operator.NeighborhoodOperatorLazy;
import com.uniovi.sercheduler.service.FitnessCalculator;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

public class LocalSearchAlgorithm {

    private final FitnessCalculatorGenerator fitnessCalculatorGenerator;
    private final InitialSolutionGenerator initialSolutionGenerator;
    private final LocalSearchEvaluatorGenerator localSearchEvaluatorGenerator;
    private final NeighborGenerator neighborGenerator;
    private final NeighborSelector neighborSelector;
    private final AcceptanceCriterion acceptanceCriterion;
    private final TerminationCriterion terminationCriterion;


    public LocalSearchAlgorithm(Builder builder){
        this.fitnessCalculatorGenerator = builder.fitnessCalculatorGenerator;
        this.initialSolutionGenerator = builder.initialSolutionGenerator;
        this.localSearchEvaluatorGenerator = builder.localSearchEvaluatorGenerator;
        this.neighborGenerator = builder.neighborGenerator;
        this.neighborSelector = builder.neighborSelector;
        this.acceptanceCriterion = builder.acceptanceCriterion;
        this.terminationCriterion = builder.terminationCriterion;
    }


    public static class Builder {

        private FitnessCalculatorGenerator fitnessCalculatorGenerator;
        private InitialSolutionGenerator initialSolutionGenerator;
        private LocalSearchEvaluatorGenerator localSearchEvaluatorGenerator;
        private NeighborGenerator neighborGenerator = new NeighborGeneratorImpl();
        private NeighborSelector neighborSelector = new NeighborSelectorImpl();
        private AcceptanceCriterion acceptanceCriterion = new AcceptanceCriterionImpl();
        private TerminationCriterion terminationCriterion = new UpgradeTermination();

        public Builder(SchedulingProblem problem){
            this.fitnessCalculatorGenerator = new FitnessCalculatorGeneratorImpl(problem);
            this.initialSolutionGenerator = new InitialSolutionGeneratorImpl(problem);
            this.localSearchEvaluatorGenerator = new LocalSearchEvaluatorGeneratorImpl(problem);
        }

        public Builder fitnessCalculatorGenerator(FitnessCalculatorGenerator fitnessCalculatorGenerator){
            this.fitnessCalculatorGenerator = fitnessCalculatorGenerator;
            return this;
        }

        public Builder initialSolutionGenerator(InitialSolutionGenerator initialSolutionGenerator){
            this.initialSolutionGenerator = initialSolutionGenerator;
            return this;
        }

        public Builder localSearchEvaluatorGenerator(LocalSearchEvaluatorGenerator localSearchEvaluatorGenerator){
            this.localSearchEvaluatorGenerator = localSearchEvaluatorGenerator;
            return this;
        }

        public Builder neighborGenerator(NeighborGenerator neighborGenerator){
            this.neighborGenerator = neighborGenerator;
            return this;
        }

        public Builder neighborSelector(NeighborSelector neighborSelector){
            this.neighborSelector = neighborSelector;
            return this;
        }

        public Builder acceptanceCriterion(AcceptanceCriterion acceptanceCriterion){
            this.acceptanceCriterion = acceptanceCriterion;
            return this;
        }

        public Builder terminationCriterion(TerminationCriterion terminationCriterion){
            this.terminationCriterion = terminationCriterion;
            return this;
        }

        public LocalSearchAlgorithm build(){
            return new LocalSearchAlgorithm(this);
        }
    }


    public SchedulePermutationSolution runLocalSearchGlobal(
            List<NeighborhoodOperatorGlobal> neighborhoodOperatorList,
            AtomicInteger localSearchIterations,
            NeighborhoodObserver observer
    ) {
        FitnessCalculator fitnessCalculator = fitnessCalculatorGenerator.createFitnessCalculator();
        SchedulePermutationSolution actualSolution = initialSolutionGenerator.createInitialSolution(fitnessCalculator);
        LocalsearchEvaluator evaluator = localSearchEvaluatorGenerator.createLocalSearchEvaluator(fitnessCalculator);

        List<GeneratedNeighbor> neighbors;
        SchedulePermutationSolution bestNeighbor;

        do {
            localSearchIterations.incrementAndGet();

            terminationCriterion.setUpgradeFound(false);

            neighbors = neighborGenerator.generateNeighborsGlobal(neighborhoodOperatorList, actualSolution);

            bestNeighbor = neighborSelector.selectBestNeighborGlobal(actualSolution, neighbors, evaluator, observer);

            if (acceptanceCriterion.checkAcceptance(actualSolution, bestNeighbor)) {
                actualSolution = bestNeighbor;
                terminationCriterion.setUpgradeFound(true);
            }

            observer.addReachedMakespan(actualSolution.getFitnessInfo().fitness().get("makespan"));

        } while(terminationCriterion.checkTerminationCondition());

        return actualSolution;
    }

    /*public SchedulePermutationSolution runLocalSearchGlobal(
            NeighborhoodOperatorGlobal neighborhoodOperator,
            AtomicInteger localSearchIterations,
            NeighborhoodObserver observer
    ) {

        return runLocalSearchGlobal(List.of(neighborhoodOperator), localSearchIterations, observer);
    }*/

    public SchedulePermutationSolution runLocalSearchLazy(
            List<NeighborhoodOperatorLazy> neighborhoodLazyOperatorList,
            AtomicInteger localSearchIterations,
            NeighborhoodObserver observer
    ) {
        FitnessCalculator fitnessCalculator = fitnessCalculatorGenerator.createFitnessCalculator();
        SchedulePermutationSolution actualSolution = initialSolutionGenerator.createInitialSolution(fitnessCalculator);
        LocalsearchEvaluator evaluator = localSearchEvaluatorGenerator.createLocalSearchEvaluator(fitnessCalculator);

        Stream<GeneratedNeighbor> neighbors;
        Optional<GeneratedNeighbor> maybeBetterNeighbor;

        do {
            localSearchIterations.incrementAndGet();

            terminationCriterion.setUpgradeFound(false);

            neighbors = neighborGenerator.generateNeighborsLazy(neighborhoodLazyOperatorList, actualSolution);

            AtomicInteger counter = new AtomicInteger();

            maybeBetterNeighbor = neighborSelector.selectBestNeighborLazy(actualSolution, neighbors, evaluator, counter, acceptanceCriterion);

            if (maybeBetterNeighbor.isPresent()) {
                actualSolution = maybeBetterNeighbor.get().generatedSolution();
                terminationCriterion.setUpgradeFound(true);
            }

            observer.addReachedMakespan(actualSolution.getFitnessInfo().fitness().get("makespan"));

        } while (terminationCriterion.checkTerminationCondition());

        return actualSolution;
    }

    /*public SchedulePermutationSolution runLocalSearchLazy(
            NeighborhoodOperatorLazy neighborhoodLazyOperator,
            AtomicInteger localSearchIterations,
            NeighborhoodObserver observer
    ) {
        return this.runLocalSearchLazy(List.of(neighborhoodLazyOperator), localSearchIterations, observer);
    }*/

    public long startTimeCounter(){
        return terminationCriterion.startTimeCounter();
    }


}
