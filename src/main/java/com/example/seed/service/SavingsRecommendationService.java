package com.example.seed.service;

import com.example.seed.dto.SavingsRecommendationResponse;
import com.example.seed.entity.AiRecommendation;
import com.example.seed.entity.AiReport;
import com.example.seed.exception.NotFoundException;
import com.example.seed.repository.AiRecommendationRepository;
import com.example.seed.repository.AiReportRepository;
import com.example.seed.repository.MemberRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SavingsRecommendationService {

    private static final String REPORT_TYPE = "CREDIT_SCORE";

    private final MemberRepository memberRepository;
    private final AiReportRepository aiReportRepository;
    private final AiRecommendationRepository aiRecommendationRepository;

    public SavingsRecommendationService(
            MemberRepository memberRepository,
            AiReportRepository aiReportRepository,
            AiRecommendationRepository aiRecommendationRepository
    ) {
        this.memberRepository = memberRepository;
        this.aiReportRepository = aiReportRepository;
        this.aiRecommendationRepository = aiRecommendationRepository;
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

        // 3. 해당 리포트에 연결된 추천 목록 조회
        List<AiRecommendation> recommendations =
                aiRecommendationRepository
                        .findByReportIdOrderByIdAsc(
                                aiReport.getId()
                        );

        // 4. DTO 변환
        List<SavingsRecommendationResponse.Recommendation> items =
                recommendations.stream()
                        .map(recommendation ->
                                new SavingsRecommendationResponse.Recommendation(
                                        recommendation.getId(),
                                        recommendation.getRecommendationType(),
                                        recommendation.getContent(),
                                        recommendation.getExpectedEffect(),
                                        recommendation.getIsApplied()
                                )
                        )
                        .toList();

        // 5. 응답 반환
        return new SavingsRecommendationResponse(items);
    }
}