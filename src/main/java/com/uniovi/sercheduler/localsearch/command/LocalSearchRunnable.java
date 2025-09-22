package com.uniovi.sercheduler.localsearch.command;

import com.uniovi.sercheduler.dao.Objective;
import com.uniovi.sercheduler.expception.HostLoadException;
import com.uniovi.sercheduler.expception.WorkflowLoadException;
import com.uniovi.sercheduler.jmetal.problem.SchedulePermutationSolution;
import com.uniovi.sercheduler.jmetal.problem.SchedulingProblem;
import com.uniovi.sercheduler.localsearch.algorithms.localsearchalgorithm.LocalSearchAlgorithm;
import com.uniovi.sercheduler.localsearch.export.XLSXExporter;
import com.uniovi.sercheduler.localsearch.export.XLSXTableExporter;
import com.uniovi.sercheduler.localsearch.observer.LocalSearchObserver;
import com.uniovi.sercheduler.localsearch.operator.*;
import com.uniovi.sercheduler.localsearch.algorithms.MaximumGradientStrategy;
import com.uniovi.sercheduler.localsearch.algorithms.SimpleClimbingStrategy;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

public class LocalSearchRunnable {

    public static final String WORFLOW_FILE = "src/test/resources/cycles.json";
    public static final String HOSTS_FILE = "src/test/resources/extreme/hosts-16.json";
    public static final long TIME_LIMIT = 1000L;
    public static final long PERIODIC_TIME = 250;
    public static final boolean CREATE_FILE = true;

    public static void main(String[] args) {

        List<Objective> objectives = List.of(Objective.MAKESPAN, Objective.ENERGY);

        String workflowFileName = getFileName(WORFLOW_FILE);

        String hostsFileName = getFileName(HOSTS_FILE);

        String instanceName = workflowFileName + "_" + hostsFileName + "_" + TIME_LIMIT;

        String fileName = "operators_experiment_results";

        long seed = System.nanoTime();

        SchedulingProblem problem;

        try {
            problem =
                    new SchedulingProblem(
                            new File(WORFLOW_FILE),
                            new File(HOSTS_FILE),
                            "441Gf",
                            "simple",
                            seed,
                            objectives,
                            Objective.MAKESPAN.objectiveName);
        } catch(HostLoadException e) {
            System.out.println("The hosts file could not be found. Please review that the path is correctly written " +
                    "and make sure that the desired file is there.");
            return;
        } catch(WorkflowLoadException e) {
            System.out.println("The workflow file could not be found. Please review that the path is correctly written " +
                    "and make sure that the desired file is there.");
            return;
        }catch(RuntimeException e){
            System.out.println("Exception's message:\n" + e.getMessage());
            return;
        }

        operatorsExperiment(fileName, instanceName, problem, TIME_LIMIT, CREATE_FILE, PERIODIC_TIME);
    }

    protected static String getFileName(String filePath) {
        String[] filePathSplit = filePath.split("/");
        return filePathSplit[filePathSplit.length - 1].split("\\.")[0];
    }

    protected static void debug(String instanceName, SchedulingProblem problem){
        LocalSearchAlgorithm localSearchAlgorithm = new LocalSearchAlgorithm.Builder(problem).build();

        long startingTime = localSearchAlgorithm.startTimeCounter();

        SchedulePermutationSolution achievedSolution =
                localSearchAlgorithm.runLocalSearchGlobal(
                        List.of(new NeighborhoodChangeHostGlobal(problem.getInstanceData())),
                        new LocalSearchObserver("jansjnjs", -1));
    }

