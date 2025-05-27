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
        int localSearchIterations = 0;
        long startingTime = System.currentTimeMillis();

        FitnessCalculator fitnessCalculator = createFitnessCalculator(problem);
        //Generate an inicial random solution
        SchedulePermutationSolution actualSolution = createInitialSolution(problem, fitnessCalculator);
        LocalsearchEvaluator evaluator = createLocalSearchEvaluator(problem, fitnessCalculator);

        //Initialize the control variable, a variable for storing their neighbors and a
        boolean upgradeFound;
        Stream<GeneratedNeighbor> neighbors;
        Optional<GeneratedNeighbor> maybeBetterNeighbor;

        do{

            localSearchIterations++;

            upgradeFound = false;

            //Lazy computation of all the neighbors
            neighbors = generateNeighbors(neighborhoodLazyOperatorList, actualSolution);

            //As we are using laziness, for counting how many neighbours have been really computed, we cannot use
            //a variable that is no effectively final, so we use a Java class called 'AtomicInteger'
            AtomicInteger counter = new AtomicInteger();

            //Find the first neighbor that is better than the source
            maybeBetterNeighbor = selectBestNeighbor(actualSolution, neighbors, evaluator, counter);

            getObserver().addNumberOfGeneratedNeighbors( counter.get() );

            //If there is an improvement, record it and update the best neighbor
            if (maybeBetterNeighbor.isPresent()) {
                actualSolution = maybeBetterNeighbor.get().generatedSolution();
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


    public SchedulePermutationSolution execute(SchedulingProblem problem, NeighborhoodOperatorLazy neighborhoodLazyOperator, Long limitTime){

        List<NeighborhoodOperatorLazy> neighborhoodLazyOperatorList = new ArrayList<>();

        neighborhoodLazyOperatorList.add(neighborhoodLazyOperator);

        return execute(problem, neighborhoodLazyOperatorList, limitTime);

    }

    public SchedulePermutationSolution execute(SchedulingProblem problem, List<NeighborhoodOperatorLazy> neighborhoodLazyOperatorList, Long limitTime){

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
        Stream<GeneratedNeighbor> neighbors;
        Optional<GeneratedNeighbor> maybeBetterNeighborInThisStart;

        do {
            //Create a fitness calculator
            fitnessCalculator = createFitnessCalculator(problem);

            //Generate an inicial random solution
            actualSolution = createInitialSolution(problem, fitnessCalculator);

            //If it is the first time, initialize the total best neighbor variable
            if(totalBestNeighbor == null)
                totalBestNeighbor = actualSolution;

            evaluator = createLocalSearchEvaluator(problem, fitnessCalculator);

            do{

                localSearchIterations++;

                upgradeFound = false;


                //Lazy computation of all the neighbors
                neighbors = generateNeighbors(neighborhoodLazyOperatorList, actualSolution);

                //As we are using laziness, for counting how many neighbours have been really computed, we cannot use
                //a variable that is no effectively final, so we use a Java class called 'AtomicInteger'
                AtomicInteger counter = new AtomicInteger();

                //Find the first neighbor that is better than the source
                maybeBetterNeighborInThisStart = selectBestNeighbor(actualSolution, neighbors, evaluator, counter);

                //If there is an improvement, record it and update the best neighbor
                if (maybeBetterNeighborInThisStart.isPresent()) {

                    actualSolution = maybeBetterNeighborInThisStart.get().generatedSolution();
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

    public SchedulePermutationSolution executeVNS(SchedulingProblem problem, List<NeighborhoodOperatorLazy> neighborhoodLazyOperatorList, Long limitTime){

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
        Stream<GeneratedNeighbor> neighbors;
        Optional<GeneratedNeighbor> maybeBetterNeighborInThisStart;

        Random random = new Random();
        NeighborhoodOperatorLazy chosenOperator;

        do {
            //Create a fitness calculator
            fitnessCalculator = createFitnessCalculator(problem);

            //Generate an inicial random solution
            actualSolution = createInitialSolution(problem, fitnessCalculator);

            //If it is the first time, initialize the total best neighbor variable
            if(totalBestNeighbor == null)
                totalBestNeighbor = actualSolution;

            evaluator = createLocalSearchEvaluator(problem, fitnessCalculator);

            chosenOperator = neighborhoodLazyOperatorList.get( random.nextInt(0, neighborhoodLazyOperatorList.size()) );

            do{

                localSearchIterations++;

                upgradeFound = false;

                //Lazy computation of all the neighbors
                neighbors = chosenOperator.execute(actualSolution);

                //As we are using laziness, for counting how many neighbours have been really computed, we cannot use
                //a variable that is no effectively final, so we use a Java class called 'AtomicInteger'
                AtomicInteger counter = new AtomicInteger();

                //Find the first neighbor that is better than the source
                maybeBetterNeighborInThisStart = selectBestNeighbor(actualSolution, neighbors, evaluator, counter);

                //If there is an improvement, record it and update the best neighbor
                if (maybeBetterNeighborInThisStart.isPresent()) {

                    actualSolution = maybeBetterNeighborInThisStart.get().generatedSolution();
                    upgradeFound = true;

                }

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
        getObserver().setTotalBestMakespan(actualSolution.getFitnessInfo().fitness().get("makespan"));

        getObserver().executionEnded();

        return totalBestNeighbor;

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

    private <T> Stream<T> lazyRandomEvaluation(List<Supplier<Stream<T>>> streamSuppliers) {

        List<Iterator<T>> iterators = streamSuppliers.stream()
                .map(Supplier::get)
                .map(Stream::iterator)
                .toList();

        Random rand = new Random();

        Iterator<T> randomizedIterator = new Iterator<>() {
            @Override
            public boolean hasNext() {
                return iterators.stream().anyMatch(Iterator::hasNext);
            }

            @Override
            public T next() {
                if (hasNext()) {

                    List<Iterator<T>> available = iterators.stream()
                            .filter(Iterator::hasNext)
                            .toList();

                    //Just in case there is concurrency
                    if (available.isEmpty())
                        throw new NoSuchElementException();

                    Iterator<T> chosen = available.get(rand.nextInt(available.size()));

                    return chosen.next();
                } else {
                    throw new NoSuchElementException();
                }
            }
        };

        //Convert the Iterator in a Stream
        return StreamSupport.stream(
                Spliterators.spliteratorUnknownSize(randomizedIterator, Spliterator.ORDERED),
                false
        );
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

    private Stream<GeneratedNeighbor> generateNeighbors(List<NeighborhoodOperatorLazy> neighborhoodLazyOperatorList, SchedulePermutationSolution actualSolution){

        List<Supplier<Stream<GeneratedNeighbor>>> operators = new ArrayList<>();

        for(NeighborhoodOperatorLazy neighborhoodLazyOperator : neighborhoodLazyOperatorList)
            operators.add(() -> neighborhoodLazyOperator.execute(actualSolution));

        return lazyRandomEvaluation(operators);
    }

}
