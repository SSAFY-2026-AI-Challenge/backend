package com.example.seed.controller;

import com.example.seed.dto.CreditReportResponse;
import com.example.seed.service.CreditReportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/credit-reports")
@Tag(name = "AI Credit Report", description = "학생 AI 신용평가 API")
public class CreditReportController {

    private final CreditReportService creditReportService;

    @Operation(
            summary = "AI 신용평가 조회",
            description = "특정 월의 학생 AI 신용평가 결과를 조회합니다."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "AI 신용평가 조회 성공",
                    content = @Content(
                            schema = @Schema(
                                    implementation = CreditReportResponse.class
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "잘못된 연월 형식",
                    content = @Content(
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "code": "BAD_REQUEST",
                                              "message": "yearMonth는 yyyy-MM 형식이어야 합니다."
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "회원 또는 해당 월 AI 신용 리포트를 찾을 수 없음",
                    content = @Content(
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "code": "NOT_FOUND",
                                              "message": "해당 월의 AI 신용 리포트가 없습니다."
                                            }
                                            """
                            )
                    )
            )
    })
    @GetMapping("/{yearMonth}")
    public ResponseEntity<CreditReportResponse> getCreditReport(
            @Parameter(
                    description = "조회할 연월",
                    example = "2026-08",
                    required = true
            )
            @PathVariable String yearMonth
    ) {

        // TODO: JWT 인증 구현 후 로그인 사용자의 memberId로 변경
        Integer memberId = 2;

        CreditReportResponse response =
                creditReportService.getCreditReport(memberId, yearMonth);

        return ResponseEntity.ok(response);
    }
}