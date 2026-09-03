package com.example.seed.dto;

public class ClassroomEconomyDashboardResponse {

    private final ClassroomEconomyKpiResponse kpis;

    public ClassroomEconomyDashboardResponse(
            ClassroomEconomyKpiResponse kpis
    ) {
        this.kpis = kpis;
    }

    public ClassroomEconomyKpiResponse getKpis() {
        return kpis;
    }
}