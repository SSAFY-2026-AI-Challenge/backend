package com.example.seed.dto;

import java.time.LocalDateTime;

public record TransactionResponse(
        Integer id,
        String accountId,
        LocalDateTime occurredAt,
        String type,
        String description,
        Integer amount
) {
}