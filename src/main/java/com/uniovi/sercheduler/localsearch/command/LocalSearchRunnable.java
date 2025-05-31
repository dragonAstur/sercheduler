package com.uniovi.sercheduler.localsearch.command;

import com.uniovi.sercheduler.dao.Objective;
import com.uniovi.sercheduler.jmetal.problem.SchedulingProblem;
import com.uniovi.sercheduler.localsearch.export.CSVExporter;
import com.uniovi.sercheduler.localsearch.export.XLSXExporter;
import com.uniovi.sercheduler.localsearch.export.XLSXTableExporter;
import com.uniovi.sercheduler.localsearch.observer.NeighborhoodObserver;
import com.uniovi.sercheduler.localsearch.operator.*;
import com.uniovi.sercheduler.localsearch.algorithms.MaximumGradientStrategy;
import com.uniovi.sercheduler.localsearch.algorithms.SimpleClimbingStrategy;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class LocalSearchRunnable {

    public static final String WORFLOWFILE = "src/test/resources/soykb.json";
    public static final String HOSTSFILE = "src/test/resources/extreme/hosts-4.json";



    public static void main(String[] args) {

        List<Objective> objectives = List.of(Objective.MAKESPAN, Objective.ENERGY);

        String[] workflowPath = WORFLOWFILE.split("/");
        String workflowName = workflowPath[workflowPath.length-1].split("\\.")[0];

        String[] hostsPath = HOSTSFILE.split("/");
        String hostsName = hostsPath[hostsPath.length-1].split("\\.")[0];

        String instanceName = workflowName + "_" + hostsName;

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

        operatorsExperiment(instanceName + "_v2", problem, 1000L);
    }

    private static void StrategiesExperiment(String instanceName, SchedulingProblem problem) {

        System.out.println("\n\nMaximum Gradient strategy\n\n");

        //Here you can change the operator
        NeighborhoodOperatorGlobal globalOperator = new NeighborhoodSwapHostGlobal();

        NeighborhoodObserver observer = new NeighborhoodObserver("DHC", instanceName);

        MaximumGradientStrategy maximumGradientStrategy = new MaximumGradientStrategy(observer);

        for(int i = 0; i < 30; i++)
            maximumGradientStrategy.execute(problem, globalOperator);

        System.out.println(observer);

        XLSXExporter.createWorkbook("local_search_results_" + globalOperator.getName());
        XLSXExporter.appendWorkbook(observer, "local_search_results_" + globalOperator.getName());

        CSVExporter.createCSV("local_search_results_" + globalOperator.getName());
        CSVExporter.appendCSV(observer, "local_search_results_" + globalOperator.getName());




        System.out.println("\n\nSimple Climbing strategy\n\n");

        //Here you can change the operator
        NeighborhoodOperatorLazy lazyOperator = new NeighborhoodSwapHostLazy();

        observer = new NeighborhoodObserver("HC", instanceName);

        SimpleClimbingStrategy simpleClimbingStrategy = new SimpleClimbingStrategy(observer);

        for(int i = 0; i < 30; i++)
            simpleClimbingStrategy.execute(problem, lazyOperator);

        System.out.println(observer);

        XLSXExporter.appendWorkbook(observer, "local_search_results_" + globalOperator.getName());

        CSVExporter.appendCSV(observer, "local_search_results_" + globalOperator.getName());
    }

    private static void operatorsExperiment(String instanceName, SchedulingProblem problem, long timeLimit){

        NeighborhoodOperatorGlobal globalOperator;
        NeighborhoodOperatorLazy lazyOperator;
        NeighborhoodObserver observer;
        MaximumGradientStrategy maximumGradientStrategy;
        SimpleClimbingStrategy simpleClimbingStrategy;
        List<NeighborhoodOperatorGlobal> globalOperatorList;
        List<NeighborhoodOperatorLazy> lazyOperatorList;

        List<Double> avgMakespanList = new ArrayList<>();
        List<Double> bestKnownCostList = new ArrayList<>();

        final String fileName = "operators_experiment_results";

        /*XLSXTableExporter.createWorkbook(fileName);*/
        XLSXTableExporter.createInstanceSheet(fileName, instanceName);




        System.out.println("\n\nDHC | N1 operator\n\n");

        globalOperator = new NeighborhoodChangeHostGlobal(problem.getInstanceData());
        observer = new NeighborhoodObserver("DHC", instanceName);
        maximumGradientStrategy = new MaximumGradientStrategy(observer);

        for(int i = 0; i < 30; i++) {
            System.out.println("Execution number " + (i+1) + "\n");
            maximumGradientStrategy.execute(problem, globalOperator, timeLimit);
        }

        avgMakespanList.add(observer.avgReachedCost());
        bestKnownCostList.add(observer.getBestReachedMakespan());

        XLSXTableExporter.appendInstanceSheet(fileName, instanceName, observer, "N1");




        System.out.println("\n\nDHC | N2 operator\n\n");

        globalOperator = new NeighborhoodInsertionGlobal();
        observer = new NeighborhoodObserver("DHC", instanceName);
        maximumGradientStrategy = new MaximumGradientStrategy(observer);

        for(int i = 0; i < 30; i++) {
            System.out.println("Execution number " + (i+1) + "\n");
            maximumGradientStrategy.execute(problem, globalOperator, timeLimit);
        }

        avgMakespanList.add(observer.avgReachedCost());
        bestKnownCostList.add(observer.getBestReachedMakespan());

        XLSXTableExporter.appendInstanceSheet(fileName, instanceName, observer, "N2");




        System.out.println("\n\nDHC | N3 operator\n\n");

        globalOperator = new NeighborhoodSwapGlobal();
        observer = new NeighborhoodObserver("DHC", instanceName);
        maximumGradientStrategy = new MaximumGradientStrategy(observer);

        for(int i = 0; i < 30; i++) {
            System.out.println("Execution number " + (i+1) + "\n");
            maximumGradientStrategy.execute(problem, globalOperator, timeLimit);
        }

        avgMakespanList.add(observer.avgReachedCost());
        bestKnownCostList.add(observer.getBestReachedMakespan());

        XLSXTableExporter.appendInstanceSheet(fileName, instanceName, observer, "N3");




        System.out.println("\n\nDHC | N4 operator\n\n");

        globalOperator = new NeighborhoodSwapHostGlobal();
        observer = new NeighborhoodObserver("DHC", instanceName);
        maximumGradientStrategy = new MaximumGradientStrategy(observer);

        for(int i = 0; i < 30; i++) {
            System.out.println("Execution number " + (i+1) + "\n");
            maximumGradientStrategy.execute(problem, globalOperator, timeLimit);
        }

        avgMakespanList.add(observer.avgReachedCost());
        bestKnownCostList.add(observer.getBestReachedMakespan());

        XLSXTableExporter.appendInstanceSheet(fileName, instanceName, observer, "N4");




        System.out.println("\n\nDHC | N1 U N2 operator\n\n");

        globalOperatorList = List.of(
                new NeighborhoodChangeHostGlobal(problem.getInstanceData()),
                new NeighborhoodInsertionGlobal()
        );
        observer = new NeighborhoodObserver("DHC", instanceName);
        maximumGradientStrategy = new MaximumGradientStrategy(observer);

        for(int i = 0; i < 30; i++) {
            System.out.println("Execution number " + (i+1) + "\n");
            maximumGradientStrategy.execute(problem, globalOperatorList, timeLimit);
        }

        avgMakespanList.add(observer.avgReachedCost());
        bestKnownCostList.add(observer.getBestReachedMakespan());

        XLSXTableExporter.appendInstanceSheet(fileName, instanceName, observer, "N1 U N2");




        System.out.println("\n\nDHC | N1 U N3 operator\n\n");

        globalOperatorList = List.of(
                new NeighborhoodChangeHostGlobal(problem.getInstanceData()),
                new NeighborhoodSwapGlobal()
        );
        observer = new NeighborhoodObserver("DHC", instanceName);
        maximumGradientStrategy = new MaximumGradientStrategy(observer);

        for(int i = 0; i < 30; i++) {
            System.out.println("Execution number " + (i+1) + "\n");
            maximumGradientStrategy.execute(problem, globalOperatorList, timeLimit);
        }

        avgMakespanList.add(observer.avgReachedCost());
        bestKnownCostList.add(observer.getBestReachedMakespan());

        XLSXTableExporter.appendInstanceSheet(fileName, instanceName, observer, "N1 U N3");




        System.out.println("\n\nDHC | N1 U N4 operator\n\n");

        globalOperatorList = List.of(
                new NeighborhoodChangeHostGlobal(problem.getInstanceData()),
                new NeighborhoodSwapHostGlobal()
        );
        observer = new NeighborhoodObserver("DHC", instanceName);
        maximumGradientStrategy = new MaximumGradientStrategy(observer);

        for(int i = 0; i < 30; i++) {
            System.out.println("Execution number " + (i+1) + "\n");
            maximumGradientStrategy.execute(problem, globalOperatorList, timeLimit);
        }

        avgMakespanList.add(observer.avgReachedCost());
        bestKnownCostList.add(observer.getBestReachedMakespan());

        XLSXTableExporter.appendInstanceSheet(fileName, instanceName, observer, "N1 U N4");




        System.out.println("\n\nDHC | N2 U N3 operator\n\n");

        globalOperatorList = List.of(
                new NeighborhoodInsertionGlobal(),
                new NeighborhoodSwapGlobal()
        );
        observer = new NeighborhoodObserver("DHC", instanceName);
        maximumGradientStrategy = new MaximumGradientStrategy(observer);

        for(int i = 0; i < 30; i++) {
            System.out.println("Execution number " + (i+1) + "\n");
            maximumGradientStrategy.execute(problem, globalOperatorList, timeLimit);
        }

        avgMakespanList.add(observer.avgReachedCost());
        bestKnownCostList.add(observer.getBestReachedMakespan());

        XLSXTableExporter.appendInstanceSheet(fileName, instanceName, observer, "N2 U N3");



        System.out.println("\n\nDHC | N2 U N4 operator\n\n");

        globalOperatorList = List.of(
                new NeighborhoodInsertionGlobal(),
                new NeighborhoodSwapHostGlobal()
        );
        observer = new NeighborhoodObserver("DHC", instanceName);
        maximumGradientStrategy = new MaximumGradientStrategy(observer);

        for(int i = 0; i < 30; i++) {
            System.out.println("Execution number " + (i+1) + "\n");
            maximumGradientStrategy.execute(problem, globalOperatorList, timeLimit);
        }

        avgMakespanList.add(observer.avgReachedCost());
        bestKnownCostList.add(observer.getBestReachedMakespan());

        XLSXTableExporter.appendInstanceSheet(fileName, instanceName, observer, "N2 U N4");




        System.out.println("\n\nDHC | N3 U N4 operator\n\n");

        globalOperatorList = List.of(
                new NeighborhoodSwapGlobal(),
                new NeighborhoodSwapHostGlobal()
        );
        observer = new NeighborhoodObserver("DHC", instanceName);
        maximumGradientStrategy = new MaximumGradientStrategy(observer);

        for(int i = 0; i < 30; i++) {
            System.out.println("Execution number " + (i+1) + "\n");
            maximumGradientStrategy.execute(problem, globalOperatorList, timeLimit);
        }

        avgMakespanList.add(observer.avgReachedCost());
        bestKnownCostList.add(observer.getBestReachedMakespan());

        XLSXTableExporter.appendInstanceSheet(fileName, instanceName, observer, "N3 U N4");




        System.out.println("\n\nDHC | N1 U N2 U N3 operator\n\n");

        globalOperatorList = List.of(
                new NeighborhoodChangeHostGlobal(problem.getInstanceData()),
                new NeighborhoodInsertionGlobal(),
                new NeighborhoodSwapGlobal()
        );
        observer = new NeighborhoodObserver("DHC", instanceName);
        maximumGradientStrategy = new MaximumGradientStrategy(observer);

        for(int i = 0; i < 30; i++) {
            System.out.println("Execution number " + (i+1) + "\n");
            maximumGradientStrategy.execute(problem, globalOperatorList, timeLimit);
        }

        avgMakespanList.add(observer.avgReachedCost());
        bestKnownCostList.add(observer.getBestReachedMakespan());

        XLSXTableExporter.appendInstanceSheet(fileName, instanceName, observer, "N1 U N2 U N3");




        System.out.println("\n\nDHC | N1 U N2 U N4 operator\n\n");

        globalOperatorList = List.of(
                new NeighborhoodChangeHostGlobal(problem.getInstanceData()),
                new NeighborhoodInsertionGlobal(),
                new NeighborhoodSwapHostGlobal()
        );
        observer = new NeighborhoodObserver("DHC", instanceName);
        maximumGradientStrategy = new MaximumGradientStrategy(observer);

        for(int i = 0; i < 30; i++) {
            System.out.println("Execution number " + (i+1) + "\n");
            maximumGradientStrategy.execute(problem, globalOperatorList, timeLimit);
        }

        avgMakespanList.add(observer.avgReachedCost());
        bestKnownCostList.add(observer.getBestReachedMakespan());

        XLSXTableExporter.appendInstanceSheet(fileName, instanceName, observer, "N1 U N2 U N4");




        System.out.println("\n\nDHC | N1 U N3 U N4 operator\n\n");

        globalOperatorList = List.of(
                new NeighborhoodChangeHostGlobal(problem.getInstanceData()),
                new NeighborhoodSwapGlobal(),
                new NeighborhoodSwapHostGlobal()
        );
        observer = new NeighborhoodObserver("DHC", instanceName);
        maximumGradientStrategy = new MaximumGradientStrategy(observer);

        for(int i = 0; i < 30; i++) {
            System.out.println("Execution number " + (i+1) + "\n");
            maximumGradientStrategy.execute(problem, globalOperatorList, timeLimit);
        }

        avgMakespanList.add(observer.avgReachedCost());
        bestKnownCostList.add(observer.getBestReachedMakespan());

        XLSXTableExporter.appendInstanceSheet(fileName, instanceName, observer, "N1 U N3 U N4");



        System.out.println("\n\nDHC | N2 U N3 U N4 operator\n\n");

        globalOperatorList = List.of(
                new NeighborhoodInsertionGlobal(),
                new NeighborhoodSwapGlobal(),
                new NeighborhoodSwapHostGlobal()
        );
        observer = new NeighborhoodObserver("DHC", instanceName);
        maximumGradientStrategy = new MaximumGradientStrategy(observer);

        for(int i = 0; i < 30; i++) {
            System.out.println("Execution number " + (i+1) + "\n");
            maximumGradientStrategy.execute(problem, globalOperatorList, timeLimit);
        }

        avgMakespanList.add(observer.avgReachedCost());
        bestKnownCostList.add(observer.getBestReachedMakespan());

        XLSXTableExporter.appendInstanceSheet(fileName, instanceName, observer, "N2 U N3 U N4");




        System.out.println("\n\nDHC | N1 U N2 U N3 U N4 operator\n\n");

        globalOperatorList = List.of(
                new NeighborhoodChangeHostGlobal(problem.getInstanceData()),
                new NeighborhoodInsertionGlobal(),
                new NeighborhoodSwapGlobal(),
                new NeighborhoodSwapHostGlobal()
        );
        observer = new NeighborhoodObserver("DHC", instanceName);
        maximumGradientStrategy = new MaximumGradientStrategy(observer);

        for(int i = 0; i < 30; i++) {
            System.out.println("Execution number " + (i+1) + "\n");
            maximumGradientStrategy.execute(problem, globalOperatorList, timeLimit);
        }

        avgMakespanList.add(observer.avgReachedCost());
        bestKnownCostList.add(observer.getBestReachedMakespan());

        XLSXTableExporter.appendInstanceSheet(fileName, instanceName, observer, "N1 U N2 U N3 U N4");




        System.out.println("\n\nDHC | VNS (random choice) operator\n\n");

        globalOperatorList = List.of(
                new NeighborhoodChangeHostGlobal(problem.getInstanceData()),
                new NeighborhoodInsertionGlobal(),
                new NeighborhoodSwapGlobal(),
                new NeighborhoodSwapHostGlobal()
        );
        observer = new NeighborhoodObserver("DHC", instanceName);
        maximumGradientStrategy = new MaximumGradientStrategy(observer);

        for(int i = 0; i < 30; i++) {
            System.out.println("Execution number " + (i+1) + "\n");
            maximumGradientStrategy.executeVNS(problem, globalOperatorList, timeLimit);
        }

        avgMakespanList.add(observer.avgReachedCost());
        bestKnownCostList.add(observer.getBestReachedMakespan());

        XLSXTableExporter.appendInstanceSheet(fileName, instanceName, observer, "VNS (random choice)");




        System.out.println("\n\nHC | N1 operator\n\n");

        lazyOperator = new NeighborhoodChangeHostLazy(problem.getInstanceData());
        observer = new NeighborhoodObserver("HC", instanceName);
        simpleClimbingStrategy = new SimpleClimbingStrategy(observer);

        for(int i = 0; i < 30; i++) {
            System.out.println("Execution number " + (i+1) + "\n");
            simpleClimbingStrategy.execute(problem, lazyOperator, timeLimit);
        }

        avgMakespanList.add(observer.avgReachedCost());
        bestKnownCostList.add(observer.getBestReachedMakespan());

        XLSXTableExporter.appendInstanceSheet(fileName, instanceName, observer, "N1");




        System.out.println("\n\nHC | N2 operator\n\n");

        lazyOperator = new NeighborhoodInsertionLazy();
        observer = new NeighborhoodObserver("HC", instanceName);
        simpleClimbingStrategy = new SimpleClimbingStrategy(observer);

        for(int i = 0; i < 30; i++) {
            System.out.println("Execution number " + (i+1) + "\n");
            simpleClimbingStrategy.execute(problem, lazyOperator, timeLimit);
        }

        avgMakespanList.add(observer.avgReachedCost());
        bestKnownCostList.add(observer.getBestReachedMakespan());

        XLSXTableExporter.appendInstanceSheet(fileName, instanceName, observer, "N2");




        System.out.println("\n\nHC | N3 operator\n\n");

        lazyOperator = new NeighborhoodSwapLazy();
        observer = new NeighborhoodObserver("HC", instanceName);
        simpleClimbingStrategy = new SimpleClimbingStrategy(observer);

        for(int i = 0; i < 30; i++) {
            System.out.println("Execution number " + (i+1) + "\n");
            simpleClimbingStrategy.execute(problem, lazyOperator, timeLimit);
        }

        avgMakespanList.add(observer.avgReachedCost());
        bestKnownCostList.add(observer.getBestReachedMakespan());

        XLSXTableExporter.appendInstanceSheet(fileName, instanceName, observer, "N3");




        System.out.println("\n\nHC | N4 operator\n\n");

        lazyOperator = new NeighborhoodSwapHostLazy();
        observer = new NeighborhoodObserver("HC", instanceName);
        simpleClimbingStrategy = new SimpleClimbingStrategy(observer);

        for(int i = 0; i < 30; i++) {
            System.out.println("Execution number " + (i+1) + "\n");
            simpleClimbingStrategy.execute(problem, lazyOperator, timeLimit);
        }

        avgMakespanList.add(observer.avgReachedCost());
        bestKnownCostList.add(observer.getBestReachedMakespan());

        XLSXTableExporter.appendInstanceSheet(fileName, instanceName, observer, "N4");




        System.out.println("\n\nHC | N1 U N2 operator\n\n");

        lazyOperatorList = List.of(
                new NeighborhoodChangeHostLazy(problem.getInstanceData()),
                new NeighborhoodInsertionLazy()
        );
        observer = new NeighborhoodObserver("HC", instanceName);
        simpleClimbingStrategy = new SimpleClimbingStrategy(observer);

        for(int i = 0; i < 30; i++) {
            System.out.println("Execution number " + (i+1) + "\n");
            simpleClimbingStrategy.execute(problem, lazyOperatorList, timeLimit);
        }

        avgMakespanList.add(observer.avgReachedCost());
        bestKnownCostList.add(observer.getBestReachedMakespan());

        XLSXTableExporter.appendInstanceSheet(fileName, instanceName, observer, "N1 U N2");




        System.out.println("\n\nHC | N1 U N3 operator\n\n");

        lazyOperatorList = List.of(
                new NeighborhoodChangeHostLazy(problem.getInstanceData()),
                new NeighborhoodSwapLazy()
        );
        observer = new NeighborhoodObserver("HC", instanceName);
        simpleClimbingStrategy = new SimpleClimbingStrategy(observer);

        for(int i = 0; i < 30; i++) {
            System.out.println("Execution number " + (i+1) + "\n");
            simpleClimbingStrategy.execute(problem, lazyOperatorList, timeLimit);
        }

        avgMakespanList.add(observer.avgReachedCost());
        bestKnownCostList.add(observer.getBestReachedMakespan());

        XLSXTableExporter.appendInstanceSheet(fileName, instanceName, observer, "N1 U N3");




        System.out.println("\n\nHC | N1 U N4 operator\n\n");

        lazyOperatorList = List.of(
                new NeighborhoodChangeHostLazy(problem.getInstanceData()),
                new NeighborhoodSwapHostLazy()
        );
        observer = new NeighborhoodObserver("HC", instanceName);
        simpleClimbingStrategy = new SimpleClimbingStrategy(observer);

        for(int i = 0; i < 30; i++) {
            System.out.println("Execution number " + (i+1) + "\n");
            simpleClimbingStrategy.execute(problem, lazyOperatorList, timeLimit);
        }

        avgMakespanList.add(observer.avgReachedCost());
        bestKnownCostList.add(observer.getBestReachedMakespan());

        XLSXTableExporter.appendInstanceSheet(fileName, instanceName, observer, "N1 U N4");




        System.out.println("\n\nHC | N2 U N3 operator\n\n");

        lazyOperatorList = List.of(
                new NeighborhoodInsertionLazy(),
                new NeighborhoodSwapLazy()
        );
        observer = new NeighborhoodObserver("HC", instanceName);
        simpleClimbingStrategy = new SimpleClimbingStrategy(observer);

        for(int i = 0; i < 30; i++) {
            System.out.println("Execution number " + (i+1) + "\n");
            simpleClimbingStrategy.execute(problem, lazyOperatorList, timeLimit);
        }

        avgMakespanList.add(observer.avgReachedCost());
        bestKnownCostList.add(observer.getBestReachedMakespan());

        XLSXTableExporter.appendInstanceSheet(fileName, instanceName, observer, "N2 U N3");




        System.out.println("\n\nHC | N2 U N4 operator\n\n");

        lazyOperatorList = List.of(
                new NeighborhoodInsertionLazy(),
                new NeighborhoodSwapHostLazy()
        );
        observer = new NeighborhoodObserver("HC", instanceName);
        simpleClimbingStrategy = new SimpleClimbingStrategy(observer);

        for(int i = 0; i < 30; i++) {
            System.out.println("Execution number " + (i+1) + "\n");
            simpleClimbingStrategy.execute(problem, lazyOperatorList, timeLimit);
        }

        avgMakespanList.add(observer.avgReachedCost());
        bestKnownCostList.add(observer.getBestReachedMakespan());

        XLSXTableExporter.appendInstanceSheet(fileName, instanceName, observer, "N2 U N4");




        System.out.println("\n\nHC | N3 U N4 operator\n\n");

        lazyOperatorList = List.of(
                new NeighborhoodSwapLazy(),
                new NeighborhoodSwapHostLazy()
        );
        observer = new NeighborhoodObserver("HC", instanceName);
        simpleClimbingStrategy = new SimpleClimbingStrategy(observer);

        for(int i = 0; i < 30; i++) {
            System.out.println("Execution number " + (i+1) + "\n");
            simpleClimbingStrategy.execute(problem, lazyOperatorList, timeLimit);
        }

        avgMakespanList.add(observer.avgReachedCost());
        bestKnownCostList.add(observer.getBestReachedMakespan());

        XLSXTableExporter.appendInstanceSheet(fileName, instanceName, observer, "N3 U N4");




        System.out.println("\n\nHC | N1 U N2 U N3 operator\n\n");

        lazyOperatorList = List.of(
                new NeighborhoodChangeHostLazy(problem.getInstanceData()),
                new NeighborhoodInsertionLazy(),
                new NeighborhoodSwapLazy()
        );
        observer = new NeighborhoodObserver("HC", instanceName);
        simpleClimbingStrategy = new SimpleClimbingStrategy(observer);

        for(int i = 0; i < 30; i++) {
            System.out.println("Execution number " + (i+1) + "\n");
            simpleClimbingStrategy.execute(problem, lazyOperatorList, timeLimit);
        }

        avgMakespanList.add(observer.avgReachedCost());
        bestKnownCostList.add(observer.getBestReachedMakespan());

        XLSXTableExporter.appendInstanceSheet(fileName, instanceName, observer, "N1 U N2 U N3");




        System.out.println("\n\nHC | N1 U N2 U N4 operator\n\n");

        lazyOperatorList = List.of(
                new NeighborhoodChangeHostLazy(problem.getInstanceData()),
                new NeighborhoodInsertionLazy(),
                new NeighborhoodSwapHostLazy()
        );
        observer = new NeighborhoodObserver("HC", instanceName);
        simpleClimbingStrategy = new SimpleClimbingStrategy(observer);

        for(int i = 0; i < 30; i++) {
            System.out.println("Execution number " + (i+1) + "\n");
            simpleClimbingStrategy.execute(problem, lazyOperatorList, timeLimit);
        }

        avgMakespanList.add(observer.avgReachedCost());
        bestKnownCostList.add(observer.getBestReachedMakespan());

        XLSXTableExporter.appendInstanceSheet(fileName, instanceName, observer, "N1 U N2 U N4");




        System.out.println("\n\nHC | N1 U N3 U N4 operator\n\n");

        lazyOperatorList = List.of(
                new NeighborhoodChangeHostLazy(problem.getInstanceData()),
                new NeighborhoodSwapLazy(),
                new NeighborhoodSwapHostLazy()
        );
        observer = new NeighborhoodObserver("HC", instanceName);
        simpleClimbingStrategy = new SimpleClimbingStrategy(observer);

        for(int i = 0; i < 30; i++) {
            System.out.println("Execution number " + (i+1) + "\n");
            simpleClimbingStrategy.execute(problem, lazyOperatorList, timeLimit);
        }

        avgMakespanList.add(observer.avgReachedCost());
        bestKnownCostList.add(observer.getBestReachedMakespan());

        XLSXTableExporter.appendInstanceSheet(fileName, instanceName, observer, "N1 U N3 U N4");




        System.out.println("\n\nHC | N2 U N3 U N4 operator\n\n");

        lazyOperatorList = List.of(
                new NeighborhoodInsertionLazy(),
                new NeighborhoodSwapLazy(),
                new NeighborhoodSwapHostLazy()
        );
        observer = new NeighborhoodObserver("HC", instanceName);
        simpleClimbingStrategy = new SimpleClimbingStrategy(observer);

        for(int i = 0; i < 30; i++) {
            System.out.println("Execution number " + (i+1) + "\n");
            simpleClimbingStrategy.execute(problem, lazyOperatorList, timeLimit);
        }

        avgMakespanList.add(observer.avgReachedCost());
        bestKnownCostList.add(observer.getBestReachedMakespan());

        XLSXTableExporter.appendInstanceSheet(fileName, instanceName, observer, "N2 U N3 U N4");




        System.out.println("\n\nHC | N1 U N2 U N3 U N4 operator\n\n");

        lazyOperatorList = List.of(
                new NeighborhoodChangeHostLazy(problem.getInstanceData()),
                new NeighborhoodInsertionLazy(),
                new NeighborhoodSwapLazy(),
                new NeighborhoodSwapHostLazy()
        );
        observer = new NeighborhoodObserver("HC", instanceName);
        simpleClimbingStrategy = new SimpleClimbingStrategy(observer);

        for(int i = 0; i < 30; i++) {
            System.out.println("Execution number " + (i+1) + "\n");
            simpleClimbingStrategy.execute(problem, lazyOperatorList, timeLimit);
        }

        avgMakespanList.add(observer.avgReachedCost());
        bestKnownCostList.add(observer.getBestReachedMakespan());

        XLSXTableExporter.appendInstanceSheet(fileName, instanceName, observer, "N1 U N2 U N3 U N4");




        System.out.println("\n\nHC | VNS (random choice) operator\n\n");
        lazyOperatorList = List.of(
                new NeighborhoodChangeHostLazy(problem.getInstanceData()),
                new NeighborhoodInsertionLazy(),
                new NeighborhoodSwapLazy(),
                new NeighborhoodSwapHostLazy()
        );
        observer = new NeighborhoodObserver("HC", instanceName);
        simpleClimbingStrategy = new SimpleClimbingStrategy(observer);

        for(int i = 0; i < 30; i++) {
            System.out.println("Execution number " + (i+1) + "\n");
            simpleClimbingStrategy.executeVNS(problem, lazyOperatorList, timeLimit);
        }

        avgMakespanList.add(observer.avgReachedCost());
        bestKnownCostList.add(observer.getBestReachedMakespan());

        XLSXTableExporter.appendInstanceSheet(fileName, instanceName, observer, "VNS (random choice)");




        XLSXTableExporter.appendSummarySheet(fileName, instanceName, avgMakespanList, timeLimit);

        XLSXTableExporter.appendBKPercentageSheet(
                fileName,
                instanceName,
                NeighborUtils.computeBestKnownPercentageList(bestKnownCostList),
                timeLimit
        );

    }
}
