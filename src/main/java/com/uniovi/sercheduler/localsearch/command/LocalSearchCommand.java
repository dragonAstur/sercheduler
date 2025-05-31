package com.uniovi.sercheduler.localsearch.command;

import org.springframework.shell.command.annotation.Command;
import org.springframework.shell.command.annotation.Option;

//Terminal command for executing:
//java -jar target/sercheduler-0.0.1-SNAPSHOT.jar localsearch --workflowFile src/test/resources/montage.json --hostsFile  src/test/resources/hosts_test.json

//Terminal command for executing genetic algorithm:
//java -jar target/sercheduler-0.0.1-SNAPSHOT.jar evaluate --workflowFile src/test/resources/montage.json --hostsFile  src/test/resources/hosts_test.json --seed 1 --executions 1000000 --fitness heft

@Command
public class LocalSearchCommand {

    @Command(command = "localsearch")
    public void localsearch(
            @Option(shortNames = 'H', required = true) String hostsFile,
            @Option(shortNames = 'W', required = true) String workflowFile) {

        /*List<Objective> objectives = List.of(Objective.MAKESPAN, Objective.ENERGY);

        long seed = System.nanoTime();

        SchedulingProblem problem =
                new SchedulingProblem(
                        new File(workflowFile),
                        new File(hostsFile),
                        "441Gf",
                        "simple",
                        seed,
                        objectives,
                        Objective.MAKESPAN.objectiveName);

        System.out.println("\n\nMaximum Gradient strategy\n\n");

        //Here you can change the operator
        NeighborhoodOperatorGlobal globalOperator = new NeighborhoodSwapGlobal();

        NeighborhoodObserver observer = new NeighborhoodObserver("DHC", globalOperator.getName());

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
        NeighborhoodOperatorLazy lazyOperator = new NeighborhoodSwapLazy();

        observer = new NeighborhoodObserver("HC", lazyOperator.getName());

        SimpleClimbingStrategy simpleClimbingStrategy = new SimpleClimbingStrategy(observer);

        for(int i = 0; i < 30; i++)
            simpleClimbingStrategy.execute(problem, lazyOperator);

        System.out.println(observer);

        XLSXExporter.appendWorkbook(observer, "local_search_results_" + globalOperator.getName());

        CSVExporter.appendCSV(observer, "local_search_results_" + globalOperator.getName());*/

    }
}
