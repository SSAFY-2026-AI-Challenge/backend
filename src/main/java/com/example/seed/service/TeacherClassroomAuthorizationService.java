package com.example.seed.service;

import com.example.seed.entity.Member;
import com.example.seed.exception.NotFoundException;
import com.example.seed.repository.MemberRepository;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

@Service
public class TeacherClassroomAuthorizationService {

    private final MemberRepository memberRepository;

    public TeacherClassroomAuthorizationService(
            MemberRepository memberRepository
    ) {
        this.memberRepository = memberRepository;
    }

    public void validateTeacherClassroom(
            Integer memberId,
            Integer classroomId
    ) {

        Member teacher = memberRepository.findById(memberId)
                .orElseThrow(() ->
                        new NotFoundException("회원을 찾을 수 없습니다.")
                );

        // 교사 계정인지 한 번 더 확인
        if (!"TEACHER".equals(teacher.getRole())) {
            throw new AccessDeniedException("교사만 접근할 수 있습니다.");
        }

        // 소속 학급이 없는 경우
        if (teacher.getClassroomId() == null) {
            throw new AccessDeniedException(
                    "소속 학급 정보가 없습니다."
            );
        }

        // 자신의 학급인지 확인
        if (!teacher.getClassroomId().equals(classroomId)) {
            throw new AccessDeniedException(
                    "담당 학급에만 접근할 수 있습니다."
            );
        }
    }
}