package com.example.seed.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name="account")
public class Account {

    @Id
    @Column(name = "id", length = 45)
    private String id;

    @Column(name = "member_id", nullable = false)
    private Integer memberId;

    @Column(name = "account_type", nullable = false, length = 20)
    private String accountType;

    @Column(name = "balance", nullable = false)
    private Integer balance;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    protected Account() {
    }

    public String getId() {
        return id;
    }

    public Integer getMemberId() {
        return memberId;
    }

    public String getAccountType() {
        return accountType;
    }

    public Integer getBalance() {
        return balance;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
