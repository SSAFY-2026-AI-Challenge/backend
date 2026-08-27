package com.example.seed.dto;

import java.time.LocalDateTime;
import java.util.List;

public record StudentDashboardResponse(
        String period,
        String job,
        Integer incomeThisMonth,
        Integer expenseThisMonth,
        Integer totalAssets,
        Integer savingsBalance,
        Double savingsRate,
        Credit credit,
        List<RecentTransaction> recentTransactions
) {

    public record Credit(
            Integer score,
            String grade
    ) {
    }

    public record RecentTransaction(
            Integer id,
            LocalDateTime occurredAt,
            String description,
            Integer amount
    ) {
    }
}