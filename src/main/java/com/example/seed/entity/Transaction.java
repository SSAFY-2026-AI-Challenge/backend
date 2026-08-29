package com.example.seed.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "`transaction`")
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "account_id", nullable = false, length = 45)
    private String accountId;

    @Column(name = "amount", nullable = false)
    private Integer amount;

    @Column(name = "description")
    private String description;

    @Column(name = "occured_at", nullable = false)
    private LocalDateTime occuredAt;

    protected Transaction() {
    }

    public Transaction(
            String accountId,
            Integer amount,
            String description,
            LocalDateTime occuredAt
    ) {
        this.accountId = accountId;
        this.amount = amount;
        this.description = description;
        this.occuredAt = occuredAt;
    }

    public Integer getId() {
        return id;
    }

    public String getAccountId() {
        return accountId;
    }

    public Integer getAmount() {
        return amount;
    }

    public String getDescription() {
        return description;
    }

    public LocalDateTime getOccuredAt() {
        return occuredAt;
    }
}