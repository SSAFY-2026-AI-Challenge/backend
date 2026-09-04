package com.example.seed.controller;

import com.example.seed.dto.MonthlyResultResponse;
import com.example.seed.service.MonthlyResultService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;
@RestController
@RequestMapping("/api/v1/monthly-results")
@Tag(
        name = "Student - Monthly Result",
        description = "학생 월말 경제활동 결과 조회 API"
)
public class MonthlyResultController {

    private final MonthlyResultService monthlyResultService;

    public MonthlyResultController(
            MonthlyResultService monthlyResultService
    ) {
        this.monthlyResultService = monthlyResultService;
    }

    @GetMapping("/{yearMonth}")
    @Operation(
            summary = "학생 월말 결과 조회",
            description = """
                특정 월의 학생 경제활동 결과를 조회합니다.

                급여, 세금/공제, 소비, 저축, 월말 잔액,
                총자산, 저축률 및 전월 대비 자산 변화를 반환합니다.

                현재 MVP에서는 별도의 월말 결과 테이블에 저장하지 않고
                기존 account, transaction, payroll 데이터를 기반으로
                요청 시 계산하여 반환합니다.
                """
    )
    public ResponseEntity<MonthlyResultResponse> getMonthlyResult(

            @Parameter(
                    description = "조회할 연월 (YYYY-MM)",
                    example = "2026-09"
            )
            @PathVariable String yearMonth,

            Authentication authentication
    ) {

        Integer memberId =
                Integer.valueOf(authentication.getName());

        MonthlyResultResponse response =
                monthlyResultService.getMonthlyResult(
                        memberId,
                        yearMonth
                );

        return ResponseEntity.ok(response);
    }
}