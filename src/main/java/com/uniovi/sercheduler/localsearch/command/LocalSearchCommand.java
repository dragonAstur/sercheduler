package com.uniovi.sercheduler.localsearch.command;

import com.uniovi.sercheduler.dao.Objective;
import com.uniovi.sercheduler.expception.HostLoadException;
import com.uniovi.sercheduler.expception.WorkflowLoadException;
import com.uniovi.sercheduler.jmetal.problem.SchedulingProblem;
import com.uniovi.sercheduler.localsearch.observer.LocalSearchObserver;
import com.uniovi.sercheduler.localsearch.operator.*;
import org.springframework.shell.command.annotation.Command;
import org.springframework.shell.command.annotation.Option;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static com.uniovi.sercheduler.localsearch.command.LocalSearchRunnable.*;

//Terminal command for executing the local search algorithm:
//java -jar target/sercheduler-0.0.1-SNAPSHOT.jar localsearch --workflowFile src/test/resources/montage.json --hostsFile  src/test/resources/hosts_test.json

//Terminal command for executing genetic algorithm:
//java -jar target/sercheduler-0.0.1-SNAPSHOT.jar evaluate --workflowFile src/test/resources/montage.json --hostsFile  src/test/resources/hosts_test.json --seed 1 --executions 1000000 --fitness heft

@Command
public class LocalSearchCommand {

