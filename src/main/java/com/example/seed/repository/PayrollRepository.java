package com.example.seed.repository;

import com.example.seed.entity.Payroll;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Optional;

public interface PayrollRepository extends JpaRepository<Payroll, Integer> {

    @Query("""
            SELECT p
            FROM Payroll p
            JOIN FETCH p.transaction t
            WHERE p.memberId = :memberId
              AND t.occuredAt >= :startDate
              AND t.occuredAt < :endDate
            """)
    Optional<Payroll> findPayrollByMemberIdAndMonth(
            @Param("memberId") Integer memberId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );

    @Query("""
            SELECT COALESCE(SUM(t.amount), 0)
            FROM Payroll p
            JOIN p.transaction t
            WHERE p.memberId = :memberId
              AND t.occuredAt >= :startDate
              AND t.occuredAt < :endDate
            """)
    Long sumPayrollAmountByMemberIdAndMonth(
            @Param("memberId") Integer memberId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );
}