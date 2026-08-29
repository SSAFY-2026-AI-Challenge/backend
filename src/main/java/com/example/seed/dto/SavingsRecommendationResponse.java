package com.example.seed.dto;

import java.util.List;

public record SavingsRecommendationResponse(
        List<Recommendation> recommendations
) {

    public record Recommendation(
            Integer id,
            String type,
            String content,
            String expectedEffect,
            Boolean isApplied
    ) {
    }
}