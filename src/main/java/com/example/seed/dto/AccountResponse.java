package com.example.seed.dto;

public record AccountResponse (
    String accountId,
    String accountType,
    Integer balance
){
}