    protected static void operatorsExperiment(String fileName, String instanceName, SchedulingProblem problem, long timeLimit,
                                              boolean createFile, long periodicTimeForMakespanEvolution){

        NeighborhoodOperatorGlobal globalOperator;
        NeighborhoodOperatorLazy lazyOperator;
        LocalSearchObserver observer;
        MaximumGradientStrategy maximumGradientStrategy;
        SimpleClimbingStrategy simpleClimbingStrategy;
        List<NeighborhoodOperatorGlobal> globalOperatorList;
        List<NeighborhoodOperatorLazy> lazyOperatorList;

        List<Double> avgMakespanList = new ArrayList<>();
        List<Double> bestKnownCostList = new ArrayList<>();

        if (createFile)
            XLSXTableExporter.createWorkbook(fileName);

        try {
            XLSXTableExporter.createInstanceSheet(fileName, instanceName);
        }catch (RuntimeException e) {
            System.out.println("Exception's message:\n" + e.getMessage());
            return;
        }

        try {
            XLSXTableExporter.createMakespanEvolutionSheet(fileName, instanceName);
        }catch (RuntimeException e) {
            System.out.println("Exception's message:\n" + e.getMessage());
            return;
        }




        System.out.println("\n\nGD | N1 operator\n\n");

        globalOperator = new NeighborhoodChangeHostGlobal(problem.getInstanceData());
        observer = new LocalSearchObserver("GD", periodicTimeForMakespanEvolution);
        maximumGradientStrategy = new MaximumGradientStrategy(observer);

        for(int i = 0; i < 30; i++) {
            System.out.println("Execution number " + (i+1) + "\n");
            maximumGradientStrategy.execute(problem, globalOperator, timeLimit);
        }

        avgMakespanList.add(observer.getAvgMinReachedMakespan());
        bestKnownCostList.add(observer.getBestMinReachedMakespan());

        XLSXTableExporter.appendInstanceSheet(fileName, instanceName, observer, "N1");
        XLSXTableExporter.appendMakespanEvolutionSheet(fileName, instanceName, observer, "N1");




        System.out.println("\n\nGD | N2 operator\n\n");

        globalOperator = new NeighborhoodInsertionGlobal();
        observer = new LocalSearchObserver("GD", periodicTimeForMakespanEvolution);
        maximumGradientStrategy = new MaximumGradientStrategy(observer);

        for(int i = 0; i < 30; i++) {
            System.out.println("Execution number " + (i+1) + "\n");
            maximumGradientStrategy.execute(problem, globalOperator, timeLimit);
        }

        avgMakespanList.add(observer.getAvgMinReachedMakespan());
        bestKnownCostList.add(observer.getBestMinReachedMakespan());

        XLSXTableExporter.appendInstanceSheet(fileName, instanceName, observer, "N2");
        XLSXTableExporter.appendMakespanEvolutionSheet(fileName, instanceName, observer, "N2");




        System.out.println("\n\nGD | N3 operator\n\n");

        globalOperator = new NeighborhoodSwapGlobal();
        observer = new LocalSearchObserver("GD", periodicTimeForMakespanEvolution);
        maximumGradientStrategy = new MaximumGradientStrategy(observer);

        for(int i = 0; i < 30; i++) {
            System.out.println("Execution number " + (i+1) + "\n");
            maximumGradientStrategy.execute(problem, globalOperator, timeLimit);
        }

        avgMakespanList.add(observer.getAvgMinReachedMakespan());
        bestKnownCostList.add(observer.getBestMinReachedMakespan());

        XLSXTableExporter.appendInstanceSheet(fileName, instanceName, observer, "N3");
        XLSXTableExporter.appendMakespanEvolutionSheet(fileName, instanceName, observer, "N3");




        System.out.println("\n\nGD | N4 operator\n\n");

        globalOperator = new NeighborhoodSwapHostGlobal();
        observer = new LocalSearchObserver("GD", periodicTimeForMakespanEvolution);
        maximumGradientStrategy = new MaximumGradientStrategy(observer);

        for(int i = 0; i < 30; i++) {
            System.out.println("Execution number " + (i+1) + "\n");
            maximumGradientStrategy.execute(problem, globalOperator, timeLimit);
        }

        avgMakespanList.add(observer.getAvgMinReachedMakespan());
        bestKnownCostList.add(observer.getBestMinReachedMakespan());

        XLSXTableExporter.appendInstanceSheet(fileName, instanceName, observer, "N4");
        XLSXTableExporter.appendMakespanEvolutionSheet(fileName, instanceName, observer, "N4");




        System.out.println("\n\nGD | N1 U N2 operator\n\n");

        globalOperatorList = List.of(
                new NeighborhoodChangeHostGlobal(problem.getInstanceData()),
                new NeighborhoodInsertionGlobal()
        );
        observer = new LocalSearchObserver("GD", periodicTimeForMakespanEvolution);
        maximumGradientStrategy = new MaximumGradientStrategy(observer);

        for(int i = 0; i < 30; i++) {
            System.out.println("Execution number " + (i+1) + "\n");
            maximumGradientStrategy.execute(problem, globalOperatorList, timeLimit);
        }

        avgMakespanList.add(observer.getAvgMinReachedMakespan());
        bestKnownCostList.add(observer.getBestMinReachedMakespan());

        XLSXTableExporter.appendInstanceSheet(fileName, instanceName, observer, "N1 U N2");
        XLSXTableExporter.appendMakespanEvolutionSheet(fileName, instanceName, observer, "N1 U N2");




        System.out.println("\n\nGD | N1 U N3 operator\n\n");

        globalOperatorList = List.of(
                new NeighborhoodChangeHostGlobal(problem.getInstanceData()),
                new NeighborhoodSwapGlobal()
        );
        observer = new LocalSearchObserver("GD", periodicTimeForMakespanEvolution);
        maximumGradientStrategy = new MaximumGradientStrategy(observer);

        for(int i = 0; i < 30; i++) {
            System.out.println("Execution number " + (i+1) + "\n");
            maximumGradientStrategy.execute(problem, globalOperatorList, timeLimit);
        }

        avgMakespanList.add(observer.getAvgMinReachedMakespan());
        bestKnownCostList.add(observer.getBestMinReachedMakespan());

        XLSXTableExporter.appendInstanceSheet(fileName, instanceName, observer, "N1 U N3");
        XLSXTableExporter.appendMakespanEvolutionSheet(fileName, instanceName, observer, "N1 U N3");




        System.out.println("\n\nGD | N1 U N4 operator\n\n");

        globalOperatorList = List.of(
                new NeighborhoodChangeHostGlobal(problem.getInstanceData()),
                new NeighborhoodSwapHostGlobal()
        );
        observer = new LocalSearchObserver("GD", periodicTimeForMakespanEvolution);
        maximumGradientStrategy = new MaximumGradientStrategy(observer);

        for(int i = 0; i < 30; i++) {
            System.out.println("Execution number " + (i+1) + "\n");
            maximumGradientStrategy.execute(problem, globalOperatorList, timeLimit);
        }

        avgMakespanList.add(observer.getAvgMinReachedMakespan());
        bestKnownCostList.add(observer.getBestMinReachedMakespan());

        XLSXTableExporter.appendInstanceSheet(fileName, instanceName, observer, "N1 U N4");
        XLSXTableExporter.appendMakespanEvolutionSheet(fileName, instanceName, observer, "N1 U N4");




        System.out.println("\n\nGD | N2 U N3 operator\n\n");

        globalOperatorList = List.of(
                new NeighborhoodInsertionGlobal(),
                new NeighborhoodSwapGlobal()
        );
        observer = new LocalSearchObserver("GD", periodicTimeForMakespanEvolution);
        maximumGradientStrategy = new MaximumGradientStrategy(observer);

        for(int i = 0; i < 30; i++) {
            System.out.println("Execution number " + (i+1) + "\n");
            maximumGradientStrategy.execute(problem, globalOperatorList, timeLimit);
        }

        avgMakespanList.add(observer.getAvgMinReachedMakespan());
        bestKnownCostList.add(observer.getBestMinReachedMakespan());

        XLSXTableExporter.appendInstanceSheet(fileName, instanceName, observer, "N2 U N3");
        XLSXTableExporter.appendMakespanEvolutionSheet(fileName, instanceName, observer, "N2 U N3");



        System.out.println("\n\nGD | N2 U N4 operator\n\n");

        globalOperatorList = List.of(
                new NeighborhoodInsertionGlobal(),
                new NeighborhoodSwapHostGlobal()
        );
        observer = new LocalSearchObserver("GD", periodicTimeForMakespanEvolution);
        maximumGradientStrategy = new MaximumGradientStrategy(observer);

        for(int i = 0; i < 30; i++) {
            System.out.println("Execution number " + (i+1) + "\n");
            maximumGradientStrategy.execute(problem, globalOperatorList, timeLimit);
        }

        avgMakespanList.add(observer.getAvgMinReachedMakespan());
        bestKnownCostList.add(observer.getBestMinReachedMakespan());

        XLSXTableExporter.appendInstanceSheet(fileName, instanceName, observer, "N2 U N4");
        XLSXTableExporter.appendMakespanEvolutionSheet(fileName, instanceName, observer, "N2 U N4");




        System.out.println("\n\nGD | N3 U N4 operator\n\n");

        globalOperatorList = List.of(
                new NeighborhoodSwapGlobal(),
                new NeighborhoodSwapHostGlobal()
        );
        observer = new LocalSearchObserver("GD", periodicTimeForMakespanEvolution);
        maximumGradientStrategy = new MaximumGradientStrategy(observer);

        for(int i = 0; i < 30; i++) {
            System.out.println("Execution number " + (i+1) + "\n");
            maximumGradientStrategy.execute(problem, globalOperatorList, timeLimit);
        }

        avgMakespanList.add(observer.getAvgMinReachedMakespan());
        bestKnownCostList.add(observer.getBestMinReachedMakespan());

        XLSXTableExporter.appendInstanceSheet(fileName, instanceName, observer, "N3 U N4");
        XLSXTableExporter.appendMakespanEvolutionSheet(fileName, instanceName, observer, "N3 U N4");




        System.out.println("\n\nGD | N1 U N2 U N3 operator\n\n");

        globalOperatorList = List.of(
                new NeighborhoodChangeHostGlobal(problem.getInstanceData()),
                new NeighborhoodInsertionGlobal(),
                new NeighborhoodSwapGlobal()
        );
        observer = new LocalSearchObserver("GD", periodicTimeForMakespanEvolution);
        maximumGradientStrategy = new MaximumGradientStrategy(observer);

        for(int i = 0; i < 30; i++) {
            System.out.println("Execution number " + (i+1) + "\n");
            maximumGradientStrategy.execute(problem, globalOperatorList, timeLimit);
        }

        avgMakespanList.add(observer.getAvgMinReachedMakespan());
        bestKnownCostList.add(observer.getBestMinReachedMakespan());

        XLSXTableExporter.appendInstanceSheet(fileName, instanceName, observer, "N1 U N2 U N3");
        XLSXTableExporter.appendMakespanEvolutionSheet(fileName, instanceName, observer, "N1 U N2 U N3");




        System.out.println("\n\nGD | N1 U N2 U N4 operator\n\n");

        globalOperatorList = List.of(
                new NeighborhoodChangeHostGlobal(problem.getInstanceData()),
                new NeighborhoodInsertionGlobal(),
                new NeighborhoodSwapHostGlobal()
        );
        observer = new LocalSearchObserver("GD", periodicTimeForMakespanEvolution);
        maximumGradientStrategy = new MaximumGradientStrategy(observer);

        for(int i = 0; i < 30; i++) {
            System.out.println("Execution number " + (i+1) + "\n");
            maximumGradientStrategy.execute(problem, globalOperatorList, timeLimit);
        }

        avgMakespanList.add(observer.getAvgMinReachedMakespan());
        bestKnownCostList.add(observer.getBestMinReachedMakespan());

        XLSXTableExporter.appendInstanceSheet(fileName, instanceName, observer, "N1 U N2 U N4");
        XLSXTableExporter.appendMakespanEvolutionSheet(fileName, instanceName, observer, "N1 U N2 U N4");




        System.out.println("\n\nGD | N1 U N3 U N4 operator\n\n");

        globalOperatorList = List.of(
                new NeighborhoodChangeHostGlobal(problem.getInstanceData()),
                new NeighborhoodSwapGlobal(),
                new NeighborhoodSwapHostGlobal()
        );
        observer = new LocalSearchObserver("GD", periodicTimeForMakespanEvolution);
        maximumGradientStrategy = new MaximumGradientStrategy(observer);

        for(int i = 0; i < 30; i++) {
            System.out.println("Execution number " + (i+1) + "\n");
            maximumGradientStrategy.execute(problem, globalOperatorList, timeLimit);
        }

        avgMakespanList.add(observer.getAvgMinReachedMakespan());
        bestKnownCostList.add(observer.getBestMinReachedMakespan());

        XLSXTableExporter.appendInstanceSheet(fileName, instanceName, observer, "N1 U N3 U N4");
        XLSXTableExporter.appendMakespanEvolutionSheet(fileName, instanceName, observer, "N1 U N3 U N4");



        System.out.println("\n\nGD | N2 U N3 U N4 operator\n\n");

        globalOperatorList = List.of(
                new NeighborhoodInsertionGlobal(),
                new NeighborhoodSwapGlobal(),
                new NeighborhoodSwapHostGlobal()
        );
        observer = new LocalSearchObserver("GD", periodicTimeForMakespanEvolution);
        maximumGradientStrategy = new MaximumGradientStrategy(observer);

        for(int i = 0; i < 30; i++) {
            System.out.println("Execution number " + (i+1) + "\n");
            maximumGradientStrategy.execute(problem, globalOperatorList, timeLimit);
        }

        avgMakespanList.add(observer.getAvgMinReachedMakespan());
        bestKnownCostList.add(observer.getBestMinReachedMakespan());

        XLSXTableExporter.appendInstanceSheet(fileName, instanceName, observer, "N2 U N3 U N4");
        XLSXTableExporter.appendMakespanEvolutionSheet(fileName, instanceName, observer, "N2 U N3 U N4");




        System.out.println("\n\nGD | N1 U N2 U N3 U N4 operator\n\n");

        globalOperatorList = List.of(
                new NeighborhoodChangeHostGlobal(problem.getInstanceData()),
                new NeighborhoodInsertionGlobal(),
                new NeighborhoodSwapGlobal(),
                new NeighborhoodSwapHostGlobal()
        );
        observer = new LocalSearchObserver("GD", periodicTimeForMakespanEvolution);
        maximumGradientStrategy = new MaximumGradientStrategy(observer);

        for(int i = 0; i < 30; i++) {
            System.out.println("Execution number " + (i+1) + "\n");
            maximumGradientStrategy.execute(problem, globalOperatorList, timeLimit);
        }

        avgMakespanList.add(observer.getAvgMinReachedMakespan());
        bestKnownCostList.add(observer.getBestMinReachedMakespan());

        XLSXTableExporter.appendInstanceSheet(fileName, instanceName, observer, "N1 U N2 U N3 U N4");
        XLSXTableExporter.appendMakespanEvolutionSheet(fileName, instanceName, observer, "N1 U N2 U N3 U N4");




        System.out.println("\n\nGD | VNS (random choice) operator\n\n");

        globalOperatorList = List.of(
                new NeighborhoodChangeHostGlobal(problem.getInstanceData()),
                new NeighborhoodInsertionGlobal(),
                new NeighborhoodSwapGlobal(),
                new NeighborhoodSwapHostGlobal()
        );
        observer = new LocalSearchObserver("GD", periodicTimeForMakespanEvolution);
        maximumGradientStrategy = new MaximumGradientStrategy(observer);

        for(int i = 0; i < 30; i++) {
            System.out.println("Execution number " + (i+1) + "\n");
            maximumGradientStrategy.executeVNS(problem, globalOperatorList, timeLimit);
        }

        avgMakespanList.add(observer.getAvgMinReachedMakespan());
        bestKnownCostList.add(observer.getBestMinReachedMakespan());

        XLSXTableExporter.appendInstanceSheet(fileName, instanceName, observer, "VNS (random choice)");
        XLSXTableExporter.appendMakespanEvolutionSheet(fileName, instanceName, observer, "VNS (random choice)");




        System.out.println("\n\nHC | N1 operator\n\n");

        lazyOperator = new NeighborhoodChangeHostLazy(problem.getInstanceData());
        observer = new LocalSearchObserver("HC", periodicTimeForMakespanEvolution);
        simpleClimbingStrategy = new SimpleClimbingStrategy(observer);

        for(int i = 0; i < 30; i++) {
            System.out.println("Execution number " + (i+1) + "\n");
            simpleClimbingStrategy.execute(problem, lazyOperator, timeLimit);
        }

        avgMakespanList.add(observer.getAvgMinReachedMakespan());
        bestKnownCostList.add(observer.getBestMinReachedMakespan());

        XLSXTableExporter.appendInstanceSheet(fileName, instanceName, observer, "N1");

        XLSXTableExporter.appendMakespanEvolutionSheet(fileName, instanceName, observer, "N1");




        System.out.println("\n\nHC | N2 operator\n\n");

        lazyOperator = new NeighborhoodInsertionLazy();
        observer = new LocalSearchObserver("HC", periodicTimeForMakespanEvolution);
        simpleClimbingStrategy = new SimpleClimbingStrategy(observer);

        for(int i = 0; i < 30; i++) {
            System.out.println("Execution number " + (i+1) + "\n");
            simpleClimbingStrategy.execute(problem, lazyOperator, timeLimit);
        }

        avgMakespanList.add(observer.getAvgMinReachedMakespan());
        bestKnownCostList.add(observer.getBestMinReachedMakespan());

        XLSXTableExporter.appendInstanceSheet(fileName, instanceName, observer, "N2");
        XLSXTableExporter.appendMakespanEvolutionSheet(fileName, instanceName, observer, "N2");




        System.out.println("\n\nHC | N3 operator\n\n");

        lazyOperator = new NeighborhoodSwapLazy();
        observer = new LocalSearchObserver("HC", periodicTimeForMakespanEvolution);
        simpleClimbingStrategy = new SimpleClimbingStrategy(observer);

        for(int i = 0; i < 30; i++) {
            System.out.println("Execution number " + (i+1) + "\n");
            simpleClimbingStrategy.execute(problem, lazyOperator, timeLimit);
        }

        avgMakespanList.add(observer.getAvgMinReachedMakespan());
        bestKnownCostList.add(observer.getBestMinReachedMakespan());

        XLSXTableExporter.appendInstanceSheet(fileName, instanceName, observer, "N3");
        XLSXTableExporter.appendMakespanEvolutionSheet(fileName, instanceName, observer, "N3");




        System.out.println("\n\nHC | N4 operator\n\n");

        lazyOperator = new NeighborhoodSwapHostLazy();
        observer = new LocalSearchObserver("HC", periodicTimeForMakespanEvolution);
        simpleClimbingStrategy = new SimpleClimbingStrategy(observer);

        for(int i = 0; i < 30; i++) {
            System.out.println("Execution number " + (i+1) + "\n");
            simpleClimbingStrategy.execute(problem, lazyOperator, timeLimit);
        }

        avgMakespanList.add(observer.getAvgMinReachedMakespan());
        bestKnownCostList.add(observer.getBestMinReachedMakespan());

        XLSXTableExporter.appendInstanceSheet(fileName, instanceName, observer, "N4");
        XLSXTableExporter.appendMakespanEvolutionSheet(fileName, instanceName, observer, "N4");




        System.out.println("\n\nHC | N1 U N2 operator\n\n");

        lazyOperatorList = List.of(
                new NeighborhoodChangeHostLazy(problem.getInstanceData()),
                new NeighborhoodInsertionLazy()
        );
        observer = new LocalSearchObserver("HC", periodicTimeForMakespanEvolution);
        simpleClimbingStrategy = new SimpleClimbingStrategy(observer);

        for(int i = 0; i < 30; i++) {
            System.out.println("Execution number " + (i+1) + "\n");
            simpleClimbingStrategy.execute(problem, lazyOperatorList, timeLimit);
        }

        avgMakespanList.add(observer.getAvgMinReachedMakespan());
        bestKnownCostList.add(observer.getBestMinReachedMakespan());

        XLSXTableExporter.appendInstanceSheet(fileName, instanceName, observer, "N1 U N2");
        XLSXTableExporter.appendMakespanEvolutionSheet(fileName, instanceName, observer, "N1 U N2");




        System.out.println("\n\nHC | N1 U N3 operator\n\n");

        lazyOperatorList = List.of(
                new NeighborhoodChangeHostLazy(problem.getInstanceData()),
                new NeighborhoodSwapLazy()
        );
        observer = new LocalSearchObserver("HC", periodicTimeForMakespanEvolution);
        simpleClimbingStrategy = new SimpleClimbingStrategy(observer);

        for(int i = 0; i < 30; i++) {
            System.out.println("Execution number " + (i+1) + "\n");
            simpleClimbingStrategy.execute(problem, lazyOperatorList, timeLimit);
        }

        avgMakespanList.add(observer.getAvgMinReachedMakespan());
        bestKnownCostList.add(observer.getBestMinReachedMakespan());

        XLSXTableExporter.appendInstanceSheet(fileName, instanceName, observer, "N1 U N3");
        XLSXTableExporter.appendMakespanEvolutionSheet(fileName, instanceName, observer, "N1 U N3");




        System.out.println("\n\nHC | N1 U N4 operator\n\n");

        lazyOperatorList = List.of(
                new NeighborhoodChangeHostLazy(problem.getInstanceData()),
                new NeighborhoodSwapHostLazy()
        );
        observer = new LocalSearchObserver("HC", periodicTimeForMakespanEvolution);
        simpleClimbingStrategy = new SimpleClimbingStrategy(observer);

        for(int i = 0; i < 30; i++) {
            System.out.println("Execution number " + (i+1) + "\n");
            simpleClimbingStrategy.execute(problem, lazyOperatorList, timeLimit);
        }

        avgMakespanList.add(observer.getAvgMinReachedMakespan());
        bestKnownCostList.add(observer.getBestMinReachedMakespan());

        XLSXTableExporter.appendInstanceSheet(fileName, instanceName, observer, "N1 U N4");
        XLSXTableExporter.appendMakespanEvolutionSheet(fileName, instanceName, observer, "N1 U N4");




        System.out.println("\n\nHC | N2 U N3 operator\n\n");

        lazyOperatorList = List.of(
                new NeighborhoodInsertionLazy(),
                new NeighborhoodSwapLazy()
        );
        observer = new LocalSearchObserver("HC", periodicTimeForMakespanEvolution);
        simpleClimbingStrategy = new SimpleClimbingStrategy(observer);

        for(int i = 0; i < 30; i++) {
            System.out.println("Execution number " + (i+1) + "\n");
            simpleClimbingStrategy.execute(problem, lazyOperatorList, timeLimit);
        }

        avgMakespanList.add(observer.getAvgMinReachedMakespan());
        bestKnownCostList.add(observer.getBestMinReachedMakespan());

        XLSXTableExporter.appendInstanceSheet(fileName, instanceName, observer, "N2 U N3");
        XLSXTableExporter.appendMakespanEvolutionSheet(fileName, instanceName, observer, "N2 U N3");




        System.out.println("\n\nHC | N2 U N4 operator\n\n");

        lazyOperatorList = List.of(
                new NeighborhoodInsertionLazy(),
                new NeighborhoodSwapHostLazy()
        );
        observer = new LocalSearchObserver("HC", periodicTimeForMakespanEvolution);
        simpleClimbingStrategy = new SimpleClimbingStrategy(observer);

        for(int i = 0; i < 30; i++) {
            System.out.println("Execution number " + (i+1) + "\n");
            simpleClimbingStrategy.execute(problem, lazyOperatorList, timeLimit);
        }

        avgMakespanList.add(observer.getAvgMinReachedMakespan());
        bestKnownCostList.add(observer.getBestMinReachedMakespan());

        XLSXTableExporter.appendInstanceSheet(fileName, instanceName, observer, "N2 U N4");
        XLSXTableExporter.appendMakespanEvolutionSheet(fileName, instanceName, observer, "N2 U N4");




        System.out.println("\n\nHC | N3 U N4 operator\n\n");

        lazyOperatorList = List.of(
                new NeighborhoodSwapLazy(),
                new NeighborhoodSwapHostLazy()
        );
        observer = new LocalSearchObserver("HC", periodicTimeForMakespanEvolution);
        simpleClimbingStrategy = new SimpleClimbingStrategy(observer);

        for(int i = 0; i < 30; i++) {
            System.out.println("Execution number " + (i+1) + "\n");
            simpleClimbingStrategy.execute(problem, lazyOperatorList, timeLimit);
        }

        avgMakespanList.add(observer.getAvgMinReachedMakespan());
        bestKnownCostList.add(observer.getBestMinReachedMakespan());

        XLSXTableExporter.appendInstanceSheet(fileName, instanceName, observer, "N3 U N4");
        XLSXTableExporter.appendMakespanEvolutionSheet(fileName, instanceName, observer, "N3 U N4");




        System.out.println("\n\nHC | N1 U N2 U N3 operator\n\n");

        lazyOperatorList = List.of(
                new NeighborhoodChangeHostLazy(problem.getInstanceData()),
                new NeighborhoodInsertionLazy(),
                new NeighborhoodSwapLazy()
        );
        observer = new LocalSearchObserver("HC", periodicTimeForMakespanEvolution);
        simpleClimbingStrategy = new SimpleClimbingStrategy(observer);

        for(int i = 0; i < 30; i++) {
            System.out.println("Execution number " + (i+1) + "\n");
            simpleClimbingStrategy.execute(problem, lazyOperatorList, timeLimit);
        }

        avgMakespanList.add(observer.getAvgMinReachedMakespan());
        bestKnownCostList.add(observer.getBestMinReachedMakespan());

        XLSXTableExporter.appendInstanceSheet(fileName, instanceName, observer, "N1 U N2 U N3");
        XLSXTableExporter.appendMakespanEvolutionSheet(fileName, instanceName, observer, "N1 U N2 U N3");




        System.out.println("\n\nHC | N1 U N2 U N4 operator\n\n");

        lazyOperatorList = List.of(
                new NeighborhoodChangeHostLazy(problem.getInstanceData()),
                new NeighborhoodInsertionLazy(),
                new NeighborhoodSwapHostLazy()
        );
        observer = new LocalSearchObserver("HC", periodicTimeForMakespanEvolution);
        simpleClimbingStrategy = new SimpleClimbingStrategy(observer);

        for(int i = 0; i < 30; i++) {
            System.out.println("Execution number " + (i+1) + "\n");
            simpleClimbingStrategy.execute(problem, lazyOperatorList, timeLimit);
        }

        avgMakespanList.add(observer.getAvgMinReachedMakespan());
        bestKnownCostList.add(observer.getBestMinReachedMakespan());

        XLSXTableExporter.appendInstanceSheet(fileName, instanceName, observer, "N1 U N2 U N4");
        XLSXTableExporter.appendMakespanEvolutionSheet(fileName, instanceName, observer, "N1 U N2 U N4");




        System.out.println("\n\nHC | N1 U N3 U N4 operator\n\n");

        lazyOperatorList = List.of(
                new NeighborhoodChangeHostLazy(problem.getInstanceData()),
                new NeighborhoodSwapLazy(),
                new NeighborhoodSwapHostLazy()
        );
        observer = new LocalSearchObserver("HC", periodicTimeForMakespanEvolution);
        simpleClimbingStrategy = new SimpleClimbingStrategy(observer);

        for(int i = 0; i < 30; i++) {
            System.out.println("Execution number " + (i+1) + "\n");
            simpleClimbingStrategy.execute(problem, lazyOperatorList, timeLimit);
        }

        avgMakespanList.add(observer.getAvgMinReachedMakespan());
        bestKnownCostList.add(observer.getBestMinReachedMakespan());

        XLSXTableExporter.appendInstanceSheet(fileName, instanceName, observer, "N1 U N3 U N4");
        XLSXTableExporter.appendMakespanEvolutionSheet(fileName, instanceName, observer, "N1 U N3 U N4");




        System.out.println("\n\nHC | N2 U N3 U N4 operator\n\n");

        lazyOperatorList = List.of(
                new NeighborhoodInsertionLazy(),
                new NeighborhoodSwapLazy(),
                new NeighborhoodSwapHostLazy()
        );
        observer = new LocalSearchObserver("HC", periodicTimeForMakespanEvolution);
        simpleClimbingStrategy = new SimpleClimbingStrategy(observer);

        for(int i = 0; i < 30; i++) {
            System.out.println("Execution number " + (i+1) + "\n");
            simpleClimbingStrategy.execute(problem, lazyOperatorList, timeLimit);
        }

        avgMakespanList.add(observer.getAvgMinReachedMakespan());
        bestKnownCostList.add(observer.getBestMinReachedMakespan());

        XLSXTableExporter.appendInstanceSheet(fileName, instanceName, observer, "N2 U N3 U N4");
        XLSXTableExporter.appendMakespanEvolutionSheet(fileName, instanceName, observer, "N2 U N3 U N4");




        System.out.println("\n\nHC | N1 U N2 U N3 U N4 operator\n\n");

        lazyOperatorList = List.of(
                new NeighborhoodChangeHostLazy(problem.getInstanceData()),
                new NeighborhoodInsertionLazy(),
                new NeighborhoodSwapLazy(),
                new NeighborhoodSwapHostLazy()
        );
        observer = new LocalSearchObserver("HC", periodicTimeForMakespanEvolution);
        simpleClimbingStrategy = new SimpleClimbingStrategy(observer);

        for(int i = 0; i < 30; i++) {
            System.out.println("Execution number " + (i+1) + "\n");
            simpleClimbingStrategy.execute(problem, lazyOperatorList, timeLimit);
        }

        avgMakespanList.add(observer.getAvgMinReachedMakespan());
        bestKnownCostList.add(observer.getBestMinReachedMakespan());

        XLSXTableExporter.appendInstanceSheet(fileName, instanceName, observer, "N1 U N2 U N3 U N4");
        XLSXTableExporter.appendMakespanEvolutionSheet(fileName, instanceName, observer, "N1 U N2 U N3 U N4");




        System.out.println("\n\nHC | VNS (random choice) operator\n\n");
        lazyOperatorList = List.of(
                new NeighborhoodChangeHostLazy(problem.getInstanceData()),
                new NeighborhoodInsertionLazy(),
                new NeighborhoodSwapLazy(),
                new NeighborhoodSwapHostLazy()
        );
        observer = new LocalSearchObserver("HC", periodicTimeForMakespanEvolution);
        simpleClimbingStrategy = new SimpleClimbingStrategy(observer);

        for(int i = 0; i < 30; i++) {
            System.out.println("Execution number " + (i+1) + "\n");
            simpleClimbingStrategy.executeVNS(problem, lazyOperatorList, timeLimit);
        }

        avgMakespanList.add(observer.getAvgMinReachedMakespan());
        bestKnownCostList.add(observer.getBestMinReachedMakespan());

        XLSXTableExporter.appendInstanceSheet(fileName, instanceName, observer, "VNS (random choice)");
        XLSXTableExporter.appendMakespanEvolutionSheet(fileName, instanceName, observer, "VNS (random choice)");




        XLSXTableExporter.appendSummarySheet(fileName, instanceName, avgMakespanList, timeLimit);

        XLSXTableExporter.appendBKPercentageSheet(
                fileName,
                instanceName,
                NeighborUtils.computeBestKnownPercentageList(bestKnownCostList),
                timeLimit
        );

    }



