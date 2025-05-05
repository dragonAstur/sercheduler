package com.uniovi.sercheduler.localsearch.command;

import com.uniovi.sercheduler.dao.Objective;
import com.uniovi.sercheduler.jmetal.problem.SchedulingProblem;
import com.uniovi.sercheduler.localsearch.export.XLSXExporter;
import com.uniovi.sercheduler.localsearch.observer.NeighborhoodObserver;
import com.uniovi.sercheduler.localsearch.operator.NeighborhoodOperatorGlobal;
import com.uniovi.sercheduler.localsearch.operator.NeighborhoodOperatorLazy;
import com.uniovi.sercheduler.localsearch.operator.NeighborhoodSwapGlobal;
import com.uniovi.sercheduler.localsearch.operator.NeighborhoodSwapLazy;
import com.uniovi.sercheduler.localsearch.strategy.MaximumGradientStrategy;
import com.uniovi.sercheduler.localsearch.strategy.SimpleClimbingStrategy;

import java.io.File;
import java.util.List;

public class LocalSearchRunnable {

    public static final String WORFLOWFILE = "src/test/resources/montage.json";
    public static final String HOSTSFILE = "src/test/resources/hosts_test.json";



    public static void main(String[] args) {

        List<Objective> objectives = List.of(Objective.MAKESPAN, Objective.ENERGY);

        long seed = System.nanoTime();

        SchedulingProblem problem =
                new SchedulingProblem(
                        new File(WORFLOWFILE),
                        new File(HOSTSFILE),
                        "441Gf",
                        "simple",
                        seed,
                        objectives,
                        Objective.MAKESPAN.objectiveName);

        System.out.println("\n\nMaximum Gradient strategy\n\n");

        NeighborhoodObserver observer = new NeighborhoodObserver("DHC");

        MaximumGradientStrategy maximumGradientStrategy = new MaximumGradientStrategy(observer);

        //Here you can change the operator
        NeighborhoodOperatorGlobal globalOperator = new NeighborhoodSwapGlobal();

        for(int i = 0; i < 30; i++)
            maximumGradientStrategy.execute(problem, globalOperator);

        System.out.println(observer);

        XLSXExporter.createWorkbook("local_search_results");
        XLSXExporter.appendWorkbook(observer, "local_search_results");

        System.out.println("\n\nSimple Climbing strategy\n\n");

        observer = new NeighborhoodObserver("HC");

        SimpleClimbingStrategy simpleClimbingStrategy = new SimpleClimbingStrategy(observer);

        //Here you can change the operator
        NeighborhoodOperatorLazy lazyOperator = new NeighborhoodSwapLazy();

        for(int i = 0; i < 30; i++)
            simpleClimbingStrategy.execute(problem, lazyOperator);

        System.out.println(observer);

        XLSXExporter.appendWorkbook(observer, "local_search_results");

    }
}
