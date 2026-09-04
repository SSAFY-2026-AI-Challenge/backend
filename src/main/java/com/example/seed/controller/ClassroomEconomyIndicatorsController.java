package com.example.seed.controller;

import com.example.seed.dto.ClassroomEconomyIndicatorsResponse;
import com.example.seed.service.ClassroomEconomyIndicatorsService;
import com.example.seed.service.TeacherClassroomAuthorizationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/classrooms/{classroomId}/economy/indicators")
@Tag(
        name = "Teacher - Classroom Economy Indicators",
        description = "교사 학급 경제지표 조회 API"
)
public class ClassroomEconomyIndicatorsController {

    private final ClassroomEconomyIndicatorsService classroomEconomyIndicatorsService;
    private final TeacherClassroomAuthorizationService teacherClassroomAuthorizationService;

    public ClassroomEconomyIndicatorsController(
            ClassroomEconomyIndicatorsService classroomEconomyIndicatorsService,
            TeacherClassroomAuthorizationService teacherClassroomAuthorizationService
    ) {
        this.classroomEconomyIndicatorsService = classroomEconomyIndicatorsService;
        this.teacherClassroomAuthorizationService =
                teacherClassroomAuthorizationService;
    }

    @GetMapping
    @Operation(
            summary = "학급 경제지표 조회",
            description = """
                    특정 학급의 최신 경제지표와 과거 경제지표 추이를 조회합니다.

                    최신 지표에는 총화폐량, 평균 자산, 주간 거래량,
                    평균 소비, 소비 변화율, 거래 변화율,
                    저축률, 빈부격차가 포함됩니다.

                    추이 데이터는 과거부터 최신 순으로 반환됩니다.
                    """
    )
    public ResponseEntity<ClassroomEconomyIndicatorsResponse> getIndicators(
            @Parameter(
                    description = "조회할 학급 ID",
                    example = "1"
            )
            @PathVariable Integer classroomId,

            Authentication authentication
    ) {

        Integer memberId =
                Integer.valueOf(authentication.getName());

        teacherClassroomAuthorizationService
                .validateTeacherClassroom(
                        memberId,
                        classroomId
                );

        ClassroomEconomyIndicatorsResponse response =
                classroomEconomyIndicatorsService.getIndicators(classroomId);

        return ResponseEntity.ok(response);
    }
}