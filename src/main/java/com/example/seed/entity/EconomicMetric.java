package com.example.seed.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "economic_metric")
public class EconomicMetric {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "class_id", nullable = false)
    private Integer classId;

    @Column(name = "total_money", nullable = false)
    private Integer totalMoney;

    @Column(name = "average_asset", nullable = false, precision = 12, scale = 2)
    private BigDecimal averageAsset;

    @Column(name = "weekly_transaction_volume", nullable = false)
    private Integer weeklyTransactionVolume;

    @Column(name = "average_consumption", nullable = false, precision = 12, scale = 2)
    private BigDecimal averageConsumption;

    @Column(name = "consumption_change_rate", nullable = false, precision = 8, scale = 4)
    private BigDecimal consumptionChangeRate;

    @Column(name = "transaction_change_rate", nullable = false, precision = 8, scale = 4)
    private BigDecimal transactionChangeRate;

    @Column(name = "saving_rate", nullable = false, precision = 8, scale = 4)
    private BigDecimal savingRate;

    @Column(name = "wealth_gap", nullable = false, precision = 8, scale = 4)
    private BigDecimal wealthGap;

    @Column(name = "measured_at", nullable = false)
    private LocalDateTime measuredAt;

    protected EconomicMetric() {
    }

    public Integer getId() {
        return id;
    }

    public Integer getClassId() {
        return classId;
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