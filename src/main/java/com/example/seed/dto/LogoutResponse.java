package com.example.seed.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "로그아웃 응답")
public class LogoutResponse {

    @Schema(description = "응답 메시지", example = "로그아웃되었습니다.")
    private final String message;

    public LogoutResponse(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}