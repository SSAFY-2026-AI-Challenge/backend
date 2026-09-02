package com.example.seed.repository;

import com.example.seed.entity.AiReport;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.Optional;

public interface AiReportRepository extends JpaRepository<AiReport, Integer> {

    Optional<AiReport> findByMemberId(Integer memberId);

    Optional<AiReport> findFirstByMemberIdOrderByGeneratedAtDesc(Integer memberId);

    Optional<AiReport> findFirstByMemberIdAndGeneratedAtGreaterThanEqualAndGeneratedAtLessThanOrderByGeneratedAtDesc(
            Integer memberId,
            LocalDateTime startDate,
            LocalDateTime endDate
    );
}
