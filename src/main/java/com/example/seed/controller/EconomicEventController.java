package com.example.seed.controller;

import com.example.seed.dto.EconomicEventResponse;
import com.example.seed.service.EconomicEventService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/economic-events")
@Tag(
        name = "Economic Event",
        description = "경제 이벤트 조회 API"
)
public class EconomicEventController {

    private final EconomicEventService economicEventService;

    public EconomicEventController(
            EconomicEventService economicEventService
    ) {
        this.economicEventService = economicEventService;
    }

    @GetMapping("/random")
    @Operation(
            summary = "랜덤 경제 이벤트 조회",
            description = """
                    economic_event 마스터 데이터에서
                    경제 이벤트 1개를 무작위로 선택하여 반환합니다.

                    현재 MVP에서는 이벤트를 조회하여 보여주기만 하며,
                    계좌, 거래, 경제지표 등의 실제 데이터는 변경하지 않습니다.
                    """
    )
    public ResponseEntity<EconomicEventResponse> getRandomEvent() {

        EconomicEventResponse response =
                economicEventService.getRandomEvent();

        return ResponseEntity.ok(response);
    }
}