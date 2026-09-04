package com.example.seed.controller;
import org.springframework.security.core.Authentication;
import com.example.seed.dto.AccountResponse;
import com.example.seed.service.AccountService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/accounts")
@Tag(
        name = "Account",
        description = "학생 계좌 API"
)
public class AccountController {

    private final AccountService accountService;

    @Operation(
            summary = "계좌 목록 조회",
            description = "로그인한 학생의 계좌 목록과 잔액을 조회합니다."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "계좌 목록 조회 성공",
                    content = @Content(
                            array = @ArraySchema(
                                    schema = @Schema(
                                            implementation = AccountResponse.class
                                    )
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "계좌 정보를 찾을 수 없음",
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
    public ResponseEntity<List<AccountResponse>> getAccounts(
            Authentication authentication
    ) {

        Integer memberId =
                Integer.valueOf(authentication.getName());

        List<AccountResponse> response =
                accountService.getAccounts(memberId);

        return ResponseEntity.ok(response);
    }
}