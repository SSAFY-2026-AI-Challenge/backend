package com.example.seed.controller;
import org.springframework.security.core.Authentication;
import com.example.seed.dto.TransactionPageResponse;
import com.example.seed.service.TransactionService;
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/transactions")
@Tag(
        name = "Transaction",
        description = "학생 거래내역 조회 API"
)
public class TransactionController {

    private final TransactionService transactionService;

    @Operation(
            summary = "거래내역 조회",
            description = "로그인한 학생의 거래내역을 날짜, 거래 타입, 계좌별로 필터링하고 페이징하여 조회합니다."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "거래내역 조회 성공",
                    content = @Content(
                            schema = @Schema(
                                    implementation = TransactionPageResponse.class
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "잘못된 필터 또는 페이징 값",
                    content = @Content(
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "code": "BAD_REQUEST",
                                              "message": "type은 INCOME 또는 EXPENSE만 가능합니다."
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "회원 또는 계좌 정보를 찾을 수 없음",
                    content = @Content(
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "code": "NOT_FOUND",
                                              "message": "계좌 정보를 찾을 수 없습니다."
                                            }
                                            """
                            )
                    )
            )
    })
    @GetMapping
    public ResponseEntity<TransactionPageResponse> getTransactions(

            @Parameter(
                    description = "조회 시작일 (YYYY-MM-DD)",
                    example = "2026-08-01"
            )
            @RequestParam(required = false)
            String from,

            @Parameter(
                    description = "조회 종료일 (YYYY-MM-DD)",
                    example = "2026-08-31"
            )
            @RequestParam(required = false)
            String to,

            @Parameter(
                    description = "거래 타입 (INCOME 또는 EXPENSE)",
                    example = "EXPENSE"
            )
            @RequestParam(required = false)
            String type,

            @Parameter(
                    description = "조회할 계좌 ID",
                    example = "CHK-s3101"
            )
            @RequestParam(required = false)
            String accountId,

            @Parameter(
                    description = "페이지 번호 (1부터 시작)",
                    example = "1"
            )
            @RequestParam(defaultValue = "1")
            Integer page,

            @Parameter(
                    description = "페이지 크기",
                    example = "20"
            )
            @RequestParam(defaultValue = "20")
            Integer size,

            Authentication authentication
    ) {

        Integer memberId =
                Integer.valueOf(authentication.getName());

        TransactionPageResponse response =
                transactionService.getTransactions(
                        memberId,
                        from,
                        to,
                        type,
                        accountId,
                        page,
                        size
                );

        return ResponseEntity.ok(response);
    }
}