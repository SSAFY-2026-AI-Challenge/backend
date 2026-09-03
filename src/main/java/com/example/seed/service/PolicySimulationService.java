package com.example.seed.service;

import com.example.seed.dto.PolicySimulationMetricResponse;
import com.example.seed.dto.PolicySimulationRequest;
import com.example.seed.dto.PolicySimulationResponse;
import com.example.seed.entity.EconomicMetric;
import com.example.seed.exception.BadRequestException;
import com.example.seed.exception.NotFoundException;
import com.example.seed.repository.ClassroomRepository;
import com.example.seed.repository.EconomicMetricRepository;
import com.example.seed.repository.MemberRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

@Service
public class PolicySimulationService {

    private final ClassroomRepository classroomRepository;
    private final EconomicMetricRepository economicMetricRepository;
    private final MemberRepository memberRepository;

    public PolicySimulationService(
            ClassroomRepository classroomRepository,
            EconomicMetricRepository economicMetricRepository,
            MemberRepository memberRepository
    ) {
        this.classroomRepository = classroomRepository;
        this.economicMetricRepository = economicMetricRepository;
        this.memberRepository = memberRepository;
    }

    public PolicySimulationResponse simulate(
            Integer classroomId,
            PolicySimulationRequest request
    ) {

        // 1. 학급 존재 여부 확인
        if (!classroomRepository.existsById(classroomId)) {
            throw new NotFoundException("학급을 찾을 수 없습니다.");
        }

        // 2. 최신 경제지표 조회
        EconomicMetric metric = economicMetricRepository
                .findFirstByClassIdOrderByMeasuredAtDesc(classroomId)
                .orElseThrow(() ->
                        new NotFoundException("학급 경제지표를 찾을 수 없습니다.")
                );

        // 3. 요청값 검증
        validateRequest(request);

        BigDecimal rate = request.getParameters().getIncomeTaxRate();

        // 4. 학급 학생 수 조회
        int studentCount =
                memberRepository.findByClassroomIdAndRole(classroomId, "STUDENT")
                        .size();

        // 5. 정책 적용 전 값 계산
        BigDecimal beforeTotalConsumption =
                metric.getAverageConsumption()
                        .multiply(BigDecimal.valueOf(studentCount));

        String beforeEconomicStatus =
                determineEconomicStatus(
                        metric.getConsumptionChangeRate(),
                        metric.getTransactionChangeRate()
                );

        PolicySimulationMetricResponse before =
                new PolicySimulationMetricResponse(
                        metric.getTotalMoney(),
                        beforeTotalConsumption,
                        null,
                        metric.getConsumptionChangeRate(),
                        beforeEconomicStatus
                );

        // 6. 정책 적용 후 값 계산
        SimulationResult simulationResult =
                calculateAfter(
                        request.getProposalId(),
                        rate,
                        metric,
                        beforeTotalConsumption
                );

        String afterEconomicStatus =
                determineEconomicStatus(
                        simulationResult.consumptionChangeRate(),
                        simulationResult.transactionChangeRate()
                );

        PolicySimulationMetricResponse after =
                new PolicySimulationMetricResponse(
                        simulationResult.moneySupply(),
                        simulationResult.totalConsumption(),
                        null,
                        simulationResult.consumptionChangeRate(),
                        afterEconomicStatus
                );

        // 7. 변화 설명 생성
        List<String> changes =
                createChanges(before, after);

        return new PolicySimulationResponse(
                before,
                after,
                changes
        );
    }

    private void validateRequest(PolicySimulationRequest request) {

        String proposalId = request.getProposalId();

        if (!proposalId.equals("proposal_tax_increase")
                && !proposalId.equals("proposal_tax_decrease")
                && !proposalId.equals("proposal_maintain_policy")) {

            throw new BadRequestException("지원하지 않는 정책입니다.");
        }

        BigDecimal incomeTaxRate =
                request.getParameters().getIncomeTaxRate();

        if (incomeTaxRate.compareTo(BigDecimal.ZERO) < 0
                || incomeTaxRate.compareTo(BigDecimal.ONE) > 0) {

            throw new BadRequestException(
                    "incomeTaxRate는 0.0 이상 1.0 이하이어야 합니다."
            );
        }
    }

