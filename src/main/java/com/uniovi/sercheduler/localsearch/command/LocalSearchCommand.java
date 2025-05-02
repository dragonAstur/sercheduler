package com.uniovi.sercheduler.localsearch.command;

import com.uniovi.sercheduler.dao.Objective;
import com.uniovi.sercheduler.jmetal.problem.SchedulingProblem;
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

        SchedulingProblem problem =
                new SchedulingProblem(
                        new File(workflowFile),
                        new File(hostsFile),
                        "441Gf",
                        "simple",
                        1L,
                        objectives,
                        Objective.MAKESPAN.objectiveName);

        System.out.println("\n\nMaximum Gradient strategy\n\n");

        NeighborhoodObserver observer = new NeighborhoodObserver("Maximum gradient");

        MaximumGradientStrategy maximumGradientStrategy = new MaximumGradientStrategy(observer);

        //Here you can change the operator
        NeighborhoodOperatorGlobal globalOperator = new NeighborhoodSwapGlobal();

        for(int i = 0; i < 100; i++){
            maximumGradientStrategy.execute(problem, globalOperator);
        }

        System.out.println(observer);

        System.out.println("\n\nSimple Climbing strategy\n\n");

        observer = new NeighborhoodObserver("Simple climbing");

        SimpleClimbingStrategy simpleClimbingStrategy = new SimpleClimbingStrategy(observer);

        //Here you can change the operator
        NeighborhoodOperatorLazy lazyOperator = new NeighborhoodSwapLazy();

        for(int i = 0; i < 30; i++){
            simpleClimbingStrategy.execute(problem, lazyOperator);
        }

        System.out.println(observer);

    }
}
