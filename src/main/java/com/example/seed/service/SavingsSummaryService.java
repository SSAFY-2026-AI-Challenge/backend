package com.example.seed.service;

import com.example.seed.dto.SavingsSummaryResponse;
import com.example.seed.entity.Account;
import com.example.seed.entity.Transaction;
import com.example.seed.exception.NotFoundException;
import com.example.seed.repository.AccountRepository;
import com.example.seed.repository.MemberRepository;
import com.example.seed.repository.TransactionRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;

@Service
public class SavingsSummaryService {

    private final MemberRepository memberRepository;
    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;

    public SavingsSummaryService(
            MemberRepository memberRepository,
            AccountRepository accountRepository,
            TransactionRepository transactionRepository
    ) {
        this.memberRepository = memberRepository;
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
    }

    public SavingsSummaryResponse getSavingsSummary(Integer memberId) {

        // 1. 회원 존재 여부 확인
        memberRepository.findById(memberId)
                .orElseThrow(() ->
                        new NotFoundException(
                                "회원을 찾을 수 없습니다."
                        )
                );

        // 2. 학생 계좌 전체 조회
        List<Account> accounts =
                accountRepository.findByMemberId(memberId);

        if (accounts.isEmpty()) {
            throw new NotFoundException(
                    "계좌 정보를 찾을 수 없습니다."
            );
        }

        // 3. SAVINGS 계좌 찾기
        Account savingsAccount = accounts.stream()
                .filter(account ->
                        "SAVINGS".equals(account.getAccountType())
                )
                .findFirst()
                .orElseThrow(() ->
                        new NotFoundException(
                                "저축 계좌를 찾을 수 없습니다."
                        )
                );

        // 4. 총 저축액
        int totalSavings =
                savingsAccount.getBalance();

        // 5. 전체 자산
        int totalAssets = accounts.stream()
                .mapToInt(Account::getBalance)
                .sum();

        // 6. 이번 달 시작 / 다음 달 시작
        YearMonth currentMonth = YearMonth.now();

        LocalDateTime startDate =
                currentMonth.atDay(1)
                        .atStartOfDay();

        LocalDateTime endDate =
                currentMonth.plusMonths(1)
                        .atDay(1)
                        .atStartOfDay();

        // 7. 이번 달 SAVINGS 계좌 거래 조회
        List<Transaction> monthlyTransactions =
                transactionRepository
                        .findByAccountIdInAndOccuredAtGreaterThanEqualAndOccuredAtLessThanOrderByOccuredAtDesc(
                                List.of(savingsAccount.getId()),
                                startDate,
                                endDate
                        );

        // 8. 이번 달 저축액
        int monthlySavings = monthlyTransactions.stream()
                .mapToInt(Transaction::getAmount)
                .filter(amount -> amount > 0)
                .sum();

        // 9. 이번 달 양수 거래 개수
        long savingsCount = monthlyTransactions.stream()
                .map(Transaction::getAmount)
                .filter(amount -> amount > 0)
                .count();

        // 10. 평균 저축액
        double averageSavings =
                savingsCount == 0
                        ? 0.0
                        : Math.round(
                        ((double) monthlySavings / savingsCount) * 1000
                ) / 1000.0;

        // 11. 저축률
        double savingsRate =
                totalAssets == 0
                        ? 0.0
                        : Math.round(
                        ((double) totalSavings / totalAssets) * 1000
                ) / 1000.0;

        // 12. 응답 생성
        return new SavingsSummaryResponse(
                totalSavings,
                monthlySavings,
                averageSavings,
                savingsRate
        );
    }
}