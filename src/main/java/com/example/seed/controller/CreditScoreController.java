package com.example.seed.controller;

import com.example.seed.dto.CreditScoreResponse;
import com.example.seed.service.CreditScoreService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/credit-score")
@Tag(
        name = "Credit Score",
        description = "학생 현재 신용등급 API"
)
public class CreditScoreController {

    private final CreditScoreService creditScoreService;

    @Operation(
            summary = "현재 신용등급 조회",
            description = "로그인한 학생의 가장 최근 신용점수와 신용등급을 조회합니다."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "현재 신용등급 조회 성공",
                    content = @Content(
                            schema = @Schema(
                                    implementation = CreditScoreResponse.class
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "회원 또는 신용평가 정보를 찾을 수 없음",
                    content = @Content(
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "code": "NOT_FOUND",
                                              "message": "신용평가 정보를 찾을 수 없습니다."
                                            }
                                            """
                            )
                    )
            )
    })
    @GetMapping
    public ResponseEntity<CreditScoreResponse> getCreditScore() {

        // TODO: JWT 인증 구현 후 로그인한 사용자의 memberId로 변경
        Integer memberId = 2;

        CreditScoreResponse response =
                creditScoreService.getCreditScore(memberId);

        return ResponseEntity.ok(response);
    }
}