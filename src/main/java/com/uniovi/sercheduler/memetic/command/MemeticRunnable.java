package com.uniovi.sercheduler.memetic.command;

import static com.uniovi.sercheduler.memetic.command.MemeticCommand.executeMemetic;

public class MemeticRunnable {

    public static final String WORKFLOWS_PATH = "experiments/pelayo/1/workflows/";
    public static final String HOSTS_PATH = "experiments/pelayo/1/hosts/";
    public static String TYPE = "scenario1";
    public static Long LIMIT_TIME = 10000L;
    public static Long SEED = 0L;
    public static String EXPERIMENT_PATH = "experiments/pelayo/1";
    public static String EXPERIMENT_CONFIG = "experiments/pelayo/1/experimentConfig.json";
    public static final String OPERATOR_CONFIG = "vns";
    public static final long PERIODIC_TIME = 100;
    public static final String FILE_NAME = "experiment";
    public static final int LSA_ITERATIONS_LIMIT = 3;

    public static final String LSA_APPLIER_NAME = "e";


    public static void main(String[] args) {

        executeMemetic(WORKFLOWS_PATH, HOSTS_PATH, TYPE, LIMIT_TIME, SEED, EXPERIMENT_PATH, EXPERIMENT_CONFIG,
                PERIODIC_TIME, FILE_NAME, OPERATOR_CONFIG, LSA_ITERATIONS_LIMIT, LSA_APPLIER_NAME);

    }
}
