package com.example.seed.service;

import com.example.seed.dto.PayrollItemResponse;
import com.example.seed.dto.PayrollResponse;
import com.example.seed.entity.Member;
import com.example.seed.entity.Payroll;
import com.example.seed.repository.MemberRepository;
import com.example.seed.repository.PayrollRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

import com.example.seed.exception.BadRequestException;
import com.example.seed.exception.NotFoundException;

@Service
public class PayrollService {

    private final MemberRepository memberRepository;
    private final PayrollRepository payrollRepository;

    public PayrollService(
            MemberRepository memberRepository,
            PayrollRepository payrollRepository
    ) {
        this.memberRepository = memberRepository;
        this.payrollRepository = payrollRepository;
    }

    public PayrollResponse getPayroll(Integer memberId, String yearMonth) {

        // 1. "2026-08" 같은 문자열을 YearMonth로 변환
        YearMonth targetMonth;

        try {
            targetMonth = YearMonth.parse(yearMonth);
        } catch (DateTimeParseException e) {
            throw new BadRequestException(
                    "yearMonth는 YYYY-MM 형식이어야 합니다."
            );
        }

        // 2. 조회할 월의 시작일과 다음 달 시작일 계산
        LocalDateTime startDate =
                targetMonth.atDay(1).atStartOfDay();

        LocalDateTime endDate =
                targetMonth.plusMonths(1)
                        .atDay(1)
                        .atStartOfDay();

        // 3. 회원 조회
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() ->
                        new NotFoundException("회원을 찾을 수 없습니다.")
                );

        // 4. 해당 회원의 해당 월 Payroll 조회
        Payroll payroll =
                payrollRepository.findPayrollByMemberIdAndMonth(
                                memberId,
                                startDate,
                                endDate
                        )
                        .orElseThrow(() ->
                                new NotFoundException(
                                        "해당 월의 급여 내역을 찾을 수 없습니다."
                                )
                        );

        // 5. Payroll과 연결된 Transaction에서 실제 월급 가져오기
        int grossPay = payroll.getTransaction().getAmount();

        // 6. 지급 항목
        List<PayrollItemResponse> earnings = new ArrayList<>();

        earnings.add(
                new PayrollItemResponse("기본급", grossPay)
        );

        // 7. 세금 / 공제 항목
        List<PayrollItemResponse> deductions = new ArrayList<>();

        addDeduction(
                deductions,
                member.getTax1Name(),
                member.getTax1Amount()
        );

        addDeduction(
                deductions,
                member.getTax2Name(),
                member.getTax2Amount()
        );

        addDeduction(
                deductions,
                member.getTax3Name(),
                member.getTax3Amount()
        );

        addDeduction(
                deductions,
                member.getTax4Name(),
                member.getTax4Amount()
        );

        addDeduction(
                deductions,
                member.getTax5Name(),
                member.getTax5Amount()
        );

        // 8. 총 공제액 계산
        int totalDeductions = deductions.stream()
                .mapToInt(PayrollItemResponse::getAmount)
                .sum();

        // 9. 실수령액 계산
        int netPay = grossPay - totalDeductions;

        // 10. API 응답 생성
        return new PayrollResponse(
                targetMonth.toString(),
                member.getJob(),
                earnings,
                deductions,
                grossPay,
                totalDeductions,
                netPay,
                "PENDING"
        );
    }

    private void addDeduction(
            List<PayrollItemResponse> deductions,
            String name,
            Integer amount
    ) {
        if (name != null && amount != null) {
            deductions.add(
                    new PayrollItemResponse(name, amount)
            );
        }
    }
}