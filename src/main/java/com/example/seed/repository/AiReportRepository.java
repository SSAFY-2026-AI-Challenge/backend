package com.example.seed.repository;

import com.example.seed.entity.AiReport;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Optional;

public interface AiReportRepository extends JpaRepository<AiReport, Integer> {

    Optional<AiReport> findFirstByMemberIdAndReportTypeAndGeneratedAtGreaterThanEqualAndGeneratedAtLessThanOrderByGeneratedAtDesc(
    //        findFirstBy
    //        → 조건에 맞는 것 중 하나 찾기
    //
    //        MemberId
    //        → 현재 로그인한 학생
    //
    //        AndReportType
    //        → CREDIT_REPORT
    //
    //        AndGeneratedAtGreaterThanEqual
    //        → 2026-03-01 이상
    //
    //        AndGeneratedAtLessThan
    //        → 2026-04-01 미만
    //
    //        OrderByGeneratedAtDesc
    //        → 만약 같은 달에 여러 개 있으면 가장 최근 리포트

            Integer memberId,
            String reportType,
            LocalDate startDate,
            LocalDate endDate
    );

    Optional<AiReport> findFirstByMemberIdAndReportTypeOrderByGeneratedAtDesc(
            Integer memberId,
            String reportType
    );
}