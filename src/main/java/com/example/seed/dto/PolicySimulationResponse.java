package com.example.seed.dto;

import java.util.List;

public class PolicySimulationResponse {

    private final PolicySimulationMetricResponse before;
    private final PolicySimulationMetricResponse after;
    private final List<String> changes;

    public PolicySimulationResponse(
            PolicySimulationMetricResponse before,
            PolicySimulationMetricResponse after,
            List<String> changes
    ) {
        this.before = before;
        this.after = after;
        this.changes = changes;
    }

    public PolicySimulationMetricResponse getBefore() {
        return before;
    }

    public PolicySimulationMetricResponse getAfter() {
        return after;
    }

    public List<String> getChanges() {
        return changes;
    }
}