package com.uniovi.sercheduler.localsearch.command;

import com.uniovi.sercheduler.dao.Objective;
import com.uniovi.sercheduler.expception.HostLoadException;
import com.uniovi.sercheduler.expception.WorkflowLoadException;
import com.uniovi.sercheduler.jmetal.problem.SchedulingProblem;
import com.uniovi.sercheduler.localsearch.export.XLSXTableExporter;
import com.uniovi.sercheduler.localsearch.observer.LocalSearchObserver;
import com.uniovi.sercheduler.localsearch.operator.*;
import com.uniovi.sercheduler.localsearch.algorithms.MaximumGradientStrategy;
import com.uniovi.sercheduler.localsearch.algorithms.SimpleClimbingStrategy;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

//java -jar target/sercheduler-0.0.1-SNAPSHOT.jar localsearch -W src/test/resources/cycles.json -H src/test/resources/extreme/hosts-16.json -C true -T 1000 -P 100 -N 41_16_100_HC -S HC -O N1uN2

public class LocalSearchRunnable {

    public static final String WORFLOW_FILE = "src/test/resources/localsearch/montage-chameleon-dss-125d-001.json";
    public static final String HOSTS_FILE = "src/test/resources/extreme/hosts-16.json";
    public static final long TIME_LIMIT = 1000L;
    public static final long PERIODIC_TIME = 100;
    public static final boolean CREATE_FILE = true;
    public static final String STRATEGY_NAME = "HC";
    public static final String OPERATOR_CONFIG = "N1uN2uN3uN4";

    public static final String FILE_NAME = "prueba";


    public static void main(String[] args) {

        List<Objective> objectives = List.of(Objective.MAKESPAN, Objective.ENERGY);

        //String workflowFileName = getFileName(WORFLOW_FILE);

        //String hostsFileName = getFileName(HOSTS_FILE);

        //String instanceName = workflowFileName + "_" + hostsFileName + "_" + TIME_LIMIT;

        //String fileName = "operators_experiment_results";

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

        executeOperatorsExperiment(TIME_LIMIT, CREATE_FILE, PERIODIC_TIME, FILE_NAME, FILE_NAME,
                STRATEGY_NAME, OPERATOR_CONFIG, problem);
    }

    protected static String getFileName(String filePath) {
        String[] filePathSplit = filePath.split("/");
        return filePathSplit[filePathSplit.length - 1].split("\\.")[0];
    }

    protected static void debug(SchedulingProblem problem){

        String fileName = "debug";

        XLSXTableExporter.createWorkbook(fileName);

        createSheets(fileName, fileName);

        LocalSearchObserver globalObserver = globalOperatorExperiment(problem, 1000, 100,
                new ArrayList<>(), new ArrayList<>(), List.of(new NeighborhoodChangeHostGlobal(problem.getInstanceData())),
                "N1");

        appendSheets(fileName, fileName, globalObserver);
    }



