package com.example.seed.controller;

import com.example.seed.dto.SavingsSummaryResponse;
import com.example.seed.service.SavingsSummaryService;
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
import org.springframework.security.core.Authentication;
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/savings")
@Tag(
        name = "Savings",
        description = "학생 저축 API"
)
public class SavingsSummaryController {

    private final SavingsSummaryService savingsSummaryService;

    @Operation(
            summary = "저축 요약 조회",
            description = "로그인한 학생의 총 저축액, 이번 달 저축액, 평균 저축액, 저축률을 조회합니다."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "저축 요약 조회 성공",
                    content = @Content(
                            schema = @Schema(
                                    implementation = SavingsSummaryResponse.class
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
    @GetMapping("/summary")
    public ResponseEntity<SavingsSummaryResponse> getSavingsSummary(
            Authentication authentication
    ) {

        Integer memberId =
                Integer.valueOf(authentication.getName());

        SavingsSummaryResponse response =
                savingsSummaryService.getSavingsSummary(memberId);

        return ResponseEntity.ok(response);
    }
}