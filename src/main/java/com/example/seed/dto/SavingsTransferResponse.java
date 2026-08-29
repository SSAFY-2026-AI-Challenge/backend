package com.example.seed.dto;

public record SavingsTransferResponse(
        String fromAccountId,
        String toAccountId,
        Integer amount,
        Integer fromAccountBalance,
        Integer toAccountBalance
) {
}