    protected static void executeOperatorsExperiment(long timeLimit, boolean createFile,
                                                     long periodicTimeForMakespanEvolution, String instanceName,
                                                     String fileName, String strategy, String operatorConfigName,
                                                     SchedulingProblem problem) {
        switch(strategy.toLowerCase()){
            case "all":
                LocalSearchRunnable.allOperatorsExperiment(fileName, instanceName, problem, timeLimit, createFile, periodicTimeForMakespanEvolution);
                break;
            case "gd":

                List<NeighborhoodOperatorGlobal> globalOperatorList =
                        switch (operatorConfigName.toLowerCase()) {
                            case "n1" -> List.of(new NeighborhoodChangeHostGlobal(problem.getInstanceData()));
                            case "n2" -> List.of(new NeighborhoodInsertionGlobal());
                            case "n3" -> List.of(new NeighborhoodSwapGlobal());
                            case "n4" -> List.of(new NeighborhoodSwapHostGlobal());
                            case "n1un2" -> List.of(
                                    new NeighborhoodChangeHostGlobal(problem.getInstanceData()),
                                    new NeighborhoodInsertionGlobal()
                            );
                            case "n1un3" -> List.of(
                                    new NeighborhoodChangeHostGlobal(problem.getInstanceData()),
                                    new NeighborhoodSwapGlobal()
                            );
                            case "n1un4" -> List.of(
                                    new NeighborhoodChangeHostGlobal(problem.getInstanceData()),
                                    new NeighborhoodSwapHostGlobal()
                            );
                            case "n2un3" -> List.of(
                                    new NeighborhoodInsertionGlobal(),
                                    new NeighborhoodSwapGlobal()
                            );
                            case "n2un4" -> List.of(
                                    new NeighborhoodInsertionGlobal(),
                                    new NeighborhoodSwapHostGlobal()
                            );
                            case "n3un4" -> List.of(
                                    new NeighborhoodSwapGlobal(),
                                    new NeighborhoodSwapHostGlobal()
                            );
                            case "n1un2un3" -> List.of(
                                    new NeighborhoodChangeHostGlobal(problem.getInstanceData()),
                                    new NeighborhoodInsertionGlobal(),
                                    new NeighborhoodSwapGlobal()
                            );
                            case "n1un2un4" -> List.of(
                                    new NeighborhoodChangeHostGlobal(problem.getInstanceData()),
                                    new NeighborhoodInsertionGlobal(),
                                    new NeighborhoodSwapHostGlobal()
                            );
                            case "n1un3un4" -> List.of(
                                    new NeighborhoodChangeHostGlobal(problem.getInstanceData()),
                                    new NeighborhoodSwapGlobal(),
                                    new NeighborhoodSwapHostGlobal()
                            );
                            case "n2un3un4" -> List.of(
                                    new NeighborhoodInsertionGlobal(),
                                    new NeighborhoodSwapGlobal(),
                                    new NeighborhoodSwapHostGlobal()
                            );
                            case "n1un2un3un4", "vns" -> List.of(
                                    new NeighborhoodChangeHostGlobal(problem.getInstanceData()),
                                    new NeighborhoodInsertionGlobal(),
                                    new NeighborhoodSwapGlobal(),
                                    new NeighborhoodSwapHostGlobal()
                            );
                            default -> new ArrayList<>();
                        };

                if(globalOperatorList.isEmpty()) {
                    System.out.println("Please define a valid operator configuration");
                    break;
                }

                XLSXTableExporter.createWorkbook(fileName);

                createSheets(fileName, instanceName);

                LocalSearchObserver globalObserver = globalOperatorExperiment(problem, timeLimit, periodicTimeForMakespanEvolution,
                        new ArrayList<>(), new ArrayList<>(), globalOperatorList, operatorConfigName);

                appendSheets(fileName, instanceName, globalObserver);

                break;
            case "hc":

                List<NeighborhoodOperatorLazy> lazyOperatorList =
                        switch (operatorConfigName.toLowerCase()) {
                            case "n1" -> List.of(
                                    new NeighborhoodChangeHostLazy(problem.getInstanceData())
                            );
                            case "n2" -> List.of(
                                    new NeighborhoodInsertionLazy()
                            );
                            case "n3" -> List.of(
                                    new NeighborhoodSwapLazy()
                            );
                            case "n4" -> List.of(
                                    new NeighborhoodSwapHostLazy()
                            );
                            case "n1un2" -> List.of(
                                    new NeighborhoodChangeHostLazy(problem.getInstanceData()),
                                    new NeighborhoodInsertionLazy()
                            );

                            case "n1un3" -> List.of(
                                    new NeighborhoodChangeHostLazy(problem.getInstanceData()),
                                    new NeighborhoodSwapLazy()
                            );
                            case "n1un4" -> List.of(
                                    new NeighborhoodChangeHostLazy(problem.getInstanceData()),
                                    new NeighborhoodSwapHostLazy()
                            );
                            case "n2un3" -> List.of(
                                    new NeighborhoodInsertionLazy(),
                                    new NeighborhoodSwapLazy()
                            );

                            case "n2un4" -> List.of(
                                    new NeighborhoodInsertionLazy(),
                                    new NeighborhoodSwapHostLazy()
                            );
                            case "n3un4" -> List.of(
                                    new NeighborhoodSwapLazy(),
                                    new NeighborhoodSwapHostLazy()
                            );
                            case "n1un2un3" -> List.of(
                                    new NeighborhoodChangeHostLazy(problem.getInstanceData()),
                                    new NeighborhoodInsertionLazy(),
                                    new NeighborhoodSwapLazy()
                            );
                            case "n1un2un4" -> List.of(
                                    new NeighborhoodChangeHostLazy(problem.getInstanceData()),
                                    new NeighborhoodInsertionLazy(),
                                    new NeighborhoodSwapHostLazy()
                            );
                            case "n1un3un4" -> List.of(
                                    new NeighborhoodChangeHostLazy(problem.getInstanceData()),
                                    new NeighborhoodSwapLazy(),
                                    new NeighborhoodSwapHostLazy()
                            );
                            case "n2un3un4" -> List.of(
                                    new NeighborhoodInsertionLazy(),
                                    new NeighborhoodSwapLazy(),
                                    new NeighborhoodSwapHostLazy()
                            );
                            case "n1un2un3un4", "vns" -> List.of(
                                    new NeighborhoodChangeHostLazy(problem.getInstanceData()),
                                    new NeighborhoodInsertionLazy(),
                                    new NeighborhoodSwapLazy(),
                                    new NeighborhoodSwapHostLazy()
                            );
                            default -> new ArrayList<>();
                        };

                if(lazyOperatorList.isEmpty()){
                    System.out.println("Please define a valid operator configuration");
                    break;
                }

                XLSXTableExporter.createWorkbook(fileName);

                createSheets(fileName, instanceName);

                LocalSearchObserver lazyObserver = lazyOperatorExperiment(problem, timeLimit, periodicTimeForMakespanEvolution,
                        new ArrayList<>(), new ArrayList<>(), lazyOperatorList, operatorConfigName);

                appendSheets(fileName, instanceName, lazyObserver);

                break;
            default:
                System.out.println("Please define a valid strategy");
                break;
        }
    }




