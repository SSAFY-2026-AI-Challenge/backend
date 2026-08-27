package com.example.seed.repository;

import com.example.seed.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface TransactionRepository
        extends JpaRepository<Transaction, Integer>{

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
}