    @Command(command = "localsearch")
    public void localsearch(
            @Option(shortNames = 'H', required = true) String hostsFile,
            @Option(shortNames = 'W', required = true) String workflowFile,
            @Option(shortNames = 'T', defaultValue = "5000") long timeLimit,
            @Option(shortNames = 'C', defaultValue = "false") boolean createFile,
            @Option(shortNames = 'P', defaultValue = "-1") long periodicTimeForMakespanEvolution,
            @Option(shortNames = 'N', defaultValue = "null") String instanceName,
            @Option(shortNames = 'F', defaultValue = "null") String fileName,
            @Option(shortNames = 'S', defaultValue = "all") String strategy,
            @Option(shortNames = 'O', defaultValue = "null") String operatorConfig
            ) {

        List<Objective> objectives = List.of(Objective.MAKESPAN, Objective.ENERGY);

        String workflowFileName = LocalSearchRunnable.getFileName(workflowFile);

        String hostsFileName = LocalSearchRunnable.getFileName(hostsFile);

        instanceName = instanceName.equals("null") ?
                workflowFileName + "_" + hostsFileName + "_" + timeLimit
                : instanceName;

        fileName = fileName.equals("null") ?
                instanceName
                : fileName;

        long seed = System.nanoTime();

        SchedulingProblem problem;

        try {
            problem =
                    new SchedulingProblem(
                            new File(workflowFile),
                            new File(hostsFile),
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

        switch(strategy){
            case "all":
                LocalSearchRunnable.operatorsExperiment(fileName, instanceName, problem, timeLimit, createFile, periodicTimeForMakespanEvolution);
                break;
            case "GD":
            case "gd":

                List<NeighborhoodOperatorGlobal> globalOperatorList =
                switch (operatorConfig) {
                    case "N1" -> List.of(new NeighborhoodChangeHostGlobal(problem.getInstanceData()));
                    case "N2" -> List.of(new NeighborhoodInsertionGlobal());
                    case "N3" -> List.of(new NeighborhoodSwapGlobal());
                    case "N4" -> List.of(new NeighborhoodSwapHostGlobal());
                    case "N1uN2" -> List.of(
                            new NeighborhoodChangeHostGlobal(problem.getInstanceData()),
                            new NeighborhoodInsertionGlobal()
                    );
                    case "N1uN3" -> List.of(
                            new NeighborhoodChangeHostGlobal(problem.getInstanceData()),
                            new NeighborhoodSwapGlobal()
                    );
                    case "N1uN4" -> List.of(
                            new NeighborhoodChangeHostGlobal(problem.getInstanceData()),
                            new NeighborhoodSwapHostGlobal()
                    );
                    case "N2uN3" -> List.of(
                            new NeighborhoodInsertionGlobal(),
                            new NeighborhoodSwapGlobal()
                    );
                    case "N2uN4" -> List.of(
                            new NeighborhoodInsertionGlobal(),
                            new NeighborhoodSwapHostGlobal()
                    );
                    case "N3uN4" -> List.of(
                            new NeighborhoodSwapGlobal(),
                            new NeighborhoodSwapHostGlobal()
                    );
                    case "N1uN2uN3" -> List.of(
                            new NeighborhoodChangeHostGlobal(problem.getInstanceData()),
                            new NeighborhoodInsertionGlobal(),
                            new NeighborhoodSwapGlobal()
                    );
                    case "N1uN2uN4" -> List.of(
                            new NeighborhoodChangeHostGlobal(problem.getInstanceData()),
                            new NeighborhoodInsertionGlobal(),
                            new NeighborhoodSwapHostGlobal()
                    );
                    case "N1uN3uN4" -> List.of(
                            new NeighborhoodChangeHostGlobal(problem.getInstanceData()),
                            new NeighborhoodSwapGlobal(),
                            new NeighborhoodSwapHostGlobal()
                    );
                    case "N2uN3uN4" -> List.of(
                            new NeighborhoodInsertionGlobal(),
                            new NeighborhoodSwapGlobal(),
                            new NeighborhoodSwapHostGlobal()
                    );
                    case "N1uN2uN3uN4", "VNS" -> List.of(
                            new NeighborhoodChangeHostGlobal(problem.getInstanceData()),
                            new NeighborhoodInsertionGlobal(),
                            new NeighborhoodSwapGlobal(),
                            new NeighborhoodSwapHostGlobal()
                    );
                    default -> new ArrayList<>();
                };

                if(globalOperatorList.isEmpty())
                    System.out.println("Please define a valid operator configuration");


                LocalSearchObserver observer = globalOperatorExperiment(problem, timeLimit, periodicTimeForMakespanEvolution,
                        new ArrayList<>(), new ArrayList<>(), globalOperatorList, operatorConfig);

                //TODO
                break;
            case "HC":
            case "hc":

                List<NeighborhoodOperatorLazy> lazyOperatorList =
                        switch (operatorConfig) {
                            case "N1" -> List.of(
                                    new NeighborhoodChangeHostLazy(problem.getInstanceData())
                            );
                            case "N2" -> List.of(
                                    new NeighborhoodInsertionLazy()
                            );
                            case "N3" -> List.of(
                                    new NeighborhoodSwapLazy()
                            );
                            case "N4" -> List.of(
                                    new NeighborhoodSwapHostLazy()
                            );
                            case "N1uN2" -> List.of(
                                    new NeighborhoodChangeHostLazy(problem.getInstanceData()),
                                    new NeighborhoodInsertionLazy()
                            );

                            case "N1uN3" -> List.of(
                                    new NeighborhoodChangeHostLazy(problem.getInstanceData()),
                                    new NeighborhoodSwapLazy()
                            );
                            case "N1uN4" -> List.of(
                                    new NeighborhoodChangeHostLazy(problem.getInstanceData()),
                                    new NeighborhoodSwapHostLazy()
                            );
                            case "N2uN3" -> List.of(
                                    new NeighborhoodInsertionLazy(),
                                    new NeighborhoodSwapLazy()
                            );

                            case "N2uN4" -> List.of(
                                    new NeighborhoodInsertionLazy(),
                                    new NeighborhoodSwapHostLazy()
                            );
                            case "N3uN4" -> List.of(
                                    new NeighborhoodSwapLazy(),
                                    new NeighborhoodSwapHostLazy()
                            );
                            case "N1uN2uN3" -> List.of(
                                    new NeighborhoodChangeHostLazy(problem.getInstanceData()),
                                    new NeighborhoodInsertionLazy(),
                                    new NeighborhoodSwapLazy()
                            );
                            case "N1uN2uN4" -> List.of(
                                    new NeighborhoodChangeHostLazy(problem.getInstanceData()),
                                    new NeighborhoodInsertionLazy(),
                                    new NeighborhoodSwapHostLazy()
                            );
                            case "N1uN3uN4" -> List.of(
                                    new NeighborhoodChangeHostLazy(problem.getInstanceData()),
                                    new NeighborhoodSwapLazy(),
                                    new NeighborhoodSwapHostLazy()
                            );
                            case "N2uN3uN4" -> List.of(
                                    new NeighborhoodInsertionLazy(),
                                    new NeighborhoodSwapLazy(),
                                    new NeighborhoodSwapHostLazy()
                            );
                            case "N1uN2uN3uN4", "VNS" -> List.of(
                                    new NeighborhoodChangeHostLazy(problem.getInstanceData()),
                                    new NeighborhoodInsertionLazy(),
                                    new NeighborhoodSwapLazy(),
                                    new NeighborhoodSwapHostLazy()
                            );
                            default -> new ArrayList<>();
                        };

                if(lazyOperatorList.isEmpty())
                    System.out.println("Please define a valid operator configuration");

                lazyOperatorExperiment(problem, timeLimit, periodicTimeForMakespanEvolution,
                        new ArrayList<>(), new ArrayList<>(), lazyOperatorList, operatorConfig);

                //TODO

                break;
            default:
                System.out.println("Please define a valid strategy");
                break;
        }

    }
}
