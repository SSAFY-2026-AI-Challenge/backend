package com.example.seed.service;

import com.example.seed.dto.MonthlyResultResponse;
import com.example.seed.entity.Account;
import com.example.seed.entity.Member;
import com.example.seed.entity.Transaction;
import com.example.seed.exception.BadRequestException;
import com.example.seed.exception.NotFoundException;
import com.example.seed.repository.AccountRepository;
import com.example.seed.repository.MemberRepository;
import com.example.seed.repository.PayrollRepository;
import com.example.seed.repository.TransactionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class MonthlyResultService {

    private final MemberRepository memberRepository;
    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;
    private final PayrollRepository payrollRepository;

    public MonthlyResultService(
            MemberRepository memberRepository,
            AccountRepository accountRepository,
            TransactionRepository transactionRepository,
            PayrollRepository payrollRepository
    ) {
        this.memberRepository = memberRepository;
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
        this.payrollRepository = payrollRepository;
    }

    public MonthlyResultResponse getMonthlyResult(
            Integer memberId,
            String yearMonth
    ) {

        // 1. yearMonth 형식 검증
        YearMonth targetMonth = parseYearMonth(yearMonth);

        LocalDateTime startDate =
                targetMonth.atDay(1).atStartOfDay();

        LocalDateTime endDate =
                targetMonth.plusMonths(1)
                        .atDay(1)
                        .atStartOfDay();

        // 2. 학생 조회
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() ->
                        new NotFoundException("회원을 찾을 수 없습니다.")
                );

        // 3. 학생 계좌 조회
        List<Account> accounts =
                accountRepository.findByMemberId(memberId);

        if (accounts.isEmpty()) {
            throw new NotFoundException("계좌를 찾을 수 없습니다.");
        }

        // 4. 해당 월 총수입
        Long incomeAmount =
                payrollRepository.sumPayrollAmountByMemberIdAndMonth(
                        memberId,
                        startDate,
                        endDate
                );

        int totalIncome =
                incomeAmount == null
                        ? 0
                        : Math.toIntExact(incomeAmount);

        // 5. 세금 / 공제
        int totalDeductions = calculateTotalDeductions(member);

        // 6. 실수령액
        int netIncome = totalIncome - totalDeductions;

        // 7. 계좌 타입별 ID 분리
        List<String> savingsAccountIds = accounts.stream()
                .filter(account ->
                        "SAVINGS".equals(account.getAccountType()))
                .map(Account::getId)
                .toList();

        List<String> checkingAccountIds = accounts.stream()
                .filter(account ->
                        "CHECKING".equals(account.getAccountType()))
                .map(Account::getId)
                .toList();

        // 8. 해당 월 총저축
        int totalSavings =
                calculateMonthlySavings(
                        savingsAccountIds,
                        startDate,
                        endDate
                );

        // 9. 해당 월 CHECKING 전체 출금액
        int checkingOutflow =
                calculateCheckingOutflow(
                        checkingAccountIds,
                        startDate,
                        endDate
                );

        // 10. 총소비
        int totalConsumption =
                Math.max(
                        checkingOutflow - totalSavings,
                        0
                );

        // 11. 대상 월 말 CHECKING 잔액
        int balance = accounts.stream()
                .filter(account ->
                        "CHECKING".equals(account.getAccountType()))
                .mapToInt(account ->
                        calculateMonthEndBalance(
                                account,
                                endDate
                        )
                )
                .sum();

        // 12. 대상 월 말 총자산
        int totalAssets = accounts.stream()
                .mapToInt(account ->
                        calculateMonthEndBalance(
                                account,
                                endDate
                        )
                )
                .sum();

        // 13. 전월 말 총자산
        // 대상 월 시작 시점 = 전월이 끝난 직후 시점
        int previousTotalAssets = accounts.stream()
                .mapToInt(account ->
                        calculateMonthEndBalance(
                                account,
                                startDate
                        )
                )
                .sum();

        // 14. 자산 변화
        int assetChange =
                totalAssets - previousTotalAssets;

        // 15. 저축률
        BigDecimal savingsRate =
                calculateSavingsRate(
                        totalSavings,
                        totalIncome
                );

        return new MonthlyResultResponse(
                yearMonth,
                totalIncome,
                totalDeductions,
                netIncome,
                totalConsumption,
                totalSavings,
                balance,
                totalAssets,
                savingsRate,
                assetChange
        );
    }

    private YearMonth parseYearMonth(String yearMonth) {

        if (yearMonth == null
                || !yearMonth.matches("\\d{4}-(0[1-9]|1[0-2])")) {

            throw new BadRequestException(
                    "yearMonth 형식은 YYYY-MM이어야 합니다."
            );
        }

        return YearMonth.parse(yearMonth);
    }

    private int calculateTotalDeductions(Member member) {

        return valueOrZero(member.getTax1Amount())
                + valueOrZero(member.getTax2Amount())
                + valueOrZero(member.getTax3Amount())
                + valueOrZero(member.getTax4Amount())
                + valueOrZero(member.getTax5Amount());
    }

    private int valueOrZero(Integer value) {
        return value == null ? 0 : value;
    }

    private int calculateMonthlySavings(
            List<String> accountIds,
            LocalDateTime startDate,
            LocalDateTime endDate
    ) {

        if (accountIds.isEmpty()) {
            return 0;
        }

        List<Transaction> transactions =
                transactionRepository
                        .findByAccountIdInAndOccuredAtGreaterThanEqualAndOccuredAtLessThanOrderByOccuredAtDesc(
                                accountIds,
                                startDate,
                                endDate
                        );

        return transactions.stream()
                .mapToInt(Transaction::getAmount)
                .filter(amount -> amount > 0)
                .sum();
    }

    private int calculateCheckingOutflow(
            List<String> accountIds,
            LocalDateTime startDate,
            LocalDateTime endDate
    ) {

        if (accountIds.isEmpty()) {
            return 0;
        }

        List<Transaction> transactions =
                transactionRepository
                        .findByAccountIdInAndOccuredAtGreaterThanEqualAndOccuredAtLessThanOrderByOccuredAtDesc(
                                accountIds,
                                startDate,
                                endDate
                        );

        return transactions.stream()
                .mapToInt(Transaction::getAmount)
                .filter(amount -> amount < 0)
                .map(amount -> Math.abs(amount))
                .sum();
    }

    private int calculateMonthEndBalance(
            Account account,
            LocalDateTime boundary
    ) {

        Long amountAfter =
                transactionRepository.sumAmountAfter(
                        account.getId(),
                        boundary
                );

        long transactionAmountAfter =
                amountAfter == null ? 0L : amountAfter;

        long monthEndBalance =
                (long) account.getBalance()
                        - transactionAmountAfter;

        return Math.toIntExact(monthEndBalance);
    }

    private BigDecimal calculateSavingsRate(
            int totalSavings,
            int totalIncome
    ) {

        if (totalIncome == 0) {
            return BigDecimal.ZERO.setScale(3);
        }

        return BigDecimal.valueOf(totalSavings)
                .divide(
                        BigDecimal.valueOf(totalIncome),
                        3,
                        RoundingMode.HALF_UP
                );
    }
}