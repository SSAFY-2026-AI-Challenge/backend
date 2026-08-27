package com.example.seed.controller;

import com.example.seed.dto.StudentDashboardResponse;
import com.example.seed.service.StudentDashboardService;
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
@RequestMapping("/api/v1/student")
@Tag(
        name = "Student Dashboard",
        description = "학생 대시보드 API"
)
public class StudentDashboardController {

    private final StudentDashboardService studentDashboardService;

    @Operation(
            summary = "학생 대시보드 조회",
            description = "로그인한 학생의 직업, 이번 달 수입/지출, 자산, 저축, 신용정보, 최근 거래 내역을 조회합니다."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "학생 대시보드 조회 성공",
                    content = @Content(
                            schema = @Schema(
                                    implementation = StudentDashboardResponse.class
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
    @GetMapping("/dashboard")
    public ResponseEntity<StudentDashboardResponse> getDashboard() {

        // TODO: JWT 인증 구현 후 로그인한 사용자의 memberId로 변경
        Integer memberId = 2;

        StudentDashboardResponse response =
                studentDashboardService.getDashboard(memberId);

        return ResponseEntity.ok(response);
    }
}