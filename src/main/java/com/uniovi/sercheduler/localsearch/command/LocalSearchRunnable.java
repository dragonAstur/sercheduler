package com.uniovi.sercheduler.localsearch.command;

import com.uniovi.sercheduler.SerchedulerApplication;
import com.uniovi.sercheduler.dao.Objective;
import com.uniovi.sercheduler.jmetal.problem.SchedulingProblem;
import com.uniovi.sercheduler.localsearch.observer.NeighborhoodObserver;
import com.uniovi.sercheduler.localsearch.operator.NeighborhoodInsertionGlobal;
import com.uniovi.sercheduler.localsearch.operator.NeighborhoodOperatorGlobal;
import com.uniovi.sercheduler.localsearch.strategy.MaximumGradientStrategy;
import org.springframework.boot.Banner;
import org.springframework.boot.SpringApplication;

import java.io.File;
import java.util.List;

public class LocalSearchRunnable {

    public static final String WORFLOWFILE = "src/test/resources/montage.json";
    public static final String HOSTSFILE = "src/test/resources/hosts_test.json";



    public static void main(String[] args) {

        List<Objective> objectives = List.of(Objective.MAKESPAN, Objective.ENERGY);

        SchedulingProblem problem =
                new SchedulingProblem(
                        new File(WORFLOWFILE),
                        new File(HOSTSFILE),
                        "441Gf",
                        "simple",
                        154L,
                        objectives,
                        Objective.MAKESPAN.objectiveName);

        NeighborhoodObserver observer = new NeighborhoodObserver();
        MaximumGradientStrategy maximumGradientStrategy = new MaximumGradientStrategy(observer);

        NeighborhoodOperatorGlobal operator = new NeighborhoodInsertionGlobal();

        maximumGradientStrategy.execute(problem, operator);

        System.out.println(observer);
    }
}
