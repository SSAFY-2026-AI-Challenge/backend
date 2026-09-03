package com.example.seed.dto;

import java.util.List;

public class ClassroomEconomyIndicatorsResponse {

    private final EconomicMetricResponse latest;
    private final List<EconomicMetricTrendResponse> trends;

    public ClassroomEconomyIndicatorsResponse(
            EconomicMetricResponse latest,
            List<EconomicMetricTrendResponse> trends
    ) {
        this.latest = latest;
        this.trends = trends;
    }

    public EconomicMetricResponse getLatest() {
        return latest;
    }

    public List<EconomicMetricTrendResponse> getTrends() {
        return trends;
    }
}