package com.example.seed.service;

import com.example.seed.dto.AccountResponse;
import com.example.seed.entity.Account;
import com.example.seed.exception.NotFoundException;
import com.example.seed.repository.AccountRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AccountService {

    private final AccountRepository accountRepository;

    public AccountService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    public List<AccountResponse> getAccounts(Integer memberId) {

        List<Account> accounts =
                accountRepository.findByMemberId(memberId);

        if (accounts.isEmpty()) {
            throw new NotFoundException(
                    "계좌 정보를 찾을 수 없습니다."
            );
        }

        return accounts.stream()
                .map(account ->
                        new AccountResponse(
                                account.getId(),
                                account.getAccountType(),
                                account.getBalance()
                        )
                )
                .toList();
    }
}