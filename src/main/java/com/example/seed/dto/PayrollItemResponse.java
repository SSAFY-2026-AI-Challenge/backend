package com.example.seed.dto;

public class PayrollItemResponse {

    private String name;
    private Integer amount;

    public PayrollItemResponse(String name, Integer amount){
        this.name = name;
        this.amount = amount;
    }

    public String getName() {
        return name;
    }

    public Integer getAmount() {
        return amount;
    }
}
