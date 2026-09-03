package com.example.seed.dto;

import java.time.LocalDateTime;

public record CreditReportResponse(
        Integer creditScore,
        String summary,
        String content,
        String expectedEffect,
        Object features,
        LocalDateTime generatedAt
) {
}
