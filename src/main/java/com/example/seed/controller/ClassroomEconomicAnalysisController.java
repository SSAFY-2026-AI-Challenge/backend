package com.example.seed.controller;

import com.example.seed.dto.ClassroomEconomicAnalysisResponse;
import com.example.seed.service.ClassroomEconomicAnalysisService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/classrooms/{classroomId}/economic-analyses")
@Tag(
        name = "Teacher - Classroom Economic Analysis",
        description = "교사 학급 경제 분석 API"
)
public class ClassroomEconomicAnalysisController {

    private final ClassroomEconomicAnalysisService classroomEconomicAnalysisService;

    public ClassroomEconomicAnalysisController(
            ClassroomEconomicAnalysisService classroomEconomicAnalysisService
    ) {
        this.classroomEconomicAnalysisService = classroomEconomicAnalysisService;
    }

    @PostMapping
    @Operation(
            summary = "학급 경제 분석 생성",
            description = """
                    특정 학급의 최신 economic_metric 데이터를 기반으로
                    학급 경제 분석을 생성합니다.

                    현재 MVP에서는 AI 서버 연동 전까지
                    소비 변화율과 거래 변화율을 기반으로
                    규칙 기반 경제상태 분석을 수행합니다.

                    분석 결과는 DB에 저장하지 않고 요청 시 생성하여 반환합니다.
                    """
    )
    public ResponseEntity<ClassroomEconomicAnalysisResponse> createAnalysis(
            @Parameter(
                    description = "분석할 학급 ID",
                    example = "1"
            )
            @PathVariable Integer classroomId
    ) {

        ClassroomEconomicAnalysisResponse response =
                classroomEconomicAnalysisService.createAnalysis(classroomId);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/latest")
    @Operation(
            summary = "최신 학급 경제 분석 조회",
            description = """
                    특정 학급의 최신 economic_metric 데이터를 기반으로
                    현재 학급 경제 분석 결과를 조회합니다.

                    현재 MVP에서는 분석 결과를 별도 DB에 저장하지 않으므로
                    최신 경제지표를 기준으로 규칙 기반 분석을 다시 생성하여 반환합니다.
                    """
    )
    public ResponseEntity<ClassroomEconomicAnalysisResponse> getLatestAnalysis(
            @Parameter(
                    description = "조회할 학급 ID",
                    example = "1"
            )
            @PathVariable Integer classroomId
    ) {

        ClassroomEconomicAnalysisResponse response =
                classroomEconomicAnalysisService.createAnalysis(classroomId);

        return ResponseEntity.ok(response);
    }
}