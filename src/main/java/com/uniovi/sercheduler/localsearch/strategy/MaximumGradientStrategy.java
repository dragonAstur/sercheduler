package com.uniovi.sercheduler.localsearch.strategy;

import com.uniovi.sercheduler.jmetal.problem.SchedulePermutationSolution;
import com.uniovi.sercheduler.jmetal.problem.SchedulingProblem;
import com.uniovi.sercheduler.localsearch.evaluator.LocalsearchEvaluator;
import com.uniovi.sercheduler.localsearch.observer.NeighborhoodObserver;
import com.uniovi.sercheduler.localsearch.operator.GeneratedNeighbor;
import com.uniovi.sercheduler.localsearch.operator.NeighborhoodOperatorGlobal;
import com.uniovi.sercheduler.service.FitnessCalculator;
import com.uniovi.sercheduler.service.FitnessCalculatorSimple;
import com.uniovi.sercheduler.service.FitnessInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MaximumGradientStrategy extends AbstractStrategy {

    private final double UPGRADE_THRESHOLD = 0.01;

    public MaximumGradientStrategy(NeighborhoodObserver observer) {
        super(observer);
    }

    public SchedulePermutationSolution execute(SchedulingProblem problem, NeighborhoodOperatorGlobal neighborhoodOperator){

        List<NeighborhoodOperatorGlobal> neighborhoodOperatorList = new ArrayList<>();

        neighborhoodOperatorList.add(neighborhoodOperator);

        return execute(problem, neighborhoodOperatorList);
    }

    public SchedulePermutationSolution execute(SchedulingProblem problem, List<NeighborhoodOperatorGlobal> neighborhoodOperatorList){

        getObserver().executionStarted();
        int localSearchIterations = 0;
        long startingTime = System.currentTimeMillis();

        FitnessCalculator fitnessCalculator = createFitnessCalculator(problem);
        //Generate an inicial random solution
        SchedulePermutationSolution actualSolution = createInitialSolution(problem, fitnessCalculator);
        LocalsearchEvaluator evaluator = createLocalSearchEvaluator(problem, fitnessCalculator);

        //Initialize the control variable, a variable for storing their neighbors and an incremental evaluator
        boolean upgradeFound;
        List<GeneratedNeighbor> neighborsList;
        SchedulePermutationSolution bestNeighbor;

        do{

            localSearchIterations++;

            upgradeFound = false;

            //Generate new neighbors
            neighborsList = generateNeighbors(neighborhoodOperatorList, actualSolution);

            getObserver().addNumberOfGeneratedNeighbors( neighborsList.size() );

            //Take the best one
            bestNeighbor = selectBestNeighbor(actualSolution, neighborsList, evaluator);

            //If there is an improvement, record it and update the best neighbor
            if(checkImprovement(actualSolution, bestNeighbor)){
                actualSolution = bestNeighbor;
                upgradeFound = true;
            }

            getObserver().addReachedMakespan(actualSolution.getFitnessInfo().fitness().get("makespan"));

        } while(upgradeFound);

        getObserver().setExecutionTime(System.currentTimeMillis() - startingTime);
        getObserver().setNumberOfIterations(localSearchIterations);
        getObserver().setTotalBestMakespan(actualSolution.getFitnessInfo().fitness().get("makespan"));

        getObserver().executionEnded();

        return actualSolution;
    }

    public SchedulePermutationSolution execute(SchedulingProblem problem, NeighborhoodOperatorGlobal neighborhoodOperator, Long limitTime){

        List<NeighborhoodOperatorGlobal> neighborhoodOperatorList = new ArrayList<>();

        neighborhoodOperatorList.add(neighborhoodOperator);

        return execute(problem, neighborhoodOperatorList, limitTime);
    }

    public SchedulePermutationSolution execute(SchedulingProblem problem, List<NeighborhoodOperatorGlobal> neighborhoodOperatorList, Long limitTime){

        getObserver().executionStarted();
        int localSearchIterations = 0;
        long startingTime = System.currentTimeMillis();

        SchedulePermutationSolution totalBestNeighbor = null;
        double totalWorstMakespan = -1;

        FitnessCalculator fitnessCalculator;
        SchedulePermutationSolution actualSolution;
        LocalsearchEvaluator evaluator;

        //Initialize the control variable, a variable for storing their neighbors and a
        boolean upgradeFound;
        List<GeneratedNeighbor> neighborsList;
        SchedulePermutationSolution bestNeighborInThisStart;

        do {

            //Create a fitness calculator
            fitnessCalculator = createFitnessCalculator(problem);

            //Generate an inicial random solution
            actualSolution = createInitialSolution(problem, fitnessCalculator);

            //If it is the first time, initialize the total best neighbor variable
            if(totalBestNeighbor == null)
                totalBestNeighbor = actualSolution;

            evaluator = createLocalSearchEvaluator(problem, fitnessCalculator);

            do {

                localSearchIterations++;

                upgradeFound = false;

                //Generate new neighbors
                neighborsList = generateNeighbors(neighborhoodOperatorList, actualSolution);

                getObserver().addNumberOfGeneratedNeighbors( neighborsList.size() );

                //Take the best one
                bestNeighborInThisStart = selectBestNeighbor(actualSolution, neighborsList, evaluator);

                //If there is an improvement, record it and update the best neighbor in this start
                if (checkImprovement(actualSolution, bestNeighborInThisStart)) {

                    actualSolution = bestNeighborInThisStart;
                    upgradeFound = true;

                }

                getObserver().addReachedMakespan(actualSolution.getFitnessInfo().fitness().get("makespan"));

            } while(upgradeFound && System.currentTimeMillis() - startingTime < limitTime);

            if(actualSolution.getFitnessInfo().fitness().get("makespan") < totalBestNeighbor.getFitnessInfo().fitness().get("makespan"))
                totalBestNeighbor = actualSolution;
            else if(actualSolution.getFitnessInfo().fitness().get("makespan") > totalWorstMakespan)
                totalWorstMakespan = actualSolution.getFitnessInfo().fitness().get("makespan");

        } while(System.currentTimeMillis() - startingTime < limitTime);

        getObserver().setTotalBestMakespan(totalBestNeighbor.getFitnessInfo().fitness().get("makespan"));
        getObserver().setTotalWorstMakespan(totalWorstMakespan);

        getObserver().setExecutionTime(System.currentTimeMillis() - startingTime);
        getObserver().setNumberOfIterations(localSearchIterations);
        getObserver().setTotalBestMakespan(actualSolution.getFitnessInfo().fitness().get("makespan"));

        getObserver().executionEnded();

        return totalBestNeighbor;
    }

    public SchedulePermutationSolution executeVNS(SchedulingProblem problem, List<NeighborhoodOperatorGlobal> neighborhoodOperatorList, Long limitTime){

        getObserver().executionStarted();
        int localSearchIterations = 0;
        long startingTime = System.currentTimeMillis();

        SchedulePermutationSolution totalBestNeighbor = null;
        double totalWorstMakespan = -1;

        FitnessCalculator fitnessCalculator;
        SchedulePermutationSolution actualSolution;
        LocalsearchEvaluator evaluator;

        //Initialize the control variable, a variable for storing their neighbors and a
        boolean upgradeFound;
        List<GeneratedNeighbor> neighborsList;
        SchedulePermutationSolution bestNeighborInThisStart;

        Random random = new Random();
        NeighborhoodOperatorGlobal chosenOperator;

        do {

            //Create a fitness calculator
            fitnessCalculator = createFitnessCalculator(problem);

            //Generate an inicial random solution
            actualSolution = createInitialSolution(problem, fitnessCalculator);

            //If it is the first time, initialize the total best neighbor variable
            if(totalBestNeighbor == null)
                totalBestNeighbor = actualSolution;

            evaluator = createLocalSearchEvaluator(problem, fitnessCalculator);

            chosenOperator = neighborhoodOperatorList.get( random.nextInt(0, neighborhoodOperatorList.size()) );

            do {

                localSearchIterations++;

                upgradeFound = false;

                //Generate new neighbors
                neighborsList = chosenOperator.execute(actualSolution);

                getObserver().addNumberOfGeneratedNeighbors( neighborsList.size() );

                //Take the best one
                bestNeighborInThisStart = selectBestNeighbor(actualSolution, neighborsList, evaluator);

                //If there is an improvement, record it and update the best neighbor in this start
                if (checkImprovement(actualSolution, bestNeighborInThisStart)) {

                    actualSolution = bestNeighborInThisStart;
                    upgradeFound = true;

                }

                getObserver().addReachedMakespan(actualSolution.getFitnessInfo().fitness().get("makespan"));

            } while(upgradeFound && System.currentTimeMillis() - startingTime < limitTime);

            if(actualSolution.getFitnessInfo().fitness().get("makespan") < totalBestNeighbor.getFitnessInfo().fitness().get("makespan"))
                totalBestNeighbor = actualSolution;
            else if(actualSolution.getFitnessInfo().fitness().get("makespan") > totalWorstMakespan)
                totalWorstMakespan = actualSolution.getFitnessInfo().fitness().get("makespan");

            getObserver().addReachedMakespan(actualSolution.getFitnessInfo().fitness().get("makespan"));

        } while(System.currentTimeMillis() - startingTime < limitTime);

        getObserver().setTotalBestMakespan(totalBestNeighbor.getFitnessInfo().fitness().get("makespan"));
        getObserver().setTotalWorstMakespan(totalWorstMakespan);

        getObserver().setExecutionTime(System.currentTimeMillis() - startingTime);
        getObserver().setNumberOfIterations(localSearchIterations);

        getObserver().executionEnded();

        return totalBestNeighbor;
    }

    private SchedulePermutationSolution selectBestNeighbor(SchedulePermutationSolution originalSolution, List<GeneratedNeighbor> neighborsList, LocalsearchEvaluator evaluator){

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

    private List<GeneratedNeighbor> generateNeighbors(List<NeighborhoodOperatorGlobal> neighborhoodOperatorList, SchedulePermutationSolution actualSolution){

        List<GeneratedNeighbor> neighborsList = new ArrayList<>();

        for(NeighborhoodOperatorGlobal neighborhoodOperator : neighborhoodOperatorList){
            neighborsList.addAll(
                    neighborhoodOperator.execute(actualSolution)
            );
        }

        return neighborsList;
    }

    private boolean checkImprovement(SchedulePermutationSolution actualSolution, SchedulePermutationSolution bestNeighbor){
        return actualSolution.getFitnessInfo().fitness().get("makespan") - bestNeighbor.getFitnessInfo().fitness().get("makespan") > UPGRADE_THRESHOLD;
    }
}
