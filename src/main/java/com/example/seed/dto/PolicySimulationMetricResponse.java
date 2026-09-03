package com.example.seed.dto;

import java.math.BigDecimal;

public class PolicySimulationMetricResponse {

    private final Integer moneySupply;
    private final BigDecimal totalConsumption;
    private final BigDecimal inflationRate;
    private final BigDecimal consumptionGrowthRate;
    private final String economicStatus;

    public PolicySimulationMetricResponse(
            Integer moneySupply,
            BigDecimal totalConsumption,
            BigDecimal inflationRate,
            BigDecimal consumptionGrowthRate,
            String economicStatus
    ) {
        this.moneySupply = moneySupply;
        this.totalConsumption = totalConsumption;
        this.inflationRate = inflationRate;
        this.consumptionGrowthRate = consumptionGrowthRate;
        this.economicStatus = economicStatus;
    }

    public Integer getMoneySupply() {
        return moneySupply;
    }

    public BigDecimal getTotalConsumption() {
        return totalConsumption;
    }

    public BigDecimal getInflationRate() {
        return inflationRate;
    }

    public BigDecimal getConsumptionGrowthRate() {
        return consumptionGrowthRate;
    }

    public String getEconomicStatus() {
        return economicStatus;
    }
}