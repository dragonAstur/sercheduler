package com.uniovi.sercheduler.localsearch.strategy;

import com.uniovi.sercheduler.jmetal.problem.SchedulePermutationSolution;
import com.uniovi.sercheduler.jmetal.problem.SchedulingProblem;
import com.uniovi.sercheduler.localsearch.evaluator.LocalsearchEvaluator;
import com.uniovi.sercheduler.localsearch.observer.NeighborhoodObserver;
import com.uniovi.sercheduler.localsearch.operator.GeneratedNeighbor;
import com.uniovi.sercheduler.localsearch.operator.NeighborhoodOperatorGlobal;
import com.uniovi.sercheduler.localsearch.operator.NeighborhoodOperatorLazy;
import com.uniovi.sercheduler.service.FitnessCalculator;
import com.uniovi.sercheduler.service.FitnessCalculatorSimple;
import com.uniovi.sercheduler.service.FitnessInfo;

import javax.swing.plaf.IconUIResource;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class SimpleClimbingStrategy extends AbstractStrategy {

    private final double UPGRADE_THRESHOLD = 0.01;

    public SimpleClimbingStrategy(NeighborhoodObserver observer) {
        super(observer);
    }

    public SchedulePermutationSolution execute(SchedulingProblem problem, NeighborhoodOperatorLazy neighborhoodLazyOperator){

        List<NeighborhoodOperatorLazy> neighborhoodLazyOperatorList = new ArrayList<>();

        neighborhoodLazyOperatorList.add(neighborhoodLazyOperator);

        return execute(problem, neighborhoodLazyOperatorList);

    }

    public SchedulePermutationSolution execute(SchedulingProblem problem, List<NeighborhoodOperatorLazy> neighborhoodLazyOperatorList){

        getObserver().executionStarted();
        AtomicInteger localSearchIterations = new AtomicInteger();
        long startingTime = System.currentTimeMillis();

        NeighborGenerator unionNeighborGenerator = new UnionNeighborGenerator();


        SchedulePermutationSolution bestSolution = runLocalSearch(problem, neighborhoodLazyOperatorList, Long.MAX_VALUE, startingTime, localSearchIterations, unionNeighborGenerator);

        getObserver().setExecutionTime(System.currentTimeMillis() - startingTime);
        getObserver().setNumberOfIterations(localSearchIterations.get());
        getObserver().setTotalBestMakespan(bestSolution.getFitnessInfo().fitness().get("makespan"));

        getObserver().executionEnded();

        return bestSolution;

    }


    public SchedulePermutationSolution execute(SchedulingProblem problem, NeighborhoodOperatorLazy neighborhoodLazyOperator, Long limitTime){

        List<NeighborhoodOperatorLazy> neighborhoodLazyOperatorList = new ArrayList<>();

        neighborhoodLazyOperatorList.add(neighborhoodLazyOperator);

        return execute(problem, neighborhoodLazyOperatorList, limitTime);

    }

    public SchedulePermutationSolution execute(SchedulingProblem problem, List<NeighborhoodOperatorLazy> neighborhoodLazyOperatorList, Long limitTime){

        getObserver().executionStarted();
        AtomicInteger localSearchIterations = new AtomicInteger();
        long startingTime = System.currentTimeMillis();

        SchedulePermutationSolution totalBestNeighbor = null;
        double totalWorstMakespan = -1;

        NeighborGenerator unionNeighborGenerator = new UnionNeighborGenerator();


        do {

            SchedulePermutationSolution actualSolution = runLocalSearch(problem, neighborhoodLazyOperatorList, limitTime, startingTime, localSearchIterations, unionNeighborGenerator);

            //If it is the first time, initialize the total best neighbor variable
            if(totalBestNeighbor == null)
                totalBestNeighbor = actualSolution;

            if(actualSolution.getFitnessInfo().fitness().get("makespan") < totalBestNeighbor.getFitnessInfo().fitness().get("makespan"))
                totalBestNeighbor = actualSolution;
            else if(actualSolution.getFitnessInfo().fitness().get("makespan") > totalWorstMakespan)
                totalWorstMakespan = actualSolution.getFitnessInfo().fitness().get("makespan");

            getObserver().addReachedMakespan(actualSolution.getFitnessInfo().fitness().get("makespan"));

        } while(System.currentTimeMillis() - startingTime < limitTime);


        getObserver().setTotalBestMakespan(totalBestNeighbor.getFitnessInfo().fitness().get("makespan"));
        getObserver().setTotalWorstMakespan(totalWorstMakespan);

        getObserver().setExecutionTime(System.currentTimeMillis() - startingTime);
        getObserver().setNumberOfIterations(localSearchIterations.get());

        getObserver().executionEnded();

        return totalBestNeighbor;

    }

    public SchedulePermutationSolution executeVNS(SchedulingProblem problem, List<NeighborhoodOperatorLazy> neighborhoodLazyOperatorList, Long limitTime){

        getObserver().executionStarted();
        AtomicInteger localSearchIterations = new AtomicInteger();
        long startingTime = System.currentTimeMillis();

        SchedulePermutationSolution totalBestNeighbor = null;
        double totalWorstMakespan = -1;

        NeighborGenerator unionNeighborGenerator = new UnionNeighborGenerator();

        Random random = new Random();
        NeighborhoodOperatorLazy chosenOperator;

        do {

            chosenOperator = neighborhoodLazyOperatorList.get( random.nextInt(0, neighborhoodLazyOperatorList.size()) );

            SchedulePermutationSolution actualSolution = runLocalSearch(problem, chosenOperator, limitTime, startingTime, localSearchIterations, unionNeighborGenerator);

            //If it is the first time, initialize the total best neighbor variable
            if(totalBestNeighbor == null)
                totalBestNeighbor = actualSolution;

            if(actualSolution.getFitnessInfo().fitness().get("makespan") < totalBestNeighbor.getFitnessInfo().fitness().get("makespan"))
                totalBestNeighbor = actualSolution;
            else if(actualSolution.getFitnessInfo().fitness().get("makespan") > totalWorstMakespan)
                totalWorstMakespan = actualSolution.getFitnessInfo().fitness().get("makespan");

            getObserver().addReachedMakespan(actualSolution.getFitnessInfo().fitness().get("makespan"));

        } while(System.currentTimeMillis() - startingTime < limitTime);


        getObserver().setTotalBestMakespan(totalBestNeighbor.getFitnessInfo().fitness().get("makespan"));
        getObserver().setTotalWorstMakespan(totalWorstMakespan);

        getObserver().setExecutionTime(System.currentTimeMillis() - startingTime);
        getObserver().setNumberOfIterations(localSearchIterations.get());

        getObserver().executionEnded();

        return totalBestNeighbor;

    }

    private SchedulePermutationSolution runLocalSearch(
            SchedulingProblem problem,
            NeighborhoodOperatorLazy neighborhoodLazyOperator,
            long limitTimeMillis,
            long startingTimeMillis,
            AtomicInteger localSearchIterations,
            NeighborGenerator neighborGenerator
    ) {

        List<NeighborhoodOperatorLazy> neighborhoodLazyOperatorList = new ArrayList<>();

        neighborhoodLazyOperatorList.add(neighborhoodLazyOperator);

        return runLocalSearch(problem, neighborhoodLazyOperatorList, limitTimeMillis, startingTimeMillis, localSearchIterations, neighborGenerator);
    }

    private SchedulePermutationSolution runLocalSearch(
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

            maybeBetterNeighbor = selectBestNeighbor(actualSolution, neighbors, evaluator, counter);

            if (maybeBetterNeighbor.isPresent()) {
                actualSolution = maybeBetterNeighbor.get().generatedSolution();
                upgradeFound = true;
            }

            getObserver().addReachedMakespan(actualSolution.getFitnessInfo().fitness().get("makespan"));
        } while (upgradeFound && (System.currentTimeMillis() - startingTimeMillis) < limitTimeMillis);

        return actualSolution;
    }


    private Optional<GeneratedNeighbor> selectBestNeighbor(SchedulePermutationSolution actualSolution, Stream<GeneratedNeighbor> neighbors, LocalsearchEvaluator evaluator, AtomicInteger counter) {
        return neighbors
                .filter(neighbor -> {

                    counter.incrementAndGet();

                    evaluator.evaluate(actualSolution, neighbor.generatedSolution(), neighbor.movements().get(neighbor.movements().size() - 1));

                    return checkImprovement(actualSolution, neighbor);
                })
                .findFirst();
    }

    private boolean checkImprovement(SchedulePermutationSolution actualSolution, GeneratedNeighbor neighbor){
        return actualSolution.getFitnessInfo().fitness().get("makespan")
                - neighbor.generatedSolution().getFitnessInfo().fitness().get("makespan")
                > UPGRADE_THRESHOLD;
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



}