    protected static void allOperatorsExperiment(String fileName, String instanceName, SchedulingProblem problem, long timeLimit,
                                                 boolean createFile, long periodicTimeForMakespanEvolution){

        String operatorsName;
        LocalSearchObserver observer;
        List<NeighborhoodOperatorGlobal> globalOperatorList;
        List<NeighborhoodOperatorLazy> lazyOperatorList;

        List<Double> avgMakespanList = new ArrayList<>();
        List<Double> bestKnownCostList = new ArrayList<>();

        if (createFile)
            XLSXTableExporter.createWorkbook(fileName);

        try{
            createSheets(fileName, instanceName);
        } catch(RuntimeException e){
            return;
        }


        globalOperatorList = List.of( new NeighborhoodChangeHostGlobal(problem.getInstanceData()) );

        operatorsName = generateGlobalOperatorsListName(globalOperatorList);

        observer = globalOperatorExperiment(problem, timeLimit, periodicTimeForMakespanEvolution,
                avgMakespanList, bestKnownCostList, globalOperatorList, operatorsName);

        appendSheets(fileName, instanceName, observer);



        globalOperatorList = List.of( new NeighborhoodInsertionGlobal() );

        operatorsName = generateGlobalOperatorsListName(globalOperatorList);

        observer = globalOperatorExperiment(problem, timeLimit, periodicTimeForMakespanEvolution,
                avgMakespanList, bestKnownCostList, globalOperatorList, operatorsName);

        appendSheets(fileName, instanceName, observer);




        globalOperatorList = List.of( new NeighborhoodSwapGlobal() );

        operatorsName = generateGlobalOperatorsListName(globalOperatorList);

        observer = globalOperatorExperiment(problem, timeLimit, periodicTimeForMakespanEvolution,
                avgMakespanList, bestKnownCostList, globalOperatorList, operatorsName);

        appendSheets(fileName, instanceName, observer);




        globalOperatorList = List.of( new NeighborhoodSwapHostGlobal() );

        operatorsName = generateGlobalOperatorsListName(globalOperatorList);

        observer = globalOperatorExperiment(problem, timeLimit, periodicTimeForMakespanEvolution,
                avgMakespanList, bestKnownCostList, globalOperatorList, operatorsName);

        appendSheets(fileName, instanceName, observer);



        globalOperatorList = List.of(
                new NeighborhoodChangeHostGlobal(problem.getInstanceData()),
                new NeighborhoodInsertionGlobal()
        );

        operatorsName = generateGlobalOperatorsListName(globalOperatorList);

        observer = globalOperatorExperiment(problem, timeLimit, periodicTimeForMakespanEvolution,
                avgMakespanList, bestKnownCostList, globalOperatorList, operatorsName);

        appendSheets(fileName, instanceName, observer);




        globalOperatorList = List.of(
                new NeighborhoodChangeHostGlobal(problem.getInstanceData()),
                new NeighborhoodSwapGlobal()
        );

        operatorsName = generateGlobalOperatorsListName(globalOperatorList);

        observer = globalOperatorExperiment(problem, timeLimit, periodicTimeForMakespanEvolution,
                avgMakespanList, bestKnownCostList, globalOperatorList, operatorsName);

        appendSheets(fileName, instanceName, observer);




        globalOperatorList = List.of(
                new NeighborhoodChangeHostGlobal(problem.getInstanceData()),
                new NeighborhoodSwapHostGlobal()
        );

        operatorsName = generateGlobalOperatorsListName(globalOperatorList);

        observer = globalOperatorExperiment(problem, timeLimit, periodicTimeForMakespanEvolution,
                avgMakespanList, bestKnownCostList, globalOperatorList, operatorsName);

        appendSheets(fileName, instanceName, observer);




        globalOperatorList = List.of(
                new NeighborhoodInsertionGlobal(),
                new NeighborhoodSwapGlobal()
        );

        operatorsName = generateGlobalOperatorsListName(globalOperatorList);

        observer = globalOperatorExperiment(problem, timeLimit, periodicTimeForMakespanEvolution,
                avgMakespanList, bestKnownCostList, globalOperatorList, operatorsName);

        appendSheets(fileName, instanceName, observer);




        globalOperatorList = List.of(
                new NeighborhoodInsertionGlobal(),
                new NeighborhoodSwapHostGlobal()
        );

        operatorsName = generateGlobalOperatorsListName(globalOperatorList);

        observer = globalOperatorExperiment(problem, timeLimit, periodicTimeForMakespanEvolution,
                avgMakespanList, bestKnownCostList, globalOperatorList, operatorsName);

        appendSheets(fileName, instanceName, observer);




        globalOperatorList = List.of(
                new NeighborhoodSwapGlobal(),
                new NeighborhoodSwapHostGlobal()
        );

        operatorsName = generateGlobalOperatorsListName(globalOperatorList);

        observer = globalOperatorExperiment(problem, timeLimit, periodicTimeForMakespanEvolution,
                avgMakespanList, bestKnownCostList, globalOperatorList, operatorsName);

        appendSheets(fileName, instanceName, observer);




        globalOperatorList = List.of(
                new NeighborhoodChangeHostGlobal(problem.getInstanceData()),
                new NeighborhoodInsertionGlobal(),
                new NeighborhoodSwapGlobal()
        );

        operatorsName = generateGlobalOperatorsListName(globalOperatorList);

        observer = globalOperatorExperiment(problem, timeLimit, periodicTimeForMakespanEvolution,
                avgMakespanList, bestKnownCostList, globalOperatorList, operatorsName);

        appendSheets(fileName, instanceName, observer);




        globalOperatorList = List.of(
                new NeighborhoodChangeHostGlobal(problem.getInstanceData()),
                new NeighborhoodInsertionGlobal(),
                new NeighborhoodSwapHostGlobal()
        );

        operatorsName = generateGlobalOperatorsListName(globalOperatorList);

        observer = globalOperatorExperiment(problem, timeLimit, periodicTimeForMakespanEvolution,
                avgMakespanList, bestKnownCostList, globalOperatorList, operatorsName);

        appendSheets(fileName, instanceName, observer);




        globalOperatorList = List.of(
                new NeighborhoodChangeHostGlobal(problem.getInstanceData()),
                new NeighborhoodSwapGlobal(),
                new NeighborhoodSwapHostGlobal()
        );

        operatorsName = generateGlobalOperatorsListName(globalOperatorList);

        observer = globalOperatorExperiment(problem, timeLimit, periodicTimeForMakespanEvolution,
                avgMakespanList, bestKnownCostList, globalOperatorList, operatorsName);

        appendSheets(fileName, instanceName, observer);




        globalOperatorList = List.of(
                new NeighborhoodInsertionGlobal(),
                new NeighborhoodSwapGlobal(),
                new NeighborhoodSwapHostGlobal()
        );

        operatorsName = generateGlobalOperatorsListName(globalOperatorList);

        observer = globalOperatorExperiment(problem, timeLimit, periodicTimeForMakespanEvolution,
                avgMakespanList, bestKnownCostList, globalOperatorList, operatorsName);

        appendSheets(fileName, instanceName, observer);




        globalOperatorList = List.of(
                new NeighborhoodChangeHostGlobal(problem.getInstanceData()),
                new NeighborhoodInsertionGlobal(),
                new NeighborhoodSwapGlobal(),
                new NeighborhoodSwapHostGlobal()
        );

        operatorsName = generateGlobalOperatorsListName(globalOperatorList);

        observer = globalOperatorExperiment(problem, timeLimit, periodicTimeForMakespanEvolution,
                avgMakespanList, bestKnownCostList, globalOperatorList, operatorsName);

        appendSheets(fileName, instanceName, observer);



        globalOperatorList = List.of(
                new NeighborhoodChangeHostGlobal(problem.getInstanceData()),
                new NeighborhoodInsertionGlobal(),
                new NeighborhoodSwapGlobal(),
                new NeighborhoodSwapHostGlobal()
        );

        operatorsName = "VNS";

        observer = globalOperatorExperiment(problem, timeLimit, periodicTimeForMakespanEvolution,
                avgMakespanList, bestKnownCostList, globalOperatorList, operatorsName);

        appendSheets(fileName, instanceName, observer);







        lazyOperatorList = List.of(
                new NeighborhoodChangeHostLazy(problem.getInstanceData())
        );

        operatorsName = generateLazyOperatorsListName(lazyOperatorList);

        observer = lazyOperatorExperiment(problem, timeLimit, periodicTimeForMakespanEvolution,
                avgMakespanList, bestKnownCostList, lazyOperatorList, operatorsName);

        appendSheets(fileName, instanceName, observer);



        lazyOperatorList = List.of(
                new NeighborhoodInsertionLazy()
        );

        operatorsName = generateLazyOperatorsListName(lazyOperatorList);

        observer = lazyOperatorExperiment(problem, timeLimit, periodicTimeForMakespanEvolution,
                avgMakespanList, bestKnownCostList, lazyOperatorList, operatorsName);

        appendSheets(fileName, instanceName, observer);




        lazyOperatorList = List.of(
                new NeighborhoodSwapLazy()
        );

        operatorsName = generateLazyOperatorsListName(lazyOperatorList);

        observer = lazyOperatorExperiment(problem, timeLimit, periodicTimeForMakespanEvolution,
                avgMakespanList, bestKnownCostList, lazyOperatorList, operatorsName);

        appendSheets(fileName, instanceName, observer);




        lazyOperatorList = List.of(
                new NeighborhoodSwapHostLazy()
        );

        operatorsName = generateLazyOperatorsListName(lazyOperatorList);

        observer = lazyOperatorExperiment(problem, timeLimit, periodicTimeForMakespanEvolution,
                avgMakespanList, bestKnownCostList, lazyOperatorList, operatorsName);

        appendSheets(fileName, instanceName, observer);




        lazyOperatorList = List.of(
                new NeighborhoodChangeHostLazy(problem.getInstanceData()),
                new NeighborhoodInsertionLazy()
        );

        operatorsName = generateLazyOperatorsListName(lazyOperatorList);

        observer = lazyOperatorExperiment(problem, timeLimit, periodicTimeForMakespanEvolution,
                avgMakespanList, bestKnownCostList, lazyOperatorList, operatorsName);

        appendSheets(fileName, instanceName, observer);




        lazyOperatorList = List.of(
                new NeighborhoodChangeHostLazy(problem.getInstanceData()),
                new NeighborhoodSwapLazy()
        );

        operatorsName = generateLazyOperatorsListName(lazyOperatorList);

        observer = lazyOperatorExperiment(problem, timeLimit, periodicTimeForMakespanEvolution,
                avgMakespanList, bestKnownCostList, lazyOperatorList, operatorsName);

        appendSheets(fileName, instanceName, observer);




        lazyOperatorList = List.of(
                new NeighborhoodChangeHostLazy(problem.getInstanceData()),
                new NeighborhoodSwapHostLazy()
        );

        operatorsName = generateLazyOperatorsListName(lazyOperatorList);

        observer = lazyOperatorExperiment(problem, timeLimit, periodicTimeForMakespanEvolution,
                avgMakespanList, bestKnownCostList, lazyOperatorList, operatorsName);

        appendSheets(fileName, instanceName, observer);




        lazyOperatorList = List.of(
                new NeighborhoodInsertionLazy(),
                new NeighborhoodSwapLazy()
        );

        operatorsName = generateLazyOperatorsListName(lazyOperatorList);

        observer = lazyOperatorExperiment(problem, timeLimit, periodicTimeForMakespanEvolution,
                avgMakespanList, bestKnownCostList, lazyOperatorList, operatorsName);

        appendSheets(fileName, instanceName, observer);




        lazyOperatorList = List.of(
                new NeighborhoodInsertionLazy(),
                new NeighborhoodSwapHostLazy()
        );

        operatorsName = generateLazyOperatorsListName(lazyOperatorList);

        observer = lazyOperatorExperiment(problem, timeLimit, periodicTimeForMakespanEvolution,
                avgMakespanList, bestKnownCostList, lazyOperatorList, operatorsName);

        appendSheets(fileName, instanceName, observer);




        lazyOperatorList = List.of(
                new NeighborhoodSwapLazy(),
                new NeighborhoodSwapHostLazy()
        );

        operatorsName = generateLazyOperatorsListName(lazyOperatorList);

        observer = lazyOperatorExperiment(problem, timeLimit, periodicTimeForMakespanEvolution,
                avgMakespanList, bestKnownCostList, lazyOperatorList, operatorsName);

        appendSheets(fileName, instanceName, observer);



        lazyOperatorList = List.of(
                new NeighborhoodChangeHostLazy(problem.getInstanceData()),
                new NeighborhoodInsertionLazy(),
                new NeighborhoodSwapLazy()
        );

        operatorsName = generateLazyOperatorsListName(lazyOperatorList);

        observer = lazyOperatorExperiment(problem, timeLimit, periodicTimeForMakespanEvolution,
                avgMakespanList, bestKnownCostList, lazyOperatorList, operatorsName);

        appendSheets(fileName, instanceName, observer);




        lazyOperatorList = List.of(
                new NeighborhoodChangeHostLazy(problem.getInstanceData()),
                new NeighborhoodInsertionLazy(),
                new NeighborhoodSwapHostLazy()
        );

        operatorsName = generateLazyOperatorsListName(lazyOperatorList);

        observer = lazyOperatorExperiment(problem, timeLimit, periodicTimeForMakespanEvolution,
                avgMakespanList, bestKnownCostList, lazyOperatorList, operatorsName);

        appendSheets(fileName, instanceName, observer);




        lazyOperatorList = List.of(
                new NeighborhoodChangeHostLazy(problem.getInstanceData()),
                new NeighborhoodSwapLazy(),
                new NeighborhoodSwapHostLazy()
        );

        operatorsName = generateLazyOperatorsListName(lazyOperatorList);

        observer = lazyOperatorExperiment(problem, timeLimit, periodicTimeForMakespanEvolution,
                avgMakespanList, bestKnownCostList, lazyOperatorList, operatorsName);

        appendSheets(fileName, instanceName, observer);




        lazyOperatorList = List.of(
                new NeighborhoodInsertionLazy(),
                new NeighborhoodSwapLazy(),
                new NeighborhoodSwapHostLazy()
        );

        operatorsName = generateLazyOperatorsListName(lazyOperatorList);

        observer = lazyOperatorExperiment(problem, timeLimit, periodicTimeForMakespanEvolution,
                avgMakespanList, bestKnownCostList, lazyOperatorList, operatorsName);

        appendSheets(fileName, instanceName, observer);




        lazyOperatorList = List.of(
                new NeighborhoodChangeHostLazy(problem.getInstanceData()),
                new NeighborhoodInsertionLazy(),
                new NeighborhoodSwapLazy(),
                new NeighborhoodSwapHostLazy()
        );

        operatorsName = generateLazyOperatorsListName(lazyOperatorList);

        observer = lazyOperatorExperiment(problem, timeLimit, periodicTimeForMakespanEvolution,
                avgMakespanList, bestKnownCostList, lazyOperatorList, operatorsName);

        appendSheets(fileName, instanceName, observer);




        lazyOperatorList = List.of(
                new NeighborhoodChangeHostLazy(problem.getInstanceData()),
                new NeighborhoodInsertionLazy(),
                new NeighborhoodSwapLazy(),
                new NeighborhoodSwapHostLazy()
        );

        operatorsName = "VNS";

        observer = lazyOperatorExperiment(problem, timeLimit, periodicTimeForMakespanEvolution,
                avgMakespanList, bestKnownCostList, lazyOperatorList, operatorsName);

        appendSheets(fileName, instanceName, observer);




        XLSXTableExporter.appendSummarySheet(fileName, instanceName, avgMakespanList, timeLimit);

        XLSXTableExporter.appendBKPercentageSheet(
                fileName,
                instanceName,
                NeighborUtils.computeBestKnownPercentageList(bestKnownCostList),
                timeLimit
        );

    }

