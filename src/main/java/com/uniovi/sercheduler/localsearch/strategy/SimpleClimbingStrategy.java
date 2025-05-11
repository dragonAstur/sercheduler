package com.uniovi.sercheduler.localsearch.strategy;

import com.uniovi.sercheduler.jmetal.problem.SchedulePermutationSolution;
import com.uniovi.sercheduler.jmetal.problem.SchedulingProblem;
import com.uniovi.sercheduler.localsearch.evaluator.LocalsearchEvaluator;
import com.uniovi.sercheduler.localsearch.observer.NeighborhoodObserver;
import com.uniovi.sercheduler.localsearch.operator.GeneratedNeighbor;
import com.uniovi.sercheduler.localsearch.operator.NeighborhoodOperatorLazy;
import com.uniovi.sercheduler.service.FitnessCalculatorSimple;
import com.uniovi.sercheduler.service.FitnessInfo;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class SimpleClimbingStrategy extends AbstractStrategy {

    public SimpleClimbingStrategy(NeighborhoodObserver observer) {
        super(observer);
    }

    public SchedulePermutationSolution execute(SchedulingProblem problem, NeighborhoodOperatorLazy neighborhoodLazyOperator){

        getObserver().executionStarted();
        int localSearchIterations = 0;
        long startingTime = System.currentTimeMillis();

        //Generate an inicial random solution
        SchedulePermutationSolution actualSolution = problem.createSolution();

        //Evaluate this new created solution (this step is skipped in the pseudocode)
        FitnessCalculatorSimple fitnessCalculator = new FitnessCalculatorSimple(problem.getInstanceData());
        FitnessInfo fitnessInfo = fitnessCalculator.calculateFitness(actualSolution);
        actualSolution.setFitnessInfo(fitnessInfo);

        //Initialize the control variable, a variable for storing their neighbors and a
        boolean upgradeFound;
        Stream<GeneratedNeighbor> neighbors;
        Optional<GeneratedNeighbor> maybeBetterNeighbor;
        LocalsearchEvaluator evaluator = new LocalsearchEvaluator(fitnessCalculator.getComputationMatrix(), fitnessCalculator.getNetworkMatrix(), problem.getInstanceData());


        do{

            localSearchIterations++;

            upgradeFound = false;

            //Lazy computation of all the neighbors
            neighbors = neighborhoodLazyOperator.execute(actualSolution);

            final SchedulePermutationSolution finalActualSolution = actualSolution; //This is just for functional programming technical problems

            //As we are using laziness, for counting how many neighbours have been really computed, we cannot use
            //a variable that is no effectively final, so we use a Java class called 'AtomicInteger'
            AtomicInteger counter = new AtomicInteger();

            //Find the first neighbor that is better than the source
            maybeBetterNeighbor = neighbors
                    .filter(neighbor -> {

                        counter.incrementAndGet();

                        evaluator.evaluate(finalActualSolution, neighbor.generatedSolution(), neighbor.movements().get(neighbor.movements().size() - 1));

                        return neighbor.generatedSolution().getFitnessInfo().fitness().get("makespan") <
                                finalActualSolution.getFitnessInfo().fitness().get("makespan");
                    })
                    .findFirst();   //This breaks the laziness

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
        getObserver().setTotalReachedMakespan(actualSolution.getFitnessInfo().fitness().get("makespan"));

        getObserver().executionEnded();

        return actualSolution;

    }

    public SchedulePermutationSolution execute(SchedulingProblem problem, List<NeighborhoodOperatorLazy> neighborhoodLazyOperatorList){

        getObserver().executionStarted();
        int localSearchIterations = 0;
        long startingTime = System.currentTimeMillis();

        //Generate an inicial random solution
        SchedulePermutationSolution actualSolution = problem.createSolution();

        //Evaluate this new created solution (this step is skipped in the pseudocode)
        FitnessCalculatorSimple fitnessCalculator = new FitnessCalculatorSimple(problem.getInstanceData());
        FitnessInfo fitnessInfo = fitnessCalculator.calculateFitness(actualSolution);
        actualSolution.setFitnessInfo(fitnessInfo);

        //Initialize the control variable, a variable for storing their neighbors and a
        boolean upgradeFound;
        Stream<GeneratedNeighbor> neighbors;
        Optional<GeneratedNeighbor> maybeBetterNeighbor;
        LocalsearchEvaluator evaluator = new LocalsearchEvaluator(fitnessCalculator.getComputationMatrix(), fitnessCalculator.getNetworkMatrix(), problem.getInstanceData());
        List<Supplier<Stream<GeneratedNeighbor>>> operators;

        do{

            localSearchIterations++;

            upgradeFound = false;

            final SchedulePermutationSolution finalActualSolution = actualSolution; //This is just for functional programming technical problems

            //Lazy computation of all the neighbors
            operators = new ArrayList<>();

            for(NeighborhoodOperatorLazy neighborhoodLazyOperator : neighborhoodLazyOperatorList)
                operators.add(() -> neighborhoodLazyOperator.execute(finalActualSolution));

            neighbors = lazyRandomEvaluation(operators);

            //As we are using laziness, for counting how many neighbours have been really computed, we cannot use
            //a variable that is no effectively final, so we use a Java class called 'AtomicInteger'
            AtomicInteger counter = new AtomicInteger();

            //Find the first neighbor that is better than the source
            maybeBetterNeighbor = neighbors
                    .filter(neighbor -> {

                        counter.incrementAndGet();

                        evaluator.evaluate(finalActualSolution, neighbor.generatedSolution(), neighbor.movements().get(neighbor.movements().size() - 1));

                        return neighbor.generatedSolution().getFitnessInfo().fitness().get("makespan") <
                                finalActualSolution.getFitnessInfo().fitness().get("makespan");
                    })
                    .findFirst();   //This breaks the laziness

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
        getObserver().setTotalReachedMakespan(actualSolution.getFitnessInfo().fitness().get("makespan"));

        getObserver().executionEnded();

        return actualSolution;

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

}
