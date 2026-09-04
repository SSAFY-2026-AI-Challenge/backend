package com.example.seed.controller;

import com.example.seed.dto.ClassroomEconomyDashboardResponse;
import com.example.seed.service.ClassroomEconomyDashboardService;
import com.example.seed.service.TeacherClassroomAuthorizationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/classrooms/{classroomId}/economy/dashboard")
@Tag(
        name = "Teacher - Classroom Economy Dashboard",
        description = "교사 학급 경제 대시보드 API"
)
public class ClassroomEconomyDashboardController {

    private final ClassroomEconomyDashboardService classroomEconomyDashboardService;
    private final TeacherClassroomAuthorizationService teacherClassroomAuthorizationService;

    public ClassroomEconomyDashboardController(
            ClassroomEconomyDashboardService classroomEconomyDashboardService,
            TeacherClassroomAuthorizationService teacherClassroomAuthorizationService
    ) {
        this.classroomEconomyDashboardService = classroomEconomyDashboardService;
        this.teacherClassroomAuthorizationService =
                teacherClassroomAuthorizationService;
    }

    @GetMapping
    @Operation(
            summary = "학급 경제 대시보드 조회",
            description = """
                    특정 학급의 핵심 경제 KPI를 조회합니다.

                    현재 MVP에서는 총 통화량, 학생 평균 보유액,
                    이번 달 총 소비액, 총 저축액, 이번 달 거래량을 제공합니다.
                    """
    )
    public ResponseEntity<ClassroomEconomyDashboardResponse> getDashboard(
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

        ClassroomEconomyDashboardResponse response =
                classroomEconomyDashboardService.getDashboard(classroomId);

        return ResponseEntity.ok(response);
    }
}