    protected static void createSheets(String fileName, String instanceName) {

        try {
            XLSXTableExporter.createInstanceSheet(fileName, instanceName);
        }catch (RuntimeException e) {
            System.out.println("Exception's message:\n" + e.getMessage());
            throw e;
        }

        try {
            XLSXTableExporter.createMakespanEvolutionSheet(fileName, instanceName);
        }catch (RuntimeException e) {
            System.out.println("Exception's message:\n" + e.getMessage());
            throw e;
        }
    }

    protected static LocalSearchObserver globalOperatorExperiment(SchedulingProblem problem,
                                                                long timeLimit, long periodicTimeForMakespanEvolution,
                                                                List<Double> avgMakespanList,
                                                                List<Double> bestKnownCostList,
                                                                List<NeighborhoodOperatorGlobal> globalOperatorList,
                                                                String operatorsName) {

        System.out.println("\n\nGD | " + operatorsName + " operator\n\n");

        LocalSearchObserver observer = new LocalSearchObserver("GD", operatorsName, periodicTimeForMakespanEvolution);
        MaximumGradientStrategy maximumGradientStrategy = new MaximumGradientStrategy();

        for(int i = 0; i < 30; i++) {
            System.out.println("Execution number " + (i+1) + "\n");

            if(operatorsName.equalsIgnoreCase("vns")) {
                maximumGradientStrategy.executeVNS(problem, globalOperatorList, timeLimit, observer);
                continue;
            }

            maximumGradientStrategy.execute(problem, globalOperatorList, timeLimit, observer);
        }

        avgMakespanList.add(observer.getAvgMinReachedMakespan());
        bestKnownCostList.add(observer.getBestMinReachedMakespan());

        return observer;

    }

