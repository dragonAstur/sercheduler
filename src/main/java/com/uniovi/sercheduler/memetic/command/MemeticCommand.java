package com.uniovi.sercheduler.memetic.command;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.shell.command.annotation.Command;
import org.springframework.shell.command.annotation.Option;

public class MemeticCommand {

    static final Logger LOG = LoggerFactory.getLogger(GeneticCommand.class);


    @Command(command = "memetic")
    public String experiment(
            @Option(shortNames = 'W') String workflowsPath,
            @Option(shortNames = 'H') String hostsPath,
            @Option(shortNames = 'T') String type,
            @Option(shortNames = 'L', defaultValue = "10000") Long limitTime,
            @Option(shortNames = 'S', defaultValue = "1") Long seed,
            @Option(shortNames = 'X', defaultValue = ".") String experimentPath,
            @Option(shortNames = 'C') String experimentConfigFile,
            @Option(shortNames = 'E', defaultValue = "-1") long periodicTimeForMakespanEvolution,
            @Option(shortNames = 'N', defaultValue = "null") String instanceName,
            @Option(shortNames = 'O', defaultValue = "null") String operatorConfig) {

        return executeMemetic(workflowsPath, hostsPath, type, limitTime, seed, experimentPath, experimentConfigFile,
                periodicTimeForMakespanEvolution, instanceName, operatorConfig);
    }

    public static String executeMemetic(String workflowsPath, String hostsPath, String type, Long limitTime, Long seed,
                                        String experimentPath, String experimentConfigFile, long periodicTimeForMakespanEvolution,
                                        String instanceName, String operatorConfig) {
        return "";
    }
}
