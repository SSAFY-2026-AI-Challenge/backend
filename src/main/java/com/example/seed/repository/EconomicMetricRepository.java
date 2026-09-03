package com.example.seed.repository;

import com.example.seed.entity.EconomicMetric;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface EconomicMetricRepository
        extends JpaRepository<EconomicMetric, Integer> {

    // 해당 학급의 가장 최근 경제지표 1건
    Optional<EconomicMetric>
    findFirstByClassIdOrderByMeasuredAtDesc(Integer classId);

    // 해당 학급의 경제지표 전체를 과거 → 최신 순으로 조회
    List<EconomicMetric>
    findByClassIdOrderByMeasuredAtAsc(Integer classId);
}