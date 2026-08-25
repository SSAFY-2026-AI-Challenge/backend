package com.example.seed.dto;

import java.util.List;

public class PayrollResponse {

    private String yearMonth;
    private String jobName;
    private List<PayrollItemResponse> earnings;
    private List<PayrollItemResponse> deductions;
    private Integer grossPay;
    private Integer totalDeductions;
    private Integer netPay;
    private String settlementStatus;

    public PayrollResponse(
            String yearMonth,
            String jobName,
            List<PayrollItemResponse> earnings,
            List<PayrollItemResponse> deductions,
            Integer grossPay,
            Integer totalDeductions,
            Integer netPay,
            String settlementStatus
    ) {
        this.yearMonth = yearMonth;
        this.jobName = jobName;
        this.earnings = earnings;
        this.deductions = deductions;
        this.grossPay = grossPay;
        this.totalDeductions = totalDeductions;
        this.netPay = netPay;
        this.settlementStatus = settlementStatus;
    }

    public String getYearMonth() {
        return yearMonth;
    }

    public String getJobName() {
        return jobName;
    }

    public List<PayrollItemResponse> getEarnings() {
        return earnings;
    }

    public List<PayrollItemResponse> getDeductions() {
        return deductions;
    }

    public Integer getGrossPay() {
        return grossPay;
    }

    public Integer getTotalDeductions() {
        return totalDeductions;
    }

    public Integer getNetPay() {
        return netPay;
    }

    public String getSettlementStatus() {
        return settlementStatus;
    }

}
