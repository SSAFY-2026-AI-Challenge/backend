package com.example.seed.controller;
import org.springframework.security.core.Authentication;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import com.example.seed.dto.PayrollResponse;
import com.example.seed.service.PayrollService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(
        name = "Payroll",
        description = "급여 및 세금 API"
)
@RestController
@RequestMapping("/api/v1/payrolls")
public class PayrollController {

    private final PayrollService payrollService;

    public PayrollController(PayrollService payrollService) {
        this.payrollService = payrollService;
    }

    @Operation(
            summary = "급여명세서 조회",
            description = "특정 월의 급여 및 세금 정보를 조회합니다."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "급여명세서 조회 성공",
                    content = @Content(
                            schema = @Schema(implementation = PayrollResponse.class)
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
                                              "message": "yearMonth는 YYYY-MM 형식이어야 합니다."
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "회원 또는 해당 월 급여 내역을 찾을 수 없음",
                    content = @Content(
                            examples = @ExampleObject(
                                    value= """
                                            {
                                              "code": "NOT_FOUND",
                                              "message": "해당 월의 급여 내역을 찾을 수 없습니다."
                                            }
                                            """
                            )
                    )
            )
    })
    @GetMapping("/{yearMonth}")
    public PayrollResponse getPayroll(
            @Parameter(
                    description = "조회할 연월",
                    example = "2026-08",
                    required = true
            )
            @PathVariable String yearMonth,
            Authentication authentication
    ) {

        Integer memberId =
                Integer.valueOf(authentication.getName());

        return payrollService.getPayroll(
                memberId,
                yearMonth
        );
    }
}
