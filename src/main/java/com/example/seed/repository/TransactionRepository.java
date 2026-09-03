package com.example.seed.repository;

import com.example.seed.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TransactionRepository
        extends JpaRepository<Transaction, Integer> {

    List<Transaction>
    findByAccountIdInAndOccuredAtGreaterThanEqualAndOccuredAtLessThanOrderByOccuredAtDesc(
            List<String> accountIds,
            LocalDateTime startDate,
            LocalDateTime endDate
    );

    List<Transaction>
    findTop5ByAccountIdInOrderByOccuredAtDesc(
            List<String> accountIds
    );

    @Query("""
        SELECT t
        FROM Transaction t
        WHERE t.accountId IN :accountIds
          AND (:startDate IS NULL OR t.occuredAt >= :startDate)
          AND (:endDate IS NULL OR t.occuredAt < :endDate)
          AND (
                :type IS NULL
                OR (:type = 'INCOME' AND t.amount > 0)
                OR (:type = 'EXPENSE' AND t.amount < 0)
              )
        ORDER BY t.occuredAt DESC
        """)
    Page<Transaction> findTransactions(
            @Param("accountIds") List<String> accountIds,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            @Param("type") String type,
            Pageable pageable
    );

    @Query("""
        SELECT COALESCE(SUM(t.amount), 0)
        FROM Transaction t
        WHERE t.accountId = :accountId
          AND t.occuredAt >= :startDate
        """)
    Long sumAmountAfter(
            @Param("accountId") String accountId,
            @Param("startDate") LocalDateTime startDate
    );
}