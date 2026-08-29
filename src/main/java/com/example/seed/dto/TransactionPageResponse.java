package com.example.seed.dto;

import java.util.List;

public record TransactionPageResponse(
        List<TransactionResponse> items,
        Integer page,
        Integer size,
        Long totalCount
) {
}