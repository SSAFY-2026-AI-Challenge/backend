package com.example.seed.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class EconomicMetricTrendResponse {

    private final Integer totalMoney;
    private final Integer weeklyTransactionVolume;
    private final BigDecimal averageConsumption;
    private final BigDecimal savingRate;
    private final LocalDateTime measuredAt;

    public EconomicMetricTrendResponse(
            Integer totalMoney,
            Integer weeklyTransactionVolume,
            BigDecimal averageConsumption,
            BigDecimal savingRate,
            LocalDateTime measuredAt
    ) {
        this.totalMoney = totalMoney;
        this.weeklyTransactionVolume = weeklyTransactionVolume;
        this.averageConsumption = averageConsumption;
        this.savingRate = savingRate;
        this.measuredAt = measuredAt;
    }

    public Integer getTotalMoney() {
        return totalMoney;
    }

    public Integer getWeeklyTransactionVolume() {
        return weeklyTransactionVolume;
    }

    public BigDecimal getAverageConsumption() {
        return averageConsumption;
    }

    public BigDecimal getSavingRate() {
        return savingRate;
    }

    public LocalDateTime getMeasuredAt() {
        return measuredAt;
    }
}