    protected static LocalSearchObserver lazyOperatorExperiment(SchedulingProblem problem,
                                                              long timeLimit, long periodicTimeForMakespanEvolution,
                                                              List<Double> avgMakespanList,
                                                              List<Double> bestKnownCostList,
                                                              List<NeighborhoodOperatorLazy> lazyOperatorList,
                                                              String operatorsName) {

        System.out.println("\n\nHC | " + operatorsName + " operator\n\n");

        LocalSearchObserver observer = new LocalSearchObserver("HC", operatorsName, periodicTimeForMakespanEvolution);
        SimpleClimbingStrategy simpleClimbingStrategy = new SimpleClimbingStrategy();

        for(int i = 0; i < 30; i++) {
            System.out.println("Execution number " + (i+1) + "\n");

            if(operatorsName.equalsIgnoreCase("vns")) {
                simpleClimbingStrategy.executeVNS(problem, lazyOperatorList, timeLimit, observer);
                continue;
            }

            simpleClimbingStrategy.execute(problem, lazyOperatorList, timeLimit, observer);
        }

        avgMakespanList.add(observer.getAvgMinReachedMakespan());
        bestKnownCostList.add(observer.getBestMinReachedMakespan());

        return observer;

    }

    protected static void appendSheets(String fileName, String instanceName, LocalSearchObserver observer) {
        XLSXTableExporter.appendInstanceSheet(fileName, instanceName, observer);
        XLSXTableExporter.appendMakespanEvolutionSheet(fileName, instanceName, observer);
    }


