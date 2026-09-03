package com.example.seed.repository;

import com.example.seed.entity.EconomicEvent;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EconomicEventRepository extends JpaRepository<EconomicEvent, Integer> {
}