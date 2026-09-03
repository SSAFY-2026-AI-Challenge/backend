package com.example.seed.service;

import com.example.seed.dto.ClassroomEconomicAnalysisResponse;
import com.example.seed.entity.EconomicMetric;
import com.example.seed.exception.NotFoundException;
import com.example.seed.repository.ClassroomRepository;
import com.example.seed.repository.EconomicMetricRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class ClassroomEconomicAnalysisService {

    private final ClassroomRepository classroomRepository;
    private final EconomicMetricRepository economicMetricRepository;

    public ClassroomEconomicAnalysisService(
            ClassroomRepository classroomRepository,
            EconomicMetricRepository economicMetricRepository
    ) {
        this.classroomRepository = classroomRepository;
        this.economicMetricRepository = economicMetricRepository;
    }

    public ClassroomEconomicAnalysisResponse createAnalysis(Integer classroomId) {

        // 존재하는 학급인지 확인
        if (!classroomRepository.existsById(classroomId)) {
            throw new NotFoundException("학급을 찾을 수 없습니다.");
        }

        // 해당 학급의 가장 최근 경제지표 조회
        EconomicMetric metric = economicMetricRepository
                .findFirstByClassIdOrderByMeasuredAtDesc(classroomId)
                .orElseThrow(() ->
                        new NotFoundException("학급 경제지표를 찾을 수 없습니다.")
                );

        // MVP 규칙 기반 경제상태 판정
        String economicStatus = determineEconomicStatus(metric);

        // 상태에 따른 분석 요약 생성
        String summary = createSummary(economicStatus);

        // 주요 경제 요인 생성
        List<String> mainFactors = createMainFactors(metric);

        return new ClassroomEconomicAnalysisResponse(
                classroomId,
                economicStatus,
                summary,
                mainFactors,
                LocalDateTime.now()
        );
    }

    private String determineEconomicStatus(EconomicMetric metric) {

        int consumptionComparison =
                metric.getConsumptionChangeRate().compareTo(BigDecimal.ZERO);

        int transactionComparison =
                metric.getTransactionChangeRate().compareTo(BigDecimal.ZERO);

        // 소비 증가 + 거래 증가
        if (consumptionComparison > 0 && transactionComparison > 0) {
            return "EXPANSION";
        }

        // 소비 감소 + 거래 감소
        if (consumptionComparison < 0 && transactionComparison < 0) {
            return "CONTRACTION";
        }

        // 소비 변화 없음 + 거래 변화 없음
        if (consumptionComparison == 0 && transactionComparison == 0) {
            return "STABLE";
        }

        // 소비와 거래 변화 방향이 서로 다른 경우
        return "MIXED";
    }

    private String createSummary(String economicStatus) {

        return switch (economicStatus) {
            case "EXPANSION" ->
                    "소비와 거래 활동이 모두 증가하여 학급 경제 활동이 확장되고 있습니다.";

            case "CONTRACTION" ->
                    "소비와 거래 활동이 모두 감소하여 학급 경제 활동이 위축되고 있습니다.";

            case "STABLE" ->
                    "소비와 거래 활동에 큰 변화가 없어 학급 경제가 안정적인 상태입니다.";

            case "MIXED" ->
                    "소비와 거래 활동의 변화 방향이 서로 달라 혼조 상태를 보이고 있습니다.";

            default ->
                    "현재 학급 경제 상태를 분석할 수 없습니다.";
        };
    }

    private List<String> createMainFactors(EconomicMetric metric) {

        List<String> mainFactors = new ArrayList<>();

        int consumptionComparison =
                metric.getConsumptionChangeRate().compareTo(BigDecimal.ZERO);

        int transactionComparison =
                metric.getTransactionChangeRate().compareTo(BigDecimal.ZERO);

        if (consumptionComparison > 0) {
            mainFactors.add(
                    "소비 증가: " + metric.getConsumptionChangeRate()
            );
        } else if (consumptionComparison < 0) {
            mainFactors.add(
                    "소비 감소: " + metric.getConsumptionChangeRate()
            );
        } else {
            mainFactors.add("소비 변화 없음");
        }

        if (transactionComparison > 0) {
            mainFactors.add(
                    "거래 증가: " + metric.getTransactionChangeRate()
            );
        } else if (transactionComparison < 0) {
            mainFactors.add(
                    "거래 감소: " + metric.getTransactionChangeRate()
            );
        } else {
            mainFactors.add("거래 변화 없음");
        }

        // 상태 판정에는 사용하지 않고 참고 지표로만 제공
        mainFactors.add("저축률: " + metric.getSavingRate());
        mainFactors.add("빈부격차 지표: " + metric.getWealthGap());

        return mainFactors;
    }
}