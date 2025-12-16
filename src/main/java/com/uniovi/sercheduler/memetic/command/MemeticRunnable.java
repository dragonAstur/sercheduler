package com.uniovi.sercheduler.memetic.command;

import static com.uniovi.sercheduler.memetic.command.MemeticCommand.executeMemetic;

public class MemeticRunnable {

    public static final String WORKFLOWS_PATH = "experiments/pelayo/1/workflows/";
    public static final String HOSTS_PATH = "experiments/pelayo/1/hosts/";
    public static String TYPE = "scenario1";
    public static Long LIMIT_TIME = 10000L;
    public static Long SEED = 1L;
    public static String EXPERIMENT_PATH = "experiments/pelayo/1";
    public static String EXPERIMENT_CONFIG = "experiments/pelayo/1/experimentConfig.json";
    public static final String OPERATOR_CONFIG = "N3";
    public static final long PERIODIC_TIME = 100;
    public static final String FILE_NAME = "memetic";


    public static void main(String[] args) {

        executeMemetic(WORKFLOWS_PATH, HOSTS_PATH, TYPE, LIMIT_TIME, SEED, EXPERIMENT_PATH, EXPERIMENT_CONFIG,
                PERIODIC_TIME, FILE_NAME, OPERATOR_CONFIG);

    }
}
