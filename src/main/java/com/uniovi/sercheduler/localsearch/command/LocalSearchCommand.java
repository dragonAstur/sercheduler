package com.uniovi.sercheduler.localsearch.command;

import com.uniovi.sercheduler.dao.Objective;
import com.uniovi.sercheduler.expception.HostLoadException;
import com.uniovi.sercheduler.expception.WorkflowLoadException;
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

        String workflowFileName = LocalSearchRunnable.getFileName(workflowFile);

        String hostsFileName = LocalSearchRunnable.getFileName(hostsFile);

        String instanceName = workflowFileName + "_" + hostsFileName + "_" + timeLimit;

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

        LocalSearchRunnable.operatorsExperiment(instanceName, problem, timeLimit, createFile);
    }
}