    protected static String generateGlobalOperatorsListName(List<NeighborhoodOperatorGlobal> globalOperatorList){

        if(globalOperatorList.isEmpty())
            return "no name";

        StringBuilder result = new StringBuilder(globalOperatorList.get(0).getName());

        if(globalOperatorList.size() == 1)
            return result.toString();

        for(NeighborhoodOperatorGlobal operator : globalOperatorList)
            result.append("u").append(operator.getName());

        return result.toString();
    }

    protected static String generateLazyOperatorsListName(List<NeighborhoodOperatorLazy> lazyOperatorList){

        if(lazyOperatorList.isEmpty())
            return "no name";

        StringBuilder result = new StringBuilder(lazyOperatorList.get(0).getName());

        if(lazyOperatorList.size() == 1)
            return result.toString();

        for(NeighborhoodOperatorLazy operator : lazyOperatorList)
            result.append("u").append(operator.getName());

        return result.toString();
    }


    /*private static void StrategiesExperiment(String instanceName, SchedulingProblem problem) {

        System.out.println("\n\nMaximum Gradient strategy\n\n");

        //Here you can change the operator
        NeighborhoodOperatorGlobal globalOperator = new NeighborhoodSwapHostGlobal();

        LocalSearchObserver observer = new LocalSearchObserver("GD", globalOperator.getName(), -1);

        MaximumGradientStrategy maximumGradientStrategy = new MaximumGradientStrategy(observer);

        for(int i = 0; i < 30; i++)
            maximumGradientStrategy.execute(problem, globalOperator);

        System.out.println(observer);

        XLSXExporter.createWorkbook("local_search_results_" + globalOperator.getName());
        XLSXExporter.appendWorkbook(observer, "local_search_results_" + globalOperator.getName());

        *//*CSVExporter.createCSV("local_search_results_" + globalOperator.getName());
        CSVExporter.appendCSV(observer, "local_search_results_" + globalOperator.getName());*//*




        System.out.println("\n\nSimple Climbing strategy\n\n");

        //Here you can change the operator
        NeighborhoodOperatorLazy lazyOperator = new NeighborhoodSwapHostLazy();

        observer = new LocalSearchObserver("HC", lazyOperator.getName(), -1);

        SimpleClimbingStrategy simpleClimbingStrategy = new SimpleClimbingStrategy(observer);

        for(int i = 0; i < 30; i++)
            simpleClimbingStrategy.execute(problem, lazyOperator);

        System.out.println(observer);

        XLSXExporter.appendWorkbook(observer, "local_search_results_" + globalOperator.getName());

        //CSVExporter.appendCSV(observer, "local_search_results_" + globalOperator.getName());
    }*/
}
