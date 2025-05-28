package com.uniovi.sercheduler.localsearch.strategy;

import com.uniovi.sercheduler.jmetal.problem.SchedulePermutationSolution;
import com.uniovi.sercheduler.jmetal.problem.SchedulingProblem;
import com.uniovi.sercheduler.localsearch.evaluator.LocalsearchEvaluator;
import com.uniovi.sercheduler.localsearch.observer.NeighborhoodObserver;
import com.uniovi.sercheduler.localsearch.operator.GeneratedNeighbor;
import com.uniovi.sercheduler.localsearch.operator.NeighborhoodOperatorGlobal;
import com.uniovi.sercheduler.localsearch.operator.NeighborhoodOperatorLazy;
import com.uniovi.sercheduler.localsearch.strategy.neighborgenerator.NeighborGenerator;
import com.uniovi.sercheduler.service.FitnessCalculator;
import com.uniovi.sercheduler.service.FitnessCalculatorSimple;
import com.uniovi.sercheduler.service.FitnessInfo;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

public abstract class AbstractStrategy {

    private final NeighborhoodObserver observer;
    private final double UPGRADE_THRESHOLD = 0.01;

    public AbstractStrategy(NeighborhoodObserver observer) {
        this.observer = observer;
    }

    protected NeighborhoodObserver getObserver(){
        return observer;
    }

    protected SchedulePermutationSolution runLocalSearchLazy(
            SchedulingProblem problem,
            List<NeighborhoodOperatorLazy> neighborhoodLazyOperatorList,
            long limitTimeMillis,
            long startingTimeMillis,
            AtomicInteger localSearchIterations,
            NeighborGenerator neighborGenerator
    ) {
        FitnessCalculator fitnessCalculator = createFitnessCalculator(problem);
        SchedulePermutationSolution actualSolution = createInitialSolution(problem, fitnessCalculator);
        LocalsearchEvaluator evaluator = createLocalSearchEvaluator(problem, fitnessCalculator);

        boolean upgradeFound;
        Stream<GeneratedNeighbor> neighbors;
        Optional<GeneratedNeighbor> maybeBetterNeighbor;

        do {
            localSearchIterations.incrementAndGet();
            upgradeFound = false;

            neighbors = neighborGenerator.generateNeighborsLazy(neighborhoodLazyOperatorList, actualSolution);

            AtomicInteger counter = new AtomicInteger();

            maybeBetterNeighbor = selectBestNeighborLazy(actualSolution, neighbors, evaluator, counter);

            if (maybeBetterNeighbor.isPresent()) {
                actualSolution = maybeBetterNeighbor.get().generatedSolution();
                upgradeFound = true;
            }

            getObserver().addReachedMakespan(actualSolution.getFitnessInfo().fitness().get("makespan"));
        } while (upgradeFound && (System.currentTimeMillis() - startingTimeMillis) < limitTimeMillis);

        return actualSolution;
    }

    protected SchedulePermutationSolution runLocalSearchLazy(
            SchedulingProblem problem,
            NeighborhoodOperatorLazy neighborhoodLazyOperator,
            long limitTimeMillis,
            long startingTimeMillis,
            AtomicInteger localSearchIterations,
            NeighborGenerator neighborGenerator
    ) {

        return this.runLocalSearchLazy(problem, List.of(neighborhoodLazyOperator), limitTimeMillis, startingTimeMillis, localSearchIterations, neighborGenerator);
    }

    protected SchedulePermutationSolution runLocalSearchGlobal(
            SchedulingProblem problem,
            List<NeighborhoodOperatorGlobal> neighborhoodOperatorList,
            long limitTimeMillis,
            long startingTimeMillis,
            AtomicInteger localSearchIterations,
            NeighborGenerator neighborGenerator
    ) {
        FitnessCalculator fitnessCalculator = createFitnessCalculator(problem);
        SchedulePermutationSolution actualSolution = createInitialSolution(problem, fitnessCalculator);
        LocalsearchEvaluator evaluator = createLocalSearchEvaluator(problem, fitnessCalculator);

        boolean upgradeFound;
        List<GeneratedNeighbor> neighbors;
        SchedulePermutationSolution bestNeighbor;

        do {
            localSearchIterations.incrementAndGet();
            upgradeFound = false;

            neighbors = neighborGenerator.generateNeighborsGlobal(neighborhoodOperatorList, actualSolution);

            bestNeighbor = selectBestNeighborGlobal(actualSolution, neighbors, evaluator);

            if (checkImprovement(actualSolution, bestNeighbor)) {
                actualSolution = bestNeighbor;
                upgradeFound = true;
            }

            getObserver().addReachedMakespan(actualSolution.getFitnessInfo().fitness().get("makespan"));
        } while (upgradeFound && (System.currentTimeMillis() - startingTimeMillis) < limitTimeMillis);

        return actualSolution;
    }

