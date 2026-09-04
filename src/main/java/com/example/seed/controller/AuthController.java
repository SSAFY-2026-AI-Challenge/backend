package com.example.seed.controller;

import com.example.seed.dto.LoginRequest;
import com.example.seed.dto.LoginResponse;
import com.example.seed.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;

@RestController
@RequestMapping("/api/v1/auth")
@Tag(
        name = "Auth",
        description = "인증 API"
)
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    @SecurityRequirements
    @Operation(
            summary = "로그인",
            description = """
                    아이디와 비밀번호로 로그인합니다.
                    로그인에 성공하면 JWT Access Token과 사용자 정보를 반환합니다.
                    """
    )
    public ResponseEntity<LoginResponse> login(
            @Valid @RequestBody LoginRequest request
    ) {

        LoginResponse response =
                authService.login(request);

        return ResponseEntity.ok(response);
    }
}