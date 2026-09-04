package com.example.seed.controller;

import com.example.seed.dto.MeResponse;
import com.example.seed.service.MeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/me")
@Tag(
        name = "Auth",
        description = "인증 및 현재 사용자 API"
)
public class MeController {

    private final MeService meService;

    public MeController(
            MeService meService
    ) {
        this.meService = meService;
    }

    @GetMapping
    @Operation(
            summary = "현재 로그인 사용자 조회",
            description = "JWT 인증 정보를 기반으로 현재 로그인한 사용자의 정보를 조회합니다."
    )
    public ResponseEntity<MeResponse> getMe(
            Authentication authentication
    ) {

        Integer memberId =
                Integer.valueOf(authentication.getName());

        MeResponse response =
                meService.getMe(memberId);

        return ResponseEntity.ok(response);
    }
}