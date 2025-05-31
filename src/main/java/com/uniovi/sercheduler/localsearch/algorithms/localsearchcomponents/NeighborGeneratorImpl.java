package com.uniovi.sercheduler.localsearch.algorithms.localsearchcomponents;

import com.uniovi.sercheduler.jmetal.problem.SchedulePermutationSolution;
import com.uniovi.sercheduler.localsearch.operator.GeneratedNeighbor;
import com.uniovi.sercheduler.localsearch.operator.NeighborhoodOperatorGlobal;
import com.uniovi.sercheduler.localsearch.operator.NeighborhoodOperatorLazy;

import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class NeighborGeneratorImpl implements NeighborGenerator {

    public Stream<GeneratedNeighbor> generateNeighborsLazy(List<NeighborhoodOperatorLazy> neighborhoodLazyOperatorList, SchedulePermutationSolution actualSolution){

        List<Supplier<Stream<GeneratedNeighbor>>> operators = new ArrayList<>();

        for(NeighborhoodOperatorLazy neighborhoodLazyOperator : neighborhoodLazyOperatorList)
            operators.add(() -> neighborhoodLazyOperator.execute(actualSolution));

        return shuffleStreams(operators);
    }



    public List<GeneratedNeighbor> generateNeighborsGlobal(List<NeighborhoodOperatorGlobal> neighborhoodOperatorList, SchedulePermutationSolution actualSolution){

        List<GeneratedNeighbor> neighborsList = new ArrayList<>();

        for(NeighborhoodOperatorGlobal neighborhoodOperator : neighborhoodOperatorList){
            neighborsList.addAll(
                    neighborhoodOperator.execute(actualSolution)
            );
        }

        return neighborsList;
    }

    private <T> Stream<T> shuffleStreams(List<Supplier<Stream<T>>> streamSuppliers) {

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
