package com.example.seed.controller;

import com.example.seed.dto.PolicySimulationRequest;
import com.example.seed.dto.PolicySimulationResponse;
import com.example.seed.service.PolicySimulationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/classrooms/{classroomId}/policy-simulations")
@Tag(
        name = "Teacher - Policy Simulation",
        description = "교사 정책 적용 전 시뮬레이션 API"
)
public class PolicySimulationController {

    private final PolicySimulationService policySimulationService;

    public PolicySimulationController(
            PolicySimulationService policySimulationService
    ) {
        this.policySimulationService = policySimulationService;
    }

    @PostMapping
    @Operation(
            summary = "학급 정책 적용 전 시뮬레이션",
            description = """
                    특정 학급의 최신 economic_metric 데이터를 기준으로
                    선택한 정책의 적용 전/후 경제지표를 시뮬레이션합니다.

                    현재 MVP에서는 AI 정책 효과 예측 API 연동 전까지
                    백엔드의 규칙 기반 계산 로직을 사용합니다.

                    시뮬레이션 결과는 실제 계좌, 거래, 경제지표에 반영하지 않으며
                    DB에도 별도로 저장하지 않습니다.
                    """
    )
    public ResponseEntity<PolicySimulationResponse> simulatePolicy(
            @Parameter(
                    description = "정책 시뮬레이션을 수행할 학급 ID",
                    example = "1"
            )
            @PathVariable Integer classroomId,

            @Valid
            @RequestBody PolicySimulationRequest request
    ) {

        PolicySimulationResponse response =
                policySimulationService.simulate(classroomId, request);

        return ResponseEntity.ok(response);
    }
}
//냥