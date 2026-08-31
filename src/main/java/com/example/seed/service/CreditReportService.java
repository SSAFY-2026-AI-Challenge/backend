package com.example.seed.service;

import com.example.seed.dto.CreditReportResponse;
import com.example.seed.entity.AiReport;
import com.example.seed.exception.BadRequestException;
import com.example.seed.exception.NotFoundException;
import com.example.seed.repository.AiReportRepository;
import com.example.seed.repository.MemberRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

@Service
public class CreditReportService {

    private static final String REPORT_TYPE = "CREDIT_REPORT";

    private final AiReportRepository aiReportRepository;
    private final MemberRepository memberRepository;
    private final ObjectMapper objectMapper;

    public CreditReportService(
            AiReportRepository aiReportRepository,
            MemberRepository memberRepository,
            ObjectMapper objectMapper
    ) {
        this.aiReportRepository = aiReportRepository;
        this.memberRepository = memberRepository;
        this.objectMapper = objectMapper;
    }

    public CreditReportResponse getCreditReport(
            Integer memberId,
            String yearMonth
    ) {

        // 1. "2026-08" 같은 문자열을 YearMonth로 변환
        YearMonth targetMonth;

        try {
            targetMonth = YearMonth.parse(yearMonth);
        } catch (DateTimeParseException e) {
            throw new BadRequestException(
                    "yearMonth는 YYYY-MM 형식이어야 합니다."
            );
        }

        // 2. 조회할 월의 시작일과 다음 달 시작일 계산
        LocalDate startDate = targetMonth.atDay(1);

        LocalDate endDate =
                targetMonth.plusMonths(1)
                        .atDay(1);

        // 3. 회원 존재 여부 확인
        memberRepository.findById(memberId)
                .orElseThrow(() ->
                        new NotFoundException(
                                "회원을 찾을 수 없습니다."
                        )
                );

        // 4. 해당 회원의 해당 월 AI 리포트 조회
        AiReport report = aiReportRepository
                .findFirstByMemberIdAndReportTypeAndGeneratedAtGreaterThanEqualAndGeneratedAtLessThanOrderByGeneratedAtDesc(
                        memberId,
                        REPORT_TYPE,
                        startDate,
                        endDate
                )
                .orElseThrow(() ->
                        new NotFoundException(
                                "해당 월의 AI 신용 리포트를 찾을 수 없습니다."
                        )
                );

        // 5. detail_json 파싱
        JsonNode detail = parseDetailJson(
                report.getDetailJson()
        );

        int score =
                detail.path("score").asInt();

        int maxScore =
                detail.path("maxScore").asInt();

        String grade =
                detail.path("grade").asText();

        List<CreditReportResponse.Factor> factors =
                parseFactors(
                        detail.path("factors")
                );

        List<CreditReportResponse.BehaviorMetric> behaviorMetrics =
                parseBehaviorMetrics(
                        detail.path("behaviorMetrics")
                );

        // 6. API 응답 생성
        return new CreditReportResponse(
                targetMonth.toString(),
                score,
                maxScore,
                grade,
                factors,
                behaviorMetrics,
                report.getSummary(),
                report.getContent(),
                report.getExpectedEffect()
        );
    }

    private JsonNode parseDetailJson(String detailJson) {

        try {
            return objectMapper.readTree(detailJson);
        } catch (Exception e) {
            throw new IllegalStateException(
                    "AI 리포트 상세 데이터를 읽을 수 없습니다.",
                    e
            );
        }
    }

    private List<CreditReportResponse.Factor> parseFactors(
            JsonNode nodes
    ) {

        List<CreditReportResponse.Factor> factors =
                new ArrayList<>();

        for (JsonNode node : nodes) {

            factors.add(
                    new CreditReportResponse.Factor(
                            node.path("type").asText(),
                            node.path("label").asText(),
                            node.path("impact").asText()
                    )
            );
        }

        return factors;
    }

    private List<CreditReportResponse.BehaviorMetric>
    parseBehaviorMetrics(JsonNode nodes) {

        List<CreditReportResponse.BehaviorMetric> metrics =
                new ArrayList<>();

        for (JsonNode node : nodes) {

            metrics.add(
                    new CreditReportResponse.BehaviorMetric(
                            node.path("key").asText(),
                            node.path("label").asText(),
                            node.path("value").asInt()
                    )
            );
        }

        return metrics;
    }
}
