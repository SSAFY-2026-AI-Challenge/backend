package com.example.seed.service;

import com.example.seed.dto.EconomicEventResponse;
import com.example.seed.entity.EconomicEvent;
import com.example.seed.exception.NotFoundException;
import com.example.seed.repository.EconomicEventRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class EconomicEventService {

    private final EconomicEventRepository economicEventRepository;

    public EconomicEventService(
            EconomicEventRepository economicEventRepository
    ) {
        this.economicEventRepository = economicEventRepository;
    }

    public EconomicEventResponse getRandomEvent() {

        // 1. 전체 경제 이벤트 조회
        List<EconomicEvent> events = economicEventRepository.findAll();

        // 2. 경제 이벤트가 하나도 없는 경우
        if (events.isEmpty()) {
            throw new NotFoundException("경제 이벤트를 찾을 수 없습니다.");
        }

        // 3. 전체 이벤트 중 랜덤 인덱스 선택
        int randomIndex =
                ThreadLocalRandom.current().nextInt(events.size());

        EconomicEvent event = events.get(randomIndex);

        // 4. Entity → Response DTO 변환
        return new EconomicEventResponse(
                event.getId(),
                event.getName(),
                event.getLevel(),
                event.getDescription(),
                event.getTrigger(),
                event.getTarget(),
                event.getEffect(),
                event.getValue()
        );
    }
}