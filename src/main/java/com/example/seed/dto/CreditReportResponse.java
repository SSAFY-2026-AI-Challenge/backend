package com.example.seed.dto;

import java.util.List;

public record CreditReportResponse (
    String yearMonth,
    int score,
    int maxScore,
    String grade,
    List<Factor> factors,
    List<BehaviorMetric> behaviorMetrics,
    String summary
) {
    public record Factor(
            String type,
            String label,
            String impact
    ) {

    } public record BehaviorMetric(
            String key,
            String label,
            int value
    ){

    }
}
