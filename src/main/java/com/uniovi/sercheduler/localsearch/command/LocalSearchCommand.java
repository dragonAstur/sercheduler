package com.uniovi.sercheduler.localsearch.command;

import com.uniovi.sercheduler.dao.Objective;
import com.uniovi.sercheduler.jmetal.problem.SchedulingProblem;
import com.uniovi.sercheduler.localsearch.export.CSVExporter;
import com.uniovi.sercheduler.localsearch.export.XLSXExporter;
import com.uniovi.sercheduler.localsearch.observer.NeighborhoodObserver;
import com.uniovi.sercheduler.localsearch.operator.*;
import com.uniovi.sercheduler.localsearch.strategy.MaximumGradientStrategy;
import com.uniovi.sercheduler.localsearch.strategy.SimpleClimbingStrategy;
import org.springframework.shell.command.annotation.Command;
import org.springframework.shell.command.annotation.Option;

import java.io.File;
import java.util.List;

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

        List<Objective> objectives = List.of(Objective.MAKESPAN, Objective.ENERGY);

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

        NeighborhoodObserver observer = new NeighborhoodObserver("DHC");

        MaximumGradientStrategy maximumGradientStrategy = new MaximumGradientStrategy(observer);

        //Here you can change the operator
        NeighborhoodOperatorGlobal globalOperator = new NeighborhoodSwapGlobal();

        for(int i = 0; i < 30; i++)
            maximumGradientStrategy.execute(problem, globalOperator);

        System.out.println(observer);

        XLSXExporter.createWorkbook("local_search_results");
        XLSXExporter.appendWorkbook(observer, "local_search_results");

        CSVExporter.createCSV("local_search_results");
        CSVExporter.appendCSV(observer, "local_search_results");

        System.out.println("\n\nSimple Climbing strategy\n\n");

        observer = new NeighborhoodObserver("HC");

        SimpleClimbingStrategy simpleClimbingStrategy = new SimpleClimbingStrategy(observer);

        //Here you can change the operator
        NeighborhoodOperatorLazy lazyOperator = new NeighborhoodSwapLazy();

        for(int i = 0; i < 30; i++)
            simpleClimbingStrategy.execute(problem, lazyOperator);

        System.out.println(observer);

        XLSXExporter.appendWorkbook(observer, "local_search_results");

        CSVExporter.appendCSV(observer, "local_search_results");

    }
}
