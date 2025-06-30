package com.uniovi.sercheduler.localsearch.command;

import com.uniovi.sercheduler.dao.Objective;
import com.uniovi.sercheduler.jmetal.problem.SchedulingProblem;
import org.springframework.shell.command.annotation.Command;
import org.springframework.shell.command.annotation.Option;

import java.io.File;
import java.util.List;

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
            @Option(shortNames = 'C', defaultValue = "false") boolean createFile) {

        List<Objective> objectives = List.of(Objective.MAKESPAN, Objective.ENERGY);

        String hostsFileName = LocalSearchRunnable.getFileName(hostsFile);

        String workflowFileName = LocalSearchRunnable.getFileName(workflowFile);

        String instanceName = workflowFileName + "_" + hostsFileName + "_" + timeLimit;

        long seed = System.nanoTime();

        SchedulingProblem problem =
                new SchedulingProblem(
                        new File(workflowFileName),
                        new File(hostsFileName),
                        "441Gf",
                        "simple",
                        seed,
                        objectives,
                        Objective.MAKESPAN.objectiveName,
                        1);

        LocalSearchRunnable.operatorsExperiment(instanceName, problem, timeLimit, createFile);
    }
}
