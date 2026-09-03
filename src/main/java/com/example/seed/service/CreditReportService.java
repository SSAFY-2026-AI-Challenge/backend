package com.example.seed.service;

import com.example.seed.dto.CreditReportResponse;
import com.example.seed.entity.AiReport;
import com.example.seed.exception.BadRequestException;
import com.example.seed.exception.NotFoundException;
import com.example.seed.repository.AiReportRepository;
import com.example.seed.repository.MemberRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeParseException;

@Service
public class CreditReportService {

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

        YearMonth targetMonth;

        try {
            targetMonth = YearMonth.parse(yearMonth);
        } catch (DateTimeParseException e) {
            throw new BadRequestException(
                    "yearMonth는 YYYY-MM 형식이어야 합니다."
            );
        }

        LocalDateTime startDate =
                targetMonth.atDay(1).atStartOfDay();

        LocalDateTime endDate =
                targetMonth.plusMonths(1)
                        .atDay(1)
                        .atStartOfDay();

        memberRepository.findById(memberId)
                .orElseThrow(() ->
                        new NotFoundException(
                                "회원을 찾을 수 없습니다."
                        )
                );

        AiReport report = aiReportRepository
                .findFirstByMemberIdAndGeneratedAtGreaterThanEqualAndGeneratedAtLessThanOrderByGeneratedAtDesc(
                        memberId,
                        startDate,
                        endDate
                )
                .orElseThrow(() ->
                        new NotFoundException(
                                "해당 월의 AI 신용 리포트를 찾을 수 없습니다."
                        )
                );

        return toResponse(report);
    }

    private CreditReportResponse toResponse(AiReport report) {

        return new CreditReportResponse(
                report.getCreditScore(),
                report.getSummary(),
                report.getContent(),
                report.getExpectedEffect(),
                parseFeatures(report.getFeatures()),
                report.getGeneratedAt()
        );
    }

    private Object parseFeatures(String features) {

        try {
            return objectMapper.readValue(features, Object.class);
        } catch (Exception e) {
            throw new IllegalStateException(
                    "AI 신용평가 상세 데이터를 읽을 수 없습니다.",
                    e
            );
        }
    }
}
