package com.example.seed.controller;

import com.example.seed.dto.LoginRequest;
import com.example.seed.dto.LoginResponse;
import com.example.seed.dto.LogoutResponse;
import com.example.seed.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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

    @PostMapping("/logout")
    @Operation(
            summary = "로그아웃",
            description = """
                    로그아웃을 수행합니다.
                    서버는 별도의 토큰 무효화(Blacklist/세션) 처리를 하지 않으며,
                    클라이언트에서 보관 중인 JWT Access Token을 삭제해야 합니다.
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "로그아웃 성공",
                    content = @Content(
                            schema = @Schema(implementation = LogoutResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "인증 실패 (JWT 누락 또는 유효하지 않음)",
                    content = @Content(
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "code": "UNAUTHORIZED",
                                              "message": "인증이 필요합니다."
                                            }
                                            """
                            )
                    )
            )
    })
    public ResponseEntity<LogoutResponse> logout() {
        return ResponseEntity.ok(new LogoutResponse("로그아웃되었습니다."));
    }
}