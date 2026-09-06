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

    private static final int RATE_SCALE = 4;
    private static final BigDecimal MIN_STIMULUS_RATE = new BigDecimal("0.01");

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

        // 2. 최신·직전 경제지표 조회
        List<EconomicMetric> recentMetrics = economicMetricRepository
                .findTop2ByClassIdOrderByMeasuredAtDesc(classroomId);

        if (recentMetrics.isEmpty()) {
            throw new NotFoundException("학급 경제지표를 찾을 수 없습니다.");
        }

        EconomicMetric metric = recentMetrics.get(0);
        EconomicMetric previousMetric =
                recentMetrics.size() > 1 ? recentMetrics.get(1) : null;

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

        BigDecimal beforeInflationRate =
                calculateInflationRate(metric, previousMetric);

        String beforeEconomicStatus =
                determineEconomicStatus(
                        metric.getConsumptionChangeRate(),
                        metric.getTransactionChangeRate()
                );

        PolicySimulationMetricResponse before =
                new PolicySimulationMetricResponse(
                        metric.getTotalMoney(),
                        beforeTotalConsumption,
                        beforeInflationRate,
                        metric.getConsumptionChangeRate(),
                        beforeEconomicStatus
                );

        // 6. 정책 적용 후 값 계산
        SimulationResult simulationResult =
                calculateAfter(
                        request.getProposalId(),
                        rate,
                        metric,
                        beforeTotalConsumption,
                        beforeInflationRate
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
                        simulationResult.inflationRate(),
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

    private BigDecimal calculateInflationRate(
            EconomicMetric current,
            EconomicMetric previous
    ) {

        if (previous != null
                && previous.getAverageConsumption().compareTo(BigDecimal.ZERO) > 0) {

            return current.getAverageConsumption()
                    .subtract(previous.getAverageConsumption())
                    .divide(
                            previous.getAverageConsumption(),
                            RATE_SCALE,
                            RoundingMode.HALF_UP
                    );
        }

        // 직전 지표가 없으면 소비 변화율을 수요 견인 물가 대리지표로 사용
        return scaleRate(current.getConsumptionChangeRate());
    }

    private SimulationResult calculateAfter(
            String proposalId,
            BigDecimal rate,
            EconomicMetric metric,
            BigDecimal beforeTotalConsumption,
            BigDecimal beforeInflationRate
    ) {

        BigDecimal multiplier;
        BigDecimal afterConsumptionChangeRate;
        BigDecimal afterTransactionChangeRate;
        BigDecimal afterInflationRate;

        switch (proposalId) {

            case "proposal_tax_increase" -> {
                // 과열 완화: 성장률을 0으로 수렴시켜 STABLE로 전환하고 물가를 낮춘다.
                multiplier = BigDecimal.ONE.subtract(rate);
                afterConsumptionChangeRate = BigDecimal.ZERO;
                afterTransactionChangeRate = BigDecimal.ZERO;
                afterInflationRate = scaleRate(beforeInflationRate.subtract(rate));
            }

            case "proposal_tax_decrease" -> {
                // 경기 활성화: 두 변화율을 양수로 만들어 EXPANSION으로 전환한다.
                BigDecimal stimulatedRate = stimulusRate(rate);

                multiplier = BigDecimal.ONE.add(rate);
                afterConsumptionChangeRate = stimulatedRate;
                afterTransactionChangeRate = stimulatedRate;
                afterInflationRate = scaleRate(beforeInflationRate.add(stimulatedRate));
            }

            case "proposal_maintain_policy" -> {
                multiplier = BigDecimal.ONE;
                afterConsumptionChangeRate = metric.getConsumptionChangeRate();
                afterTransactionChangeRate = metric.getTransactionChangeRate();
                afterInflationRate = beforeInflationRate;
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
                afterInflationRate,
                afterConsumptionChangeRate,
                afterTransactionChangeRate
        );
    }

    private BigDecimal stimulusRate(BigDecimal rate) {

        if (rate.compareTo(BigDecimal.ZERO) > 0) {
            return scaleRate(rate);
        }

        return MIN_STIMULUS_RATE;
    }

    private BigDecimal scaleRate(BigDecimal value) {
        return value.setScale(RATE_SCALE, RoundingMode.HALF_UP);
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

        if (before.getInflationRate() != null
                && after.getInflationRate() != null) {

            int inflationComparison =
                    after.getInflationRate()
                            .compareTo(before.getInflationRate());

            if (inflationComparison < 0) {
                changes.add("물가상승률이 감소했습니다.");
            } else if (inflationComparison > 0) {
                changes.add("물가상승률이 증가했습니다.");
            }
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
            BigDecimal inflationRate,
            BigDecimal consumptionChangeRate,
            BigDecimal transactionChangeRate
    ) {
    }
}
