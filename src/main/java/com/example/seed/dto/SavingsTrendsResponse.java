package com.example.seed.dto;

import java.util.List;

public record SavingsTrendsResponse(
        List<MonthlySaving> trends
) {

    public record MonthlySaving(
            String yearMonth,
            Integer amount
    ) {
    }
}