    private static void StrategiesExperiment(String instanceName, SchedulingProblem problem) {

        System.out.println("\n\nMaximum Gradient strategy\n\n");

        //Here you can change the operator
        NeighborhoodOperatorGlobal globalOperator = new NeighborhoodSwapHostGlobal();

        LocalSearchObserver observer = new LocalSearchObserver("GD", -1);

        MaximumGradientStrategy maximumGradientStrategy = new MaximumGradientStrategy(observer);

        for(int i = 0; i < 30; i++)
            maximumGradientStrategy.execute(problem, globalOperator);

        System.out.println(observer);

        XLSXExporter.createWorkbook("local_search_results_" + globalOperator.getName());
        XLSXExporter.appendWorkbook(observer, "local_search_results_" + globalOperator.getName());

        /*CSVExporter.createCSV("local_search_results_" + globalOperator.getName());
        CSVExporter.appendCSV(observer, "local_search_results_" + globalOperator.getName());*/




        System.out.println("\n\nSimple Climbing strategy\n\n");

        //Here you can change the operator
        NeighborhoodOperatorLazy lazyOperator = new NeighborhoodSwapHostLazy();

        observer = new LocalSearchObserver("HC", -1);

        SimpleClimbingStrategy simpleClimbingStrategy = new SimpleClimbingStrategy(observer);

        for(int i = 0; i < 30; i++)
            simpleClimbingStrategy.execute(problem, lazyOperator);

        System.out.println(observer);

        XLSXExporter.appendWorkbook(observer, "local_search_results_" + globalOperator.getName());

        //CSVExporter.appendCSV(observer, "local_search_results_" + globalOperator.getName());
    }
}
