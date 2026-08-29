package com.example.seed.dto;

public record SavingsTransferRequest(
        String fromAccountId,
        String toAccountId,
        Integer amount,
        String goalId
) {
}