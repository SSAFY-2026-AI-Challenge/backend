package com.example.seed.service;

import com.example.seed.dto.PolicyProposalResponse;
import com.example.seed.entity.EconomicMetric;
import com.example.seed.exception.NotFoundException;
import com.example.seed.repository.ClassroomRepository;
import com.example.seed.repository.EconomicMetricRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class PolicyProposalService {

    private final ClassroomRepository classroomRepository;
    private final EconomicMetricRepository economicMetricRepository;

    public PolicyProposalService(
            ClassroomRepository classroomRepository,
            EconomicMetricRepository economicMetricRepository
    ) {
        this.classroomRepository = classroomRepository;
        this.economicMetricRepository = economicMetricRepository;
    }

    public List<PolicyProposalResponse> getPolicyProposals(Integer classroomId) {

        // 학급 존재 여부 확인
        if (!classroomRepository.existsById(classroomId)) {
            throw new NotFoundException("학급을 찾을 수 없습니다.");
        }

        // 해당 학급의 가장 최신 경제지표 조회
        EconomicMetric metric = economicMetricRepository
                .findFirstByClassIdOrderByMeasuredAtDesc(classroomId)
                .orElseThrow(() ->
                        new NotFoundException("학급 경제지표를 찾을 수 없습니다.")
                );

        String economicStatus = determineEconomicStatus(metric);

        return createPolicyProposals(economicStatus);
    }

    private String determineEconomicStatus(EconomicMetric metric) {

        int consumptionComparison =
                metric.getConsumptionChangeRate().compareTo(BigDecimal.ZERO);

        int transactionComparison =
                metric.getTransactionChangeRate().compareTo(BigDecimal.ZERO);

        if (consumptionComparison > 0 && transactionComparison > 0) {
            return "EXPANSION";
        }

        if (consumptionComparison < 0 && transactionComparison < 0) {
            return "CONTRACTION";
        }

        if (consumptionComparison == 0 && transactionComparison == 0) {
            return "STABLE";
        }

        return "MIXED";
    }

    private List<PolicyProposalResponse> createPolicyProposals(
            String economicStatus
    ) {

        List<PolicyProposalResponse> proposals = new ArrayList<>();

        switch (economicStatus) {

            case "EXPANSION" -> {
                proposals.add(createTaxIncreaseProposal());
                proposals.add(createMaintainPolicyProposal());
            }

            case "CONTRACTION" -> {
                proposals.add(createTaxDecreaseProposal());
                proposals.add(createMaintainPolicyProposal());
            }

            case "STABLE", "MIXED" ->
                    proposals.add(createMaintainPolicyProposal());
        }

        return proposals;
    }

    private PolicyProposalResponse createTaxIncreaseProposal() {

        return new PolicyProposalResponse(
                "proposal_tax_increase",
                "TAX_INCREASE",
                "소득세율 인상",
                "학급 경제의 소비 및 거래 증가를 완화하기 위해 소득세율 인상을 제안합니다.",
                "과도한 경제활동 완화"
        );
    }

    private PolicyProposalResponse createTaxDecreaseProposal() {

        return new PolicyProposalResponse(
                "proposal_tax_decrease",
                "TAX_DECREASE",
                "소득세율 인하",
                "위축된 소비 및 거래 활동을 활성화하기 위해 소득세율 인하를 제안합니다.",
                "경제활동 활성화"
        );
    }

    private PolicyProposalResponse createMaintainPolicyProposal() {

        return new PolicyProposalResponse(
                "proposal_maintain_policy",
                "MAINTAIN_POLICY",
                "현행 정책 유지",
                "현재 경제 흐름을 유지하며 추가적인 정책 변경 없이 경제지표를 관찰합니다.",
                "경제 안정성 유지"
        );
    }
}