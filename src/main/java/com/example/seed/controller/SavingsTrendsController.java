package com.example.seed.controller;

import com.example.seed.dto.SavingsTrendsResponse;
import com.example.seed.service.SavingsTrendsService;
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
public class SavingsTrendsController {

    private final SavingsTrendsService savingsTrendsService;

    @Operation(
            summary = "최근 6개월 저축 추이 조회",
            description = "로그인한 학생의 현재 월을 포함한 최근 6개월 저축액을 월별로 조회합니다."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "저축 추이 조회 성공",
                    content = @Content(
                            schema = @Schema(
                                    implementation = SavingsTrendsResponse.class
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "회원 또는 저축 계좌 정보를 찾을 수 없음",
                    content = @Content(
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "code": "NOT_FOUND",
                                              "message": "저축 계좌를 찾을 수 없습니다."
                                            }
                                            """
                            )
                    )
            )
    })
    @GetMapping("/trends")
    public ResponseEntity<SavingsTrendsResponse> getSavingsTrends() {

        // TODO: JWT 인증 구현 후 SecurityContext에서 memberId 추출
        Integer memberId = 3;

        SavingsTrendsResponse response =
                savingsTrendsService.getSavingsTrends(memberId);

        return ResponseEntity.ok(response);
    }
}