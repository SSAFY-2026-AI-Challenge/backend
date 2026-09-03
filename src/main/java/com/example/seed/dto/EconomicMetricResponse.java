package com.example.seed.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class EconomicMetricResponse {

    private final Integer totalMoney;
    private final BigDecimal averageAsset;
    private final Integer weeklyTransactionVolume;
    private final BigDecimal averageConsumption;
    private final BigDecimal consumptionChangeRate;
    private final BigDecimal transactionChangeRate;
    private final BigDecimal savingRate;
    private final BigDecimal wealthGap;
    private final LocalDateTime measuredAt;

    public EconomicMetricResponse(
            Integer totalMoney,
            BigDecimal averageAsset,
            Integer weeklyTransactionVolume,
            BigDecimal averageConsumption,
            BigDecimal consumptionChangeRate,
            BigDecimal transactionChangeRate,
            BigDecimal savingRate,
            BigDecimal wealthGap,
            LocalDateTime measuredAt
    ) {
        this.totalMoney = totalMoney;
        this.averageAsset = averageAsset;
        this.weeklyTransactionVolume = weeklyTransactionVolume;
        this.averageConsumption = averageConsumption;
        this.consumptionChangeRate = consumptionChangeRate;
        this.transactionChangeRate = transactionChangeRate;
        this.savingRate = savingRate;
        this.wealthGap = wealthGap;
        this.measuredAt = measuredAt;
    }

    public Integer getTotalMoney() {
        return totalMoney;
    }

    public BigDecimal getAverageAsset() {
        return averageAsset;
    }

    public Integer getWeeklyTransactionVolume() {
        return weeklyTransactionVolume;
    }

    public BigDecimal getAverageConsumption() {
        return averageConsumption;
    }

    public BigDecimal getConsumptionChangeRate() {
        return consumptionChangeRate;
    }

    public BigDecimal getTransactionChangeRate() {
        return transactionChangeRate;
    }

    public BigDecimal getSavingRate() {
        return savingRate;
    }

    public BigDecimal getWealthGap() {
        return wealthGap;
    }

    public LocalDateTime getMeasuredAt() {
        return measuredAt;
    }
}