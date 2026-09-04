package com.example.seed.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "저축 계좌 이체 요청")
public record SavingsTransferRequest(
        @Schema(description = "출금 계좌 ID (CHECKING 계좌, 미입력 시 본인의 CHECKING 계좌 자동 지정)", example = "ACC-CHECKING-001")
        String fromAccountId,

        @Schema(description = "입금 계좌 ID (SAVINGS 계좌, 미입력 시 본인의 SAVINGS 계좌 자동 지정)", example = "ACC-SAVINGS-001")
        String toAccountId,

        @Schema(description = "이체 금액", example = "10000", requiredMode = Schema.RequiredMode.REQUIRED)
        Integer amount,

        @Schema(description = "저축 목표 ID (선택)", example = "GOAL-001")
        String goalId
) {
}