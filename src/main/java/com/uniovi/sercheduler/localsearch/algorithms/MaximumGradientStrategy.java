package com.uniovi.sercheduler.localsearch.algorithms;

import com.uniovi.sercheduler.jmetal.problem.SchedulePermutationSolution;
import com.uniovi.sercheduler.jmetal.problem.SchedulingProblem;
import com.uniovi.sercheduler.localsearch.algorithms.localsearchalgorithm.LocalSearchAlgorithm;
import com.uniovi.sercheduler.localsearch.algorithms.localsearchcomponents.UpgradeAndTimeLimitTermination;
import com.uniovi.sercheduler.localsearch.algorithms.multistart.MultiStartLocalSearch;
import com.uniovi.sercheduler.localsearch.algorithms.multistartcomponents.AllStartOperatorSelector;
import com.uniovi.sercheduler.localsearch.observer.LocalSearchObserver;
import com.uniovi.sercheduler.localsearch.observer.Observer;
import com.uniovi.sercheduler.localsearch.operator.NeighborhoodOperatorGlobal;
import com.uniovi.sercheduler.localsearch.algorithms.multistartcomponents.RandomStartOperatorSelector;

import java.util.List;

public class MaximumGradientStrategy {

    private final double UPGRADE_THRESHOLD = 0.01;


    /**
     * Dados un problema y un operador de vecindad global, aplica el algoritmo LSA bajo la estrategia GD
     * y devuelve la mejor solución hallada
     *
     * @param problem el problema a resolver (principalmente el workflow, una infraestructura y la función de evaluación)
     * @param neighborhoodOperator el operador de vecindad global
     * @param observer un observer para registrar métricas del algoritmo
     * @return la mejor solución encontrada por el LSA
     */
    public SchedulePermutationSolution execute(SchedulingProblem problem, NeighborhoodOperatorGlobal neighborhoodOperator,
                                               Observer observer){

        return execute(problem, List.of(neighborhoodOperator), observer);
    }

    /**
     * Dados un problema y una lista de operadores de vecindad globales, aplica el algoritmo LSA bajo la estrategia GD
     * y devuelve la mejor solución hallada
     *
     * @param problem el problema a resolver (principalmente el workflow, una infraestructura y la función de evaluación)
     * @param neighborhoodOperatorList la lista de operadores de vecindad globales
     * @param observer un observer para registrar métricas del algoritmo
     * @return la mejor solución encontrada por el LSA
     */
    public SchedulePermutationSolution execute(SchedulingProblem problem,
                                               List<NeighborhoodOperatorGlobal> neighborhoodOperatorList,
                                               Observer observer){

        LocalSearchAlgorithm localSearchAlgorithm = new LocalSearchAlgorithm.Builder(problem).build();

        observer.startRun(localSearchAlgorithm.startTimeCounter());

        SchedulePermutationSolution achievedSolution =
                localSearchAlgorithm.runLocalSearchGlobal(neighborhoodOperatorList, observer);

        observer.endRun();

        return achievedSolution;
    }

    /**
     * Dados un problema y un operador de vecindad global, aplica un algoritmo multi-arranque ejecutando
     * el algoritmo LSA bajo la estrategia GD tantas veces como le sea posible en el tiempo especificado
     * y devuelve la mejor solución hallada
     *
     * @param problem el problema a resolver (principalmente el workflow, una infraestructura y la función de evaluación)
     * @param neighborhoodOperator el operador de vecindad global
     * @param limitTime el tiempo límite
     * @param observer un observer para registrar métricas del algoritmo
     * @return la mejor solución encontrada entre todos los arranques
     */
    public SchedulePermutationSolution execute(SchedulingProblem problem, NeighborhoodOperatorGlobal neighborhoodOperator,
                                               Long limitTime, Observer observer){

        return execute(problem, List.of(neighborhoodOperator), limitTime, observer);
    }

    /**
     * Dados un problema y una lista de operadores de vecindad globales, aplica un algoritmo multi-arranque ejecutando
     * el algoritmo LSA bajo la estrategia GD tantas veces como le sea posible en el tiempo especificado
     * y devuelve la mejor solución hallada
     *
     * @param problem el problema a resolver (principalmente el workflow, una infraestructura y la función de evaluación)
     * @param neighborhoodOperatorList la lista de operadores de vecindad globales
     * @param limitTime el tiempo límite
     * @param observer un observer para registrar métricas del algoritmo
     * @return la mejor solución encontrada entre todos los arranques
     */
    public SchedulePermutationSolution execute(SchedulingProblem problem,
                                               List<NeighborhoodOperatorGlobal> neighborhoodOperatorList, Long limitTime,
                                               Observer observer){

        LocalSearchAlgorithm localSearchAlgorithm = new LocalSearchAlgorithm.Builder(problem)
                .terminationCriterion(new UpgradeAndTimeLimitTermination(limitTime))
                .build();

        MultiStartLocalSearch multiStartLocalSearch = new MultiStartLocalSearch(new AllStartOperatorSelector());

        return multiStartLocalSearch.executeGlobal(localSearchAlgorithm, neighborhoodOperatorList, limitTime, observer);

    }

    /**
     * Dados un problema y una lista de operadores de vecindad globales, aplica un algoritmo multi-arranque ejecutando
     * el algoritmo LSA bajo la estrategia GD, escogiendo un operador aleatoriamente de la lista, tantas veces como le
     * sea posible en el tiempo especificado y devuelve la mejor solución hallada
     *
     * @param problem el problema a resolver (principalmente el workflow, una infraestructura y la función de evaluación)
     * @param neighborhoodOperatorList el operador de vecindad global
     * @param limitTime el tiempo límite
     * @param observer un observer para registrar métricas del algoritmo
     * @return la mejor solución encontrada entre todos los arranques
     */
    public SchedulePermutationSolution executeVNS(SchedulingProblem problem,
                                                  List<NeighborhoodOperatorGlobal> neighborhoodOperatorList,
                                                  Long limitTime, Observer observer){

        LocalSearchAlgorithm localSearchAlgorithm = new LocalSearchAlgorithm.Builder(problem)
                .terminationCriterion(new UpgradeAndTimeLimitTermination(limitTime))
                .build();

        MultiStartLocalSearch multiStartLocalSearch = new MultiStartLocalSearch(new RandomStartOperatorSelector());

        return multiStartLocalSearch.executeGlobal(localSearchAlgorithm, neighborhoodOperatorList, limitTime, observer);
    }


}
