package com.uniovi.sercheduler.dto.analysis;

public record GenerationInfo(
    double makespan,
    double energy,
    int objectiveEnergyCount,
    int objectiveMakespanCount,
    int simpleEnergyCount,
    int function1EnergyCount,
    int function2EnergyCount,
    int simpleMakespanCount,
    int function1MakespanCount,
    int function2MakespanCount) {}
