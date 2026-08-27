package com.example.seed.service;

import com.example.seed.dto.StudentDashboardResponse;
import com.example.seed.entity.Account;
import com.example.seed.entity.AiReport;
import com.example.seed.entity.Member;
import com.example.seed.entity.Transaction;
import com.example.seed.exception.NotFoundException;
import com.example.seed.repository.AccountRepository;
import com.example.seed.repository.AiReportRepository;
import com.example.seed.repository.MemberRepository;
import com.example.seed.repository.TransactionRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;

@Service
public class StudentDashboardService {

    private static final String REPORT_TYPE = "CREDIT_REPORT";

    private final MemberRepository memberRepository;
    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;
    private final AiReportRepository aiReportRepository;
    private final ObjectMapper objectMapper;

    public StudentDashboardService(
            MemberRepository memberRepository,
            AccountRepository accountRepository,
            TransactionRepository transactionRepository,
            AiReportRepository aiReportRepository,
            ObjectMapper objectMapper
    ) {
        this.memberRepository = memberRepository;
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
        this.aiReportRepository = aiReportRepository;
        this.objectMapper = objectMapper;
    }

    public StudentDashboardResponse getDashboard(Integer memberId) {

        // 1. 회원 조회
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() ->
                        new NotFoundException("회원을 찾을 수 없습니다.")
                );

        // 2. 학생 계좌 조회
        List<Account> accounts =
                accountRepository.findByMemberId(memberId);

        if (accounts.isEmpty()) {
            throw new NotFoundException(
                    "계좌 정보를 찾을 수 없습니다."
            );
        }

        // 3. 계좌 ID 목록
        List<String> accountIds = accounts.stream()
                .map(Account::getId)
                .toList();

        // 4. 총자산 계산
        int totalAssets = accounts.stream()
                .mapToInt(Account::getBalance)
                .sum();

        // 5. 저축 계좌 잔액
        int savingsBalance = accounts.stream()
                .filter(account ->
                        "SAVINGS".equals(account.getAccountType())
                )
                .mapToInt(Account::getBalance)
                .sum();

        // 6. 현재 월 계산
        YearMonth currentMonth = YearMonth.now();

        LocalDateTime startDate =
                currentMonth.atDay(1).atStartOfDay();

        LocalDateTime endDate =
                currentMonth.plusMonths(1)
                        .atDay(1)
                        .atStartOfDay();

        // 7. 이번 달 거래 조회
        List<Transaction> monthlyTransactions =
                transactionRepository
                        .findByAccountIdInAndOccuredAtGreaterThanEqualAndOccuredAtLessThanOrderByOccuredAtDesc(
                                accountIds,
                                startDate,
                                endDate
                        );

        // 8. 이번 달 수입
        int incomeThisMonth = monthlyTransactions.stream()
                .mapToInt(Transaction::getAmount)
                .filter(amount -> amount > 0)
                .sum();

        // 9. 이번 달 지출
        int expenseThisMonth = monthlyTransactions.stream()
                .mapToInt(Transaction::getAmount)
                .filter(amount -> amount < 0)
                .map(Math::abs)
                .sum();

        // 10. 저축률
        double savingsRate =
                totalAssets == 0
                        ? 0.0
                        : (double) savingsBalance / totalAssets;

        // 11. 최근 거래 5개
        List<StudentDashboardResponse.RecentTransaction>
                recentTransactions =
                transactionRepository
                        .findTop5ByAccountIdInOrderByOccuredAtDesc(accountIds)
                        .stream()
                        .map(transaction ->
                                new StudentDashboardResponse.RecentTransaction(
                                        transaction.getId(),
                                        transaction.getOccuredAt(),
                                        transaction.getDescription(),
                                        transaction.getAmount()
                                )
                        )
                        .toList();

        // 12. 신용 정보
        StudentDashboardResponse.Credit credit =
                getCredit(memberId, currentMonth);

        // 13. 최종 응답
        return new StudentDashboardResponse(
                currentMonth.toString(),
                member.getJob(),
                incomeThisMonth,
                expenseThisMonth,
                totalAssets,
                savingsBalance,
                savingsRate,
                credit,
                recentTransactions
        );
    }

    private StudentDashboardResponse.Credit getCredit(
            Integer memberId,
            YearMonth currentMonth
    ) {

        LocalDate startDate =
                currentMonth.atDay(1);

        LocalDate endDate =
                currentMonth.plusMonths(1)
                        .atDay(1);

        return aiReportRepository
                .findFirstByMemberIdAndReportTypeAndGeneratedAtGreaterThanEqualAndGeneratedAtLessThanOrderByGeneratedAtDesc(
                        memberId,
                        REPORT_TYPE,
                        startDate,
                        endDate
                )
                .map(this::toCredit)
                .orElse(
                        new StudentDashboardResponse.Credit(
                                null,
                                null
                        )
                );
    }

    private StudentDashboardResponse.Credit toCredit(
            AiReport report
    ) {

        try {
            JsonNode detail =
                    objectMapper.readTree(
                            report.getDetailJson()
                    );

            return new StudentDashboardResponse.Credit(
                    detail.path("score").asInt(),
                    detail.path("grade").asText()
            );

        } catch (Exception e) {
            return new StudentDashboardResponse.Credit(
                    null,
                    null
            );
        }
    }
}