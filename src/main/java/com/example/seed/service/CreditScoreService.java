package com.example.seed.service;

import com.example.seed.dto.CreditScoreResponse;
import com.example.seed.entity.AiReport;
import com.example.seed.exception.NotFoundException;
import com.example.seed.repository.AiReportRepository;
import com.example.seed.repository.MemberRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

@Service
public class CreditScoreService {

    private static final String REPORT_TYPE = "CREDIT_SCORE";

    private final AiReportRepository aiReportRepository;
    private final MemberRepository memberRepository;
    private final ObjectMapper objectMapper;

    public CreditScoreService(
            AiReportRepository aiReportRepository,
            MemberRepository memberRepository,
            ObjectMapper objectMapper
    ) {
        this.aiReportRepository = aiReportRepository;
        this.memberRepository = memberRepository;
        this.objectMapper = objectMapper;
    }

    public CreditScoreResponse getCreditScore(Integer memberId) {

        // 1. 회원 존재 여부 확인
        memberRepository.findById(memberId)
                .orElseThrow(() ->
                        new NotFoundException(
                                "회원을 찾을 수 없습니다."
                        )
                );

        // 2. 가장 최근 신용 리포트 조회
        AiReport report = aiReportRepository
                .findFirstByMemberIdAndReportTypeOrderByGeneratedAtDesc(
                        memberId,
                        REPORT_TYPE
                )
                .orElseThrow(() ->
                        new NotFoundException(
                                "신용평가 정보를 찾을 수 없습니다."
                        )
                );

        // 3. detail_json 파싱
        JsonNode detail = parseDetailJson(
                report.getDetailJson()
        );

        int score = detail.path("credit_score").asInt();
        String grade = detail.path("grade_code").asText();

        // 4. 응답 생성
        return new CreditScoreResponse(
                score,
                grade
        );
    }

    private JsonNode parseDetailJson(String detailJson) {

        try {
            return objectMapper.readTree(detailJson);

        } catch (Exception e) {
            throw new IllegalStateException(
                    "AI 신용평가 상세 데이터를 읽을 수 없습니다.",
                    e
            );
        }
    }
}