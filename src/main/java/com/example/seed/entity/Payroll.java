package com.example.seed.entity;

import jakarta.persistence.*;

@Entity
@Table(name="payroll")
public class Payroll {

    @Id
    private Integer id;

    @Column(name="member_id", nullable=false)
    private Integer memberId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="transaction_id", nullable=false)
    private Transaction transaction;

    protected Payroll(){

    }

    public Integer getID(){
        return memberId;
    }

    public Transaction getTransaction(){
        return transaction;
    }
}