    protected SchedulePermutationSolution runLocalSearchGlobal(
            SchedulingProblem problem,
            NeighborhoodOperatorGlobal neighborhoodOperator,
            long limitTimeMillis,
            long startingTimeMillis,
            AtomicInteger localSearchIterations,
            NeighborGenerator neighborGenerator
    ) {

        return runLocalSearchGlobal(problem, List.of(neighborhoodOperator), limitTimeMillis, startingTimeMillis, localSearchIterations, neighborGenerator);
    }

    private FitnessCalculator createFitnessCalculator(SchedulingProblem problem){
        return new FitnessCalculatorSimple(problem.getInstanceData());
    }

    private SchedulePermutationSolution createInitialSolution(SchedulingProblem problem, FitnessCalculator fitnessCalculator){
        SchedulePermutationSolution actualSolution = problem.createSolution();

        //Evaluate this new created solution (this step is skipped in the pseudocode)

        FitnessInfo fitnessInfo = fitnessCalculator.calculateFitness(actualSolution);
        actualSolution.setFitnessInfo(fitnessInfo);

        return actualSolution;
    }

    private LocalsearchEvaluator createLocalSearchEvaluator(SchedulingProblem problem, FitnessCalculator fitnessCalculator){

        return new LocalsearchEvaluator(fitnessCalculator.getComputationMatrix(), fitnessCalculator.getNetworkMatrix(), problem.getInstanceData());
    }

    private Optional<GeneratedNeighbor> selectBestNeighborLazy(SchedulePermutationSolution actualSolution, Stream<GeneratedNeighbor> neighbors, LocalsearchEvaluator evaluator, AtomicInteger counter) {
        return neighbors
                .filter(neighbor -> {

                    counter.incrementAndGet();

                    evaluator.evaluate(actualSolution, neighbor.generatedSolution(), neighbor.movements().get(neighbor.movements().size() - 1));

                    return checkImprovement(actualSolution, neighbor);
                })
                .findFirst();
    }

    private SchedulePermutationSolution selectBestNeighborGlobal(SchedulePermutationSolution originalSolution, List<GeneratedNeighbor> neighborsList, LocalsearchEvaluator evaluator){

        SchedulePermutationSolution bestSolution = originalSolution;
        double originalMakespan = originalSolution.getFitnessInfo().fitness().get("makespan");
        double bestMakespan = originalMakespan;
        double neighborMakespan;
        SchedulePermutationSolution neighborSolution;

        int numberOfBetterNeighbors = 0;
        double neighborImprovingRatio = 0.0;
        double allNeighborsImprovingRatioSum = 0.0;
        double betterNeighborsImprovingRatioSum = 0.0;

        for(GeneratedNeighbor neighbor : neighborsList){

            neighborSolution = neighbor.generatedSolution();
            evaluator.evaluate(originalSolution, neighborSolution, neighbor.movements().get(neighbor.movements().size() - 1));
            neighborMakespan = neighborSolution.getFitnessInfo().fitness().get("makespan");

            neighborImprovingRatio = (originalMakespan - neighborMakespan) / originalMakespan * 100;
            allNeighborsImprovingRatioSum += neighborImprovingRatio;

            if(neighborMakespan < originalMakespan){
                numberOfBetterNeighbors++;
                betterNeighborsImprovingRatioSum += neighborImprovingRatio;

                if(bestMakespan > neighborMakespan){
                    bestMakespan = neighborMakespan;
                    bestSolution = neighborSolution;
                }
            }

        }

        getObserver().addBetterNeighborsRatio(numberOfBetterNeighbors * 1.00 / neighborsList.size() );
        getObserver().addAllNeighborsImprovingRatio(allNeighborsImprovingRatioSum / neighborsList.size() );

        if(numberOfBetterNeighbors > 0)
            getObserver().addBetterNeighborsImprovingRatio( betterNeighborsImprovingRatioSum / numberOfBetterNeighbors );
        else
            getObserver().addBetterNeighborsImprovingRatio( 0.0 );

        return bestSolution;
    }

    private boolean checkImprovement(SchedulePermutationSolution actualSolution, GeneratedNeighbor neighbor){
        return actualSolution.getFitnessInfo().fitness().get("makespan")
                - neighbor.generatedSolution().getFitnessInfo().fitness().get("makespan")
                > UPGRADE_THRESHOLD;
    }

    private boolean checkImprovement(SchedulePermutationSolution actualSolution, SchedulePermutationSolution bestNeighbor){
        return actualSolution.getFitnessInfo().fitness().get("makespan") - bestNeighbor.getFitnessInfo().fitness().get("makespan") > UPGRADE_THRESHOLD;
    }

}
