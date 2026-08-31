package com.example.seed.service;

import com.example.seed.dto.SavingsRecommendationResponse;
import com.example.seed.entity.AiReport;
import com.example.seed.exception.NotFoundException;
import com.example.seed.repository.AiReportRepository;
import com.example.seed.repository.MemberRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class SavingsRecommendationService {

    private static final String REPORT_TYPE = "CREDIT_SCORE";

    private final MemberRepository memberRepository;
    private final AiReportRepository aiReportRepository;

    public SavingsRecommendationService(
            MemberRepository memberRepository,
            AiReportRepository aiReportRepository
    ) {
        this.memberRepository = memberRepository;
        this.aiReportRepository = aiReportRepository;
    }

    public SavingsRecommendationResponse getRecommendations(Integer memberId) {

        // 1. 회원 존재 여부 확인
        memberRepository.findById(memberId)
                .orElseThrow(() ->
                        new NotFoundException("회원을 찾을 수 없습니다.")
                );

        // 2. 해당 학생의 최신 신용평가 리포트 조회
        AiReport aiReport =
                aiReportRepository
                        .findFirstByMemberIdAndReportTypeOrderByGeneratedAtDesc(
                                memberId,
                                REPORT_TYPE
                        )
                        .orElseThrow(() ->
                                new NotFoundException(
                                        "AI 신용평가 리포트를 찾을 수 없습니다."
                                )
                        );

        // 3. ai_report.content / expected_effect 를 추천 목록으로 변환
        return new SavingsRecommendationResponse(toItems(aiReport));
    }

    private List<SavingsRecommendationResponse.Recommendation> toItems(
            AiReport report
    ) {
        String[] contents = splitLines(report.getContent());
        String[] effects = splitLines(report.getExpectedEffect());
        int count = Math.max(contents.length, effects.length);

        List<SavingsRecommendationResponse.Recommendation> items =
                new ArrayList<>();

        for (int i = 0; i < count; i++) {
            items.add(
                    new SavingsRecommendationResponse.Recommendation(
                            report.getId(),
                            null,
                            i < contents.length ? emptyToNull(contents[i]) : null,
                            i < effects.length ? emptyToNull(effects[i]) : null,
                            false
                    )
            );
        }

        return items;
    }

    private String[] splitLines(String value) {
        if (value == null || value.isBlank()) {
            return new String[0];
        }
        return value.split("\\n", -1);
    }

    private String emptyToNull(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return value;
    }
}
