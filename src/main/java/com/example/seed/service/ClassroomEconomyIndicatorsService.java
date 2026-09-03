package com.example.seed.service;

import com.example.seed.dto.ClassroomEconomyIndicatorsResponse;
import com.example.seed.dto.EconomicMetricResponse;
import com.example.seed.dto.EconomicMetricTrendResponse;
import com.example.seed.entity.EconomicMetric;
import com.example.seed.exception.NotFoundException;
import com.example.seed.repository.ClassroomRepository;
import com.example.seed.repository.EconomicMetricRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ClassroomEconomyIndicatorsService {

    private final ClassroomRepository classroomRepository;
    private final EconomicMetricRepository economicMetricRepository;

    public ClassroomEconomyIndicatorsService(
            ClassroomRepository classroomRepository,
            EconomicMetricRepository economicMetricRepository
    ) {
        this.classroomRepository = classroomRepository;
        this.economicMetricRepository = economicMetricRepository;
    }

    public ClassroomEconomyIndicatorsResponse getIndicators(Integer classroomId) {

        // 존재하는 학급인지 확인
        if (!classroomRepository.existsById(classroomId)) {
            throw new NotFoundException("학급을 찾을 수 없습니다.");
        }

        // 가장 최근 경제지표 1건 조회
        EconomicMetric latestMetric = economicMetricRepository
                .findFirstByClassIdOrderByMeasuredAtDesc(classroomId)
                .orElseThrow(() ->
                        new NotFoundException("학급 경제지표를 찾을 수 없습니다.")
                );

        // 최신 경제지표 DTO
        EconomicMetricResponse latest = new EconomicMetricResponse(
                latestMetric.getTotalMoney(),
                latestMetric.getAverageAsset(),
                latestMetric.getWeeklyTransactionVolume(),
                latestMetric.getAverageConsumption(),
                latestMetric.getConsumptionChangeRate(),
                latestMetric.getTransactionChangeRate(),
                latestMetric.getSavingRate(),
                latestMetric.getWealthGap(),
                latestMetric.getMeasuredAt()
        );

        // 과거 → 최신 순서의 경제지표 조회
        List<EconomicMetricTrendResponse> trends = economicMetricRepository
                .findByClassIdOrderByMeasuredAtAsc(classroomId)
                .stream()
                .map(metric -> new EconomicMetricTrendResponse(
                        metric.getTotalMoney(),
                        metric.getWeeklyTransactionVolume(),
                        metric.getAverageConsumption(),
                        metric.getSavingRate(),
                        metric.getMeasuredAt()
                ))
                .toList();

        return new ClassroomEconomyIndicatorsResponse(
                latest,
                trends
        );
    }
}