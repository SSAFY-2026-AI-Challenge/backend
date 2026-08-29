package com.example.seed.controller;

import com.example.seed.dto.SavingsTransferRequest;
import com.example.seed.dto.SavingsTransferResponse;
import com.example.seed.service.SavingsTransferService;
import io.swagger.v3.oas.annotations.Operation;
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
@RequestMapping("/api/v1/savings")
@Tag(name = "Savings", description = "학생 저축 API")
public class SavingsTransferController {

    private final SavingsTransferService savingsTransferService;

    @Operation(
            summary = "저축 계좌 이체",
            description = "로그인한 학생의 CHECKING 계좌에서 SAVINGS 계좌로 금액을 이체합니다."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "저축 이체 성공",
                    content = @Content(
                            schema = @Schema(
                                    implementation = SavingsTransferResponse.class
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "잘못된 요청 또는 잔액 부족",
                    content = @Content(
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "code": "BAD_REQUEST",
                                              "message": "출금 계좌의 잔액이 부족합니다."
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "회원 또는 계좌를 찾을 수 없음",
                    content = @Content(
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "code": "NOT_FOUND",
                                              "message": "출금 계좌를 찾을 수 없습니다."
                                            }
                                            """
                            )
                    )
            )
    })
    @PostMapping("/transfers")
    public ResponseEntity<SavingsTransferResponse> transfer(
            @RequestBody SavingsTransferRequest request
    ) {

        // TODO: JWT 인증 구현 후 SecurityContext에서 memberId 추출
        Integer memberId = 3;

        SavingsTransferResponse response =
                savingsTransferService.transfer(memberId, request);

        return ResponseEntity.ok(response);
    }
}