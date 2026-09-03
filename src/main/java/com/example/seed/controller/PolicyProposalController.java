package com.example.seed.controller;

import com.example.seed.dto.PolicyProposalResponse;
import com.example.seed.service.PolicyProposalService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/classrooms/{classroomId}/policy-proposals")
@Tag(
        name = "Teacher - Policy Proposal",
        description = "교사 학급 정책 제안 API"
)
public class PolicyProposalController {

    private final PolicyProposalService policyProposalService;

    public PolicyProposalController(
            PolicyProposalService policyProposalService
    ) {
        this.policyProposalService = policyProposalService;
    }

    @GetMapping
    @Operation(
            summary = "학급 정책 제안 목록 조회",
            description = """
                    특정 학급의 최신 economic_metric 데이터를 기반으로
                    현재 경제상태에 맞는 정책 제안 목록을 반환합니다.

                    현재 MVP에서는 AI 정책 추천 API 연동 전까지
                    소비 변화율과 거래 변화율을 기반으로 경제상태를 판정하고,
                    규칙 기반으로 정책 제안 목록을 생성합니다.

                    정책 제안 결과는 DB에 저장하지 않습니다.
                    """
    )
    public ResponseEntity<List<PolicyProposalResponse>> getPolicyProposals(
            @Parameter(
                    description = "정책 제안을 조회할 학급 ID",
                    example = "1"
            )
            @PathVariable Integer classroomId
    ) {

        List<PolicyProposalResponse> response =
                policyProposalService.getPolicyProposals(classroomId);

        return ResponseEntity.ok(response);
    }
}