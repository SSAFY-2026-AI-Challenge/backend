package com.example.seed.service;

import com.example.seed.dto.TransactionPageResponse;
import com.example.seed.dto.TransactionResponse;
import com.example.seed.entity.Account;
import com.example.seed.entity.Transaction;
import com.example.seed.exception.BadRequestException;
import com.example.seed.exception.NotFoundException;
import com.example.seed.repository.AccountRepository;
import com.example.seed.repository.MemberRepository;
import com.example.seed.repository.TransactionRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.List;

@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;
    private final MemberRepository memberRepository;

    public TransactionService(
            TransactionRepository transactionRepository,
            AccountRepository accountRepository,
            MemberRepository memberRepository
    ) {
        this.transactionRepository = transactionRepository;
        this.accountRepository = accountRepository;
        this.memberRepository = memberRepository;
    }

    public TransactionPageResponse getTransactions(
            Integer memberId,
            String from,
            String to,
            String type,
            String accountId,
            Integer page,
            Integer size
    ) {

        // 1. 회원 존재 여부 확인
        memberRepository.findById(memberId)
                .orElseThrow(() ->
                        new NotFoundException(
                                "회원을 찾을 수 없습니다."
                        )
                );

        // 2. 로그인 학생의 계좌 조회
        List<Account> accounts =
                accountRepository.findByMemberId(memberId);

        if (accounts.isEmpty()) {
            throw new NotFoundException(
                    "계좌 정보를 찾을 수 없습니다."
            );
        }

        // 3. 조회 대상 계좌 결정
        List<String> accountIds;

        if (accountId != null && !accountId.isBlank()) {

            boolean isMyAccount = accounts.stream()
                    .anyMatch(account ->
                            account.getId().equals(accountId)
                    );

            if (!isMyAccount) {
                throw new NotFoundException(
                        "계좌 정보를 찾을 수 없습니다."
                );
            }

            accountIds = List.of(accountId);

        } else {

            accountIds = accounts.stream()
                    .map(Account::getId)
                    .toList();
        }

        // 4. 날짜 파싱
        LocalDateTime startDate = parseStartDate(from);
        LocalDateTime endDate = parseEndDate(to);

        if (startDate != null
                && endDate != null
                && !startDate.isBefore(endDate)) {

            throw new BadRequestException(
                    "from은 to보다 이후일 수 없습니다."
            );
        }

        // 5. 거래 타입 검증
        String normalizedType = normalizeType(type);

        // 6. 페이징 값 검증
        if (page == null || page < 1) {
            throw new BadRequestException(
                    "page는 1 이상이어야 합니다."
            );
        }

        if (size == null || size < 1 || size > 100) {
            throw new BadRequestException(
                    "size는 1 이상 100 이하여야 합니다."
            );
        }

        // API는 page=1부터 시작하지만
        // Spring Data JPA는 page=0부터 시작
        Pageable pageable =
                PageRequest.of(page - 1, size);

        // 7. 거래 내역 조회
        Page<Transaction> transactionPage =
                transactionRepository.findTransactions(
                        accountIds,
                        startDate,
                        endDate,
                        normalizedType,
                        pageable
                );

        // 8. Entity -> DTO 변환
        List<TransactionResponse> items =
                transactionPage.getContent()
                        .stream()
                        .map(this::toResponse)
                        .toList();

        // 9. 최종 페이징 응답
        return new TransactionPageResponse(
                items,
                page,
                size,
                transactionPage.getTotalElements()
        );
    }

    private LocalDateTime parseStartDate(String from) {

        if (from == null || from.isBlank()) {
            return null;
        }

        try {
            return LocalDate.parse(from)
                    .atStartOfDay();

        } catch (DateTimeParseException e) {
            throw new BadRequestException(
                    "from은 YYYY-MM-DD 형식이어야 합니다."
            );
        }
    }

    private LocalDateTime parseEndDate(String to) {

        if (to == null || to.isBlank()) {
            return null;
        }

        try {
            return LocalDate.parse(to)
                    .plusDays(1)
                    .atStartOfDay();

        } catch (DateTimeParseException e) {
            throw new BadRequestException(
                    "to는 YYYY-MM-DD 형식이어야 합니다."
            );
        }
    }

    private String normalizeType(String type) {

        if (type == null || type.isBlank()) {
            return null;
        }

        String normalizedType =
                type.toUpperCase();

        if (!normalizedType.equals("INCOME")
                && !normalizedType.equals("EXPENSE")) {

            throw new BadRequestException(
                    "type은 INCOME 또는 EXPENSE만 가능합니다."
            );
        }

        return normalizedType;
    }

    private TransactionResponse toResponse(
            Transaction transaction
    ) {

        String type =
                transaction.getAmount() > 0
                        ? "INCOME"
                        : "EXPENSE";

        return new TransactionResponse(
                transaction.getId(),
                transaction.getAccountId(),
                transaction.getOccuredAt(),
                type,
                transaction.getDescription(),
                transaction.getAmount()
        );
    }
}