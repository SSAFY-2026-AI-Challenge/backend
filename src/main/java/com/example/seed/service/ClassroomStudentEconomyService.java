package com.example.seed.service;

import com.example.seed.dto.ClassroomStudentEconomyResponse;
import com.example.seed.entity.Account;
import com.example.seed.entity.AiReport;
import com.example.seed.entity.Member;
import com.example.seed.exception.NotFoundException;
import com.example.seed.repository.AccountRepository;
import com.example.seed.repository.AiReportRepository;
import com.example.seed.repository.ClassroomRepository;
import com.example.seed.repository.MemberRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ClassroomStudentEconomyService {

    private final ClassroomRepository classroomRepository;
    private final MemberRepository memberRepository;
    private final AccountRepository accountRepository;
    private final AiReportRepository aiReportRepository;

    public ClassroomStudentEconomyService(
            ClassroomRepository classroomRepository,
            MemberRepository memberRepository,
            AccountRepository accountRepository,
            AiReportRepository aiReportRepository
    ) {
        this.classroomRepository = classroomRepository;
        this.memberRepository = memberRepository;
        this.accountRepository = accountRepository;
        this.aiReportRepository = aiReportRepository;
    }

    public List<ClassroomStudentEconomyResponse> getStudentEconomy(
            Integer classroomId
    ) {

        // 1. 학급 존재 여부 확인
        classroomRepository.findById(classroomId)
                .orElseThrow(() ->
                        new NotFoundException(
                                "학급을 찾을 수 없습니다."
                        )
                );

        // 2. 해당 학급의 학생 목록 조회
        List<Member> students =
                memberRepository.findByClassroomIdAndRole(
                        classroomId,
                        "STUDENT"
                );

        List<ClassroomStudentEconomyResponse> responses =
                new ArrayList<>();

        // 3. 학생별 경제정보 생성
        for (Member student : students) {

            // 학생의 모든 계좌 조회
            List<Account> accounts =
                    accountRepository.findByMemberId(
                            student.getId()
                    );

            // 전체 계좌 잔액 합계
            int balance = accounts.stream()
                    .mapToInt(Account::getBalance)
                    .sum();

            // 가장 최근 신용평가 조회
            String creditGrade = aiReportRepository
                    .findFirstByMemberIdOrderByGeneratedAtDesc(
                            student.getId()
                    )
                    .map(report ->
                            toGradeCode(
                                    report.getCreditScore()
                            )
                    )
                    .orElse(null);

            responses.add(
                    new ClassroomStudentEconomyResponse(
                            student.getId(),
                            student.getName(),
                            student.getJob(),
                            balance,
                            creditGrade
                    )
            );
        }

        return responses;
    }

    private String toGradeCode(int score) {

        if (score >= 900) {
            return "A+";
        }

        if (score >= 800) {
            return "A";
        }

        if (score >= 700) {
            return "B";
        }

        if (score >= 600) {
            return "C";
        }

        return "D";
    }
}