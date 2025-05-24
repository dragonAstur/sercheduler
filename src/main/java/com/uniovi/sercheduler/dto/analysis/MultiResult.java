package com.uniovi.sercheduler.dto.analysis;

import com.uniovi.sercheduler.dao.Objective;

public record MultiResult(double makespan, double energy, String fitness, String objective) {}
