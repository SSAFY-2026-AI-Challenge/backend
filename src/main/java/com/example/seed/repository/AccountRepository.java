package com.example.seed.repository;

import com.example.seed.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AccountRepository extends JpaRepository<Account, String> {

    List<Account> findByMemberId(Integer memberId);
}