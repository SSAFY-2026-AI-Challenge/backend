package com.example.seed.controller;

import com.example.seed.dto.ClassroomStudentEconomyResponse;
import com.example.seed.service.ClassroomStudentEconomyService;
import com.example.seed.service.TeacherClassroomAuthorizationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/classrooms/{classroomId}/students/economy")
@Tag(
        name = "Teacher - Classroom Student Economy",
        description = "교사 학급 학생 경제활동 조회 API"
)
public class ClassroomStudentEconomyController {

    private final ClassroomStudentEconomyService classroomStudentEconomyService;
    private final TeacherClassroomAuthorizationService teacherClassroomAuthorizationService;

    public ClassroomStudentEconomyController(
            ClassroomStudentEconomyService classroomStudentEconomyService,
            TeacherClassroomAuthorizationService teacherClassroomAuthorizationService
    ) {
        this.classroomStudentEconomyService = classroomStudentEconomyService;
        this.teacherClassroomAuthorizationService =
                teacherClassroomAuthorizationService;
    }

    @GetMapping
    @Operation(
            summary = "학급 학생 경제활동 조회",
            description = "특정 학급에 소속된 학생들의 이름, 직업, 전체 계좌 잔액 합계, 신용등급을 조회합니다."
    )
    public ResponseEntity<List<ClassroomStudentEconomyResponse>> getStudentEconomy(
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

        List<ClassroomStudentEconomyResponse> response =
                classroomStudentEconomyService.getStudentEconomy(classroomId);

        return ResponseEntity.ok(response);
    }
}