package com.uniovi.sercheduler.localsearch.command;

import com.uniovi.sercheduler.dao.Objective;
import com.uniovi.sercheduler.jmetal.problem.SchedulingProblem;
import com.uniovi.sercheduler.localsearch.observer.NeighborhoodObserver;
import com.uniovi.sercheduler.localsearch.operator.NeighborhoodInsertionGlobal;
import com.uniovi.sercheduler.localsearch.operator.NeighborhoodOperatorGlobal;
import com.uniovi.sercheduler.localsearch.strategy.MaximumGradientStrategy;
import org.springframework.shell.command.annotation.Command;
import org.springframework.shell.command.annotation.Option;

import java.io.File;
import java.util.List;

//Terminal command for executing:
//java -jar target/sercheduler-0.0.1-SNAPSHOT.jar localsearch --workflowFile src/test/resources/montage.json --hostsFile  src/test/resources/hosts_test.json

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

        NeighborhoodObserver neighborhoodObserver = new NeighborhoodObserver();
        MaximumGradientStrategy maximumGradientStrategy = new MaximumGradientStrategy(neighborhoodObserver);

        NeighborhoodOperatorGlobal operator = new NeighborhoodInsertionGlobal();

        maximumGradientStrategy.execute(problem, operator);

    }
}
