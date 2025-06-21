package com.uniovi.sercheduler.localsearch.operator;

import com.uniovi.sercheduler.localsearch.movement.Movement;

import java.util.ArrayList;
import java.util.List;

public class NeighborhoodMulti {

    public List<GeneratedNeighbor> execute(List<GeneratedNeighbor> originalNeighbors, NeighborhoodOperatorPositional operator, int position){

        List<GeneratedNeighbor> finalNeighbors = new ArrayList<>();

        for(GeneratedNeighbor originalNeighbor : originalNeighbors){
            finalNeighbors.addAll(
                    generateFinalNeighbors(originalNeighbor, operator, position)
            );
        }

        return finalNeighbors;

    }

    public List<GeneratedNeighbor> execute(List<GeneratedNeighbor> originalNeighbors, NeighborhoodOperatorGlobal operator){

        List<GeneratedNeighbor> finalNeighbors = new ArrayList<>();

        for(GeneratedNeighbor originalNeighbor : originalNeighbors){
            finalNeighbors.addAll(
                    generateFinalNeighbors(originalNeighbor, operator)
            );
        }

        return finalNeighbors;
    }

    private List<GeneratedNeighbor> generateFinalNeighbors(GeneratedNeighbor originalNeighbor, NeighborhoodOperatorPositional operator, int position){

        List<GeneratedNeighbor> finalNeighbors = new ArrayList<>();

        List<GeneratedNeighbor> newNeighbors = operator.execute(originalNeighbor.generatedSolution(), position);

        for(GeneratedNeighbor newNeighbor : newNeighbors){

            List<Movement> movements = new ArrayList<>(originalNeighbor.movements());
            movements.addAll(newNeighbor.movements());

            finalNeighbors.add(
                    new GeneratedNeighbor(newNeighbor.generatedSolution(), movements)
            );
        }

        return finalNeighbors;
    }

    private List<GeneratedNeighbor> generateFinalNeighbors(GeneratedNeighbor originalNeighbor, NeighborhoodOperatorGlobal operator){

        List<GeneratedNeighbor> finalNeighbors = new ArrayList<>();

        List<GeneratedNeighbor> newNeighbors = operator.execute(originalNeighbor.generatedSolution());

        for(GeneratedNeighbor newNeighbor : newNeighbors){

            List<Movement> movements = new ArrayList<>(originalNeighbor.movements());
            movements.addAll(newNeighbor.movements());

            finalNeighbors.add(
                    new GeneratedNeighbor(newNeighbor.generatedSolution(), movements)
            );
        }

        return finalNeighbors;
    }
}
