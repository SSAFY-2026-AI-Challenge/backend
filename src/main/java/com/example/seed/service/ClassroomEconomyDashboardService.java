package com.example.seed.service;

import com.example.seed.dto.ClassroomEconomyDashboardResponse;
import com.example.seed.dto.ClassroomEconomyKpiResponse;
import com.example.seed.entity.Account;
import com.example.seed.entity.Member;
import com.example.seed.entity.Transaction;
import com.example.seed.exception.NotFoundException;
import com.example.seed.repository.AccountRepository;
import com.example.seed.repository.ClassroomRepository;
import com.example.seed.repository.MemberRepository;
import com.example.seed.repository.TransactionRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class ClassroomEconomyDashboardService {

    private final ClassroomRepository classroomRepository;
    private final MemberRepository memberRepository;
    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;

    public ClassroomEconomyDashboardService(
            ClassroomRepository classroomRepository,
            MemberRepository memberRepository,
            AccountRepository accountRepository,
            TransactionRepository transactionRepository
    ) {
        this.classroomRepository = classroomRepository;
        this.memberRepository = memberRepository;
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
    }

    public ClassroomEconomyDashboardResponse getDashboard(Integer classroomId) {

        // 1. 학급 존재 여부 확인
        classroomRepository.findById(classroomId)
                .orElseThrow(() ->
                        new NotFoundException("학급을 찾을 수 없습니다.")
                );

        // 2. 해당 학급 학생 조회
        List<Member> students =
                memberRepository.findByClassroomIdAndRole(
                        classroomId,
                        "STUDENT"
                );

        int moneySupply = 0;
        int totalSavings = 0;

        List<String> accountIds = new ArrayList<>();

        // 3. 학생들의 계좌 정보 집계
        for (Member student : students) {

            List<Account> accounts =
                    accountRepository.findByMemberId(
                            student.getId()
                    );

            for (Account account : accounts) {

                moneySupply += account.getBalance();

                if ("SAVINGS".equals(account.getAccountType())) {
                    totalSavings += account.getBalance();
                }

                accountIds.add(account.getId());
            }
        }

        // 4. 평균 보유액
        int averageBalance = students.isEmpty()
                ? 0
                : moneySupply / students.size();

        // 5. 이번 달 범위 계산
        LocalDate today = LocalDate.now();

        LocalDateTime startDate = today
                .withDayOfMonth(1)
                .atStartOfDay();

        LocalDateTime endDate = startDate.plusMonths(1);

        int totalConsumption = 0;
        int transactionVolume = 0;

        // 계좌가 하나라도 있을 때만 거래 조회
        if (!accountIds.isEmpty()) {

            List<Transaction> transactions =
                    transactionRepository
                            .findByAccountIdInAndOccuredAtGreaterThanEqualAndOccuredAtLessThanOrderByOccuredAtDesc(
                                    accountIds,
                                    startDate,
                                    endDate
                            );

            transactionVolume = transactions.size();

            totalConsumption = transactions.stream()
                    .filter(transaction -> transaction.getAmount() < 0)
                    .mapToInt(transaction -> Math.abs(transaction.getAmount()))
                    .sum();
        }

        ClassroomEconomyKpiResponse kpis =
                new ClassroomEconomyKpiResponse(
                        moneySupply,
                        averageBalance,
                        totalConsumption,
                        totalSavings,
                        transactionVolume
                );

        return new ClassroomEconomyDashboardResponse(kpis);
    }
}