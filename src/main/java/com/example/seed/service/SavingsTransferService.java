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
        if (request == null || request.amount() == null) {
            throw new BadRequestException(
                    "이체 금액을 입력해주세요."
            );
        }

        if (request.amount() <= 0) {
            throw new BadRequestException(
                    "이체 금액은 0보다 커야 합니다."
            );
        }

        Account fromAccount;
        Account toAccount;

        boolean hasFrom = request.fromAccountId() != null && !request.fromAccountId().trim().isEmpty();
        boolean hasTo = request.toAccountId() != null && !request.toAccountId().trim().isEmpty();

        // 출금 계좌와 입금 계좌가 모두 지정되었고 서로 다른 경우: 지정된 계좌 사용
        if (hasFrom && hasTo && !request.fromAccountId().trim().equals(request.toAccountId().trim())) {
            fromAccount = accountRepository.findById(request.fromAccountId().trim())
                    .orElseThrow(() ->
                            new NotFoundException(
                                    "출금 계좌를 찾을 수 없습니다."
                            )
                    );

            toAccount = accountRepository.findById(request.toAccountId().trim())
                    .orElseThrow(() ->
                            new NotFoundException(
                                    "저축 계좌를 찾을 수 없습니다."
                            )
                    );

            // 로그인한 학생 소유의 계좌인지 확인
            if (!fromAccount.getMemberId().equals(memberId)
                    || !toAccount.getMemberId().equals(memberId)) {
                throw new BadRequestException(
                        "본인의 계좌만 이체할 수 있습니다."
                );
            }

            // CHECKING -> SAVINGS 이체인지 확인
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
        } else {
            // 계좌 ID가 생략되었거나 둘 다 동일하게 넘어온 경우: 학생 소유의 CHECKING 및 SAVINGS 계좌 자동 매핑
            List<Account> accounts = accountRepository.findByMemberId(memberId);

            if (accounts.isEmpty()) {
                throw new NotFoundException("계좌 정보를 찾을 수 없습니다.");
            }

            fromAccount = accounts.stream()
                    .filter(account -> "CHECKING".equals(account.getAccountType()))
                    .findFirst()
                    .orElseThrow(() ->
                            new NotFoundException("출금할 당좌(CHECKING) 계좌를 찾을 수 없습니다.")
                    );

            toAccount = accounts.stream()
                    .filter(account -> "SAVINGS".equals(account.getAccountType()))
                    .findFirst()
                    .orElseThrow(() ->
                            new NotFoundException("입금할 저축(SAVINGS) 계좌를 찾을 수 없습니다.")
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