package com.example.seed.service;

import com.example.seed.dto.SavingsTransferRequest;
import com.example.seed.dto.SavingsTransferResponse;
import com.example.seed.entity.Account;
import com.example.seed.entity.Transaction;
import com.example.seed.exception.BadRequestException;
import com.example.seed.exception.NotFoundException;
import com.example.seed.repository.AccountRepository;
import com.example.seed.repository.MemberRepository;
import com.example.seed.repository.TransactionRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class SavingsTransferService {

    private final MemberRepository memberRepository;
    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;

    public SavingsTransferService(
            MemberRepository memberRepository,
            AccountRepository accountRepository,
            TransactionRepository transactionRepository
    ) {
        this.memberRepository = memberRepository;
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
    }

    @Transactional
    public SavingsTransferResponse transfer(
            Integer memberId,
            SavingsTransferRequest request
    ) {

        // 1. 회원 존재 여부 확인
        memberRepository.findById(memberId)
                .orElseThrow(() ->
                        new NotFoundException("회원을 찾을 수 없습니다.")
                );

        // 2. 요청값 검증
        if (request.fromAccountId() == null
                || request.toAccountId() == null
                || request.amount() == null) {

            throw new BadRequestException(
                    "이체 정보를 모두 입력해주세요."
            );
        }

        if (request.amount() <= 0) {
            throw new BadRequestException(
                    "이체 금액은 0보다 커야 합니다."
            );
        }

        if (request.fromAccountId().equals(request.toAccountId())) {
            throw new BadRequestException(
                    "출금 계좌와 입금 계좌는 같을 수 없습니다."
            );
        }

        // 3. 출금 계좌 조회
        Account fromAccount =
                accountRepository.findById(request.fromAccountId())
                        .orElseThrow(() ->
                                new NotFoundException(
                                        "출금 계좌를 찾을 수 없습니다."
                                )
                        );

        // 4. 입금 계좌 조회
        Account toAccount =
                accountRepository.findById(request.toAccountId())
                        .orElseThrow(() ->
                                new NotFoundException(
                                        "저축 계좌를 찾을 수 없습니다."
                                )
                        );

        // 5. 로그인한 학생 소유의 계좌인지 확인
        if (!fromAccount.getMemberId().equals(memberId)
                || !toAccount.getMemberId().equals(memberId)) {

            throw new BadRequestException(
                    "본인의 계좌만 이체할 수 있습니다."
            );
        }

        // 6. CHECKING -> SAVINGS 이체인지 확인
        if (!"CHECKING".equals(fromAccount.getAccountType())) {
            throw new BadRequestException(
                    "출금 계좌는 CHECKING 계좌여야 합니다."
            );
        }

        if (!"SAVINGS".equals(toAccount.getAccountType())) {
            throw new BadRequestException(
                    "입금 계좌는 SAVINGS 계좌여야 합니다."
            );
        }

        // 7. 잔액 확인
        if (fromAccount.getBalance() < request.amount()) {
            throw new BadRequestException(
                    "출금 계좌의 잔액이 부족합니다."
            );
        }

        // 8. 계좌 잔액 변경
        fromAccount.withdraw(request.amount());
        toAccount.deposit(request.amount());

        // 9. 거래내역 생성
        LocalDateTime now = LocalDateTime.now();

        Transaction withdrawTransaction =
                new Transaction(
                        fromAccount.getId(),
                        -request.amount(),
                        "저축 이체",
                        now
                );

        Transaction depositTransaction =
                new Transaction(
                        toAccount.getId(),
                        request.amount(),
                        "저축 이체",
                        now
                );

        // 10. 거래내역 저장
        transactionRepository.saveAll(
                List.of(
                        withdrawTransaction,
                        depositTransaction
                )
        );

        // 11. 응답 반환
        return new SavingsTransferResponse(
                fromAccount.getId(),
                toAccount.getId(),
                request.amount(),
                fromAccount.getBalance(),
                toAccount.getBalance()
        );
    }
}