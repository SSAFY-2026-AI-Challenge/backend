package com.example.seed.controller;

import com.example.seed.dto.SavingsRecommendationResponse;
import com.example.seed.service.SavingsRecommendationService;
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
@RequestMapping("/api/v1/savings")
@Tag(name = "Savings", description = "학생 저축 API")
public class SavingsRecommendationController {

    private final SavingsRecommendationService savingsRecommendationService;

    @Operation(
            summary = "AI 맞춤 저축 추천 조회",
            description = "로그인한 학생의 최신 AI 신용평가 리포트에 연결된 추천 정보를 조회합니다."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "AI 맞춤 저축 추천 조회 성공",
                    content = @Content(
                            schema = @Schema(
                                    implementation = SavingsRecommendationResponse.class
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "회원 또는 AI 신용평가 리포트를 찾을 수 없음",
                    content = @Content(
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "code": "NOT_FOUND",
                                              "message": "AI 신용평가 리포트를 찾을 수 없습니다."
                                            }
                                            """
                            )
                    )
            )
    })
    @GetMapping("/recommendations")
    public ResponseEntity<SavingsRecommendationResponse> getRecommendations() {

        // TODO: JWT 인증 구현 후 SecurityContext에서 memberId 추출
        Integer memberId = 3;

        SavingsRecommendationResponse response =
                savingsRecommendationService.getRecommendations(memberId);

        return ResponseEntity.ok(response);
    }
}