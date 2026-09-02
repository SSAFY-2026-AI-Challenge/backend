package com.example.seed.service;

import com.example.seed.dto.CreditScoreResponse;
import com.example.seed.entity.AiReport;
import com.example.seed.exception.NotFoundException;
import com.example.seed.repository.AiReportRepository;
import com.example.seed.repository.MemberRepository;
import org.springframework.stereotype.Service;

@Service
public class CreditScoreService {

    private final AiReportRepository aiReportRepository;
    private final MemberRepository memberRepository;

    public CreditScoreService(
            AiReportRepository aiReportRepository,
            MemberRepository memberRepository
    ) {
        this.aiReportRepository = aiReportRepository;
        this.memberRepository = memberRepository;
    }

    public CreditScoreResponse getCreditScore(Integer memberId) {

        memberRepository.findById(memberId)
                .orElseThrow(() ->
                        new NotFoundException(
                                "회원을 찾을 수 없습니다."
                        )
                );

        AiReport report = aiReportRepository
                .findFirstByMemberIdOrderByGeneratedAtDesc(memberId)
                .orElseThrow(() ->
                        new NotFoundException(
                                "신용평가 정보를 찾을 수 없습니다."
                        )
                );

        int score = report.getCreditScore();

        return new CreditScoreResponse(
                score,
                toGradeCode(score)
        );
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
