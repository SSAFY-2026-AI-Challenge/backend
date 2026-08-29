package com.example.seed.dto;

public record SavingsSummaryResponse (
        Integer totalSavings,
        Integer monthlySavings,
        Double averageSavings,
        Double savingsRate
){

}