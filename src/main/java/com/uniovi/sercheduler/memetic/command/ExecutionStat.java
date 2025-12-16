package com.uniovi.sercheduler.memetic.command;

import java.util.DoubleSummaryStatistics;

public record ExecutionStat(
        String executionName,
        String workflow,
        String algorithm,
        DoubleSummaryStatistics statistics) {}
