package com.example.seed.service;

import com.example.seed.dto.SavingsTrendsResponse;
import com.example.seed.entity.Account;
import com.example.seed.entity.Transaction;
import com.example.seed.exception.NotFoundException;
import com.example.seed.repository.AccountRepository;
import com.example.seed.repository.MemberRepository;
import com.example.seed.repository.TransactionRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

@Service
public class SavingsTrendsService {

    private final MemberRepository memberRepository;
    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;

    public SavingsTrendsService(
            MemberRepository memberRepository,
            AccountRepository accountRepository,
            TransactionRepository transactionRepository
    ) {
        this.memberRepository = memberRepository;
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
    }

    public SavingsTrendsResponse getSavingsTrends(Integer memberId) {

        // 1. 회원 존재 여부 확인
        memberRepository.findById(memberId)
                .orElseThrow(() ->
                        new NotFoundException(
                                "회원을 찾을 수 없습니다."
                        )
                );

        // 2. 학생 계좌 조회
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

        // 4. 현재 월 포함 최근 6개월 범위 계산
        YearMonth currentMonth = YearMonth.now();
        YearMonth startMonth = currentMonth.minusMonths(5);

        LocalDateTime startDate =
                startMonth.atDay(1)
                        .atStartOfDay();

        LocalDateTime endDate =
                currentMonth.plusMonths(1)
                        .atDay(1)
                        .atStartOfDay();

        // 5. 최근 6개월 SAVINGS 계좌 거래 조회
        List<Transaction> transactions =
                transactionRepository
                        .findByAccountIdInAndOccuredAtGreaterThanEqualAndOccuredAtLessThanOrderByOccuredAtDesc(
                                List.of(savingsAccount.getId()),
                                startDate,
                                endDate
                        );

        // 6. 월별 저축액 계산
        List<SavingsTrendsResponse.MonthlySaving> trends =
                new ArrayList<>();

        for (int i = 0; i < 6; i++) {

            YearMonth targetMonth =
                    startMonth.plusMonths(i);

            int monthlyAmount = transactions.stream()
                    .filter(transaction ->
                            transaction.getAmount() > 0
                    )
                    .filter(transaction ->
                            YearMonth.from(
                                    transaction.getOccuredAt()
                            ).equals(targetMonth)
                    )
                    .mapToInt(Transaction::getAmount)
                    .sum();

            trends.add(
                    new SavingsTrendsResponse.MonthlySaving(
                            targetMonth.toString(),
                            monthlyAmount
                    )
            );
        }

        // 7. 응답 반환
        return new SavingsTrendsResponse(trends);
    }
}