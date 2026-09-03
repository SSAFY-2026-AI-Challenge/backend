package com.example.seed.dto;

public class ClassroomEconomyKpiResponse {

    private final Integer moneySupply;
    private final Integer averageBalance;
    private final Integer totalConsumption;
    private final Integer totalSavings;
    private final Integer transactionVolume;

    public ClassroomEconomyKpiResponse(
            Integer moneySupply,
            Integer averageBalance,
            Integer totalConsumption,
            Integer totalSavings,
            Integer transactionVolume
    ) {
        this.moneySupply = moneySupply;
        this.averageBalance = averageBalance;
        this.totalConsumption = totalConsumption;
        this.totalSavings = totalSavings;
        this.transactionVolume = transactionVolume;
    }

    public Integer getMoneySupply() {
        return moneySupply;
    }

    public Integer getAverageBalance() {
        return averageBalance;
    }

    public Integer getTotalConsumption() {
        return totalConsumption;
    }

    public Integer getTotalSavings() {
        return totalSavings;
    }

    public Integer getTransactionVolume() {
        return transactionVolume;
    }
}