    private SimulationResult calculateAfter(
            String proposalId,
            BigDecimal rate,
            EconomicMetric metric,
            BigDecimal beforeTotalConsumption
    ) {

        BigDecimal multiplier;

        BigDecimal afterConsumptionChangeRate;
        BigDecimal afterTransactionChangeRate;

        switch (proposalId) {

            case "proposal_tax_increase" -> {

                multiplier = BigDecimal.ONE.subtract(rate);

                afterConsumptionChangeRate =
                        metric.getConsumptionChangeRate()
                                .subtract(rate);

                afterTransactionChangeRate =
                        metric.getTransactionChangeRate()
                                .subtract(rate);
            }

            case "proposal_tax_decrease" -> {

                multiplier = BigDecimal.ONE.add(rate);

                afterConsumptionChangeRate =
                        metric.getConsumptionChangeRate()
                                .add(rate);

                afterTransactionChangeRate =
                        metric.getTransactionChangeRate()
                                .add(rate);
            }

            case "proposal_maintain_policy" -> {

                multiplier = BigDecimal.ONE;

                afterConsumptionChangeRate =
                        metric.getConsumptionChangeRate();

                afterTransactionChangeRate =
                        metric.getTransactionChangeRate();
            }

            default ->
                    throw new BadRequestException("지원하지 않는 정책입니다.");
        }

        int afterMoneySupply =
                BigDecimal.valueOf(metric.getTotalMoney())
                        .multiply(multiplier)
                        .setScale(0, RoundingMode.HALF_UP)
                        .intValue();

        BigDecimal afterTotalConsumption =
                beforeTotalConsumption
                        .multiply(multiplier)
                        .setScale(2, RoundingMode.HALF_UP);

        return new SimulationResult(
                afterMoneySupply,
                afterTotalConsumption,
                afterConsumptionChangeRate,
                afterTransactionChangeRate
        );
    }

    private String determineEconomicStatus(
            BigDecimal consumptionChangeRate,
            BigDecimal transactionChangeRate
    ) {

        int consumptionComparison =
                consumptionChangeRate.compareTo(BigDecimal.ZERO);

        int transactionComparison =
                transactionChangeRate.compareTo(BigDecimal.ZERO);

        if (consumptionComparison > 0
                && transactionComparison > 0) {
            return "EXPANSION";
        }

        if (consumptionComparison < 0
                && transactionComparison < 0) {
            return "CONTRACTION";
        }

        if (consumptionComparison == 0
                && transactionComparison == 0) {
            return "STABLE";
        }

        return "MIXED";
    }

    private List<String> createChanges(
            PolicySimulationMetricResponse before,
            PolicySimulationMetricResponse after
    ) {

        List<String> changes = new ArrayList<>();

        if (after.getMoneySupply() < before.getMoneySupply()) {
            changes.add("총 통화량이 감소했습니다.");
        } else if (after.getMoneySupply() > before.getMoneySupply()) {
            changes.add("총 통화량이 증가했습니다.");
        }

        int consumptionComparison =
                after.getTotalConsumption()
                        .compareTo(before.getTotalConsumption());

        if (consumptionComparison < 0) {
            changes.add("총 소비액이 감소했습니다.");
        } else if (consumptionComparison > 0) {
            changes.add("총 소비액이 증가했습니다.");
        }

        if (!before.getEconomicStatus()
                .equals(after.getEconomicStatus())) {

            changes.add(
                    "경제상태가 "
                            + before.getEconomicStatus()
                            + "에서 "
                            + after.getEconomicStatus()
                            + "로 변경되었습니다."
            );
        }

        if (changes.isEmpty()) {
            changes.add("경제지표에 변화가 없습니다.");
        }

        return changes;
    }

    private record SimulationResult(
            Integer moneySupply,
            BigDecimal totalConsumption,
            BigDecimal consumptionChangeRate,
            BigDecimal transactionChangeRate
    ) {
    }
}