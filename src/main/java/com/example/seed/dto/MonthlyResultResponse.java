package com.example.seed.dto;

import java.math.BigDecimal;

public class MonthlyResultResponse {

    private final String yearMonth;
    private final Integer totalIncome;
    private final Integer totalDeductions;
    private final Integer netIncome;
    private final Integer totalConsumption;
    private final Integer totalSavings;
    private final Integer balance;
    private final Integer totalAssets;
    private final BigDecimal savingsRate;
    private final Integer assetChange;

    public MonthlyResultResponse(
            String yearMonth,
            Integer totalIncome,
            Integer totalDeductions,
            Integer netIncome,
            Integer totalConsumption,
            Integer totalSavings,
            Integer balance,
            Integer totalAssets,
            BigDecimal savingsRate,
            Integer assetChange
    ) {
        this.yearMonth = yearMonth;
        this.totalIncome = totalIncome;
        this.totalDeductions = totalDeductions;
        this.netIncome = netIncome;
        this.totalConsumption = totalConsumption;
        this.totalSavings = totalSavings;
        this.balance = balance;
        this.totalAssets = totalAssets;
        this.savingsRate = savingsRate;
        this.assetChange = assetChange;
    }

    public String getYearMonth() {
        return yearMonth;
    }

    public Integer getTotalIncome() {
        return totalIncome;
    }

    public Integer getTotalDeductions() {
        return totalDeductions;
    }

    public Integer getNetIncome() {
        return netIncome;
    }

    public Integer getTotalConsumption() {
        return totalConsumption;
    }

    public Integer getTotalSavings() {
        return totalSavings;
    }

    public Integer getBalance() {
        return balance;
    }

    public Integer getTotalAssets() {
        return totalAssets;
    }

    public BigDecimal getSavingsRate() {
        return savingsRate;
    }

    public Integer getAssetChange() {
        return assetChange;
    }
}