package com.example.seed.repository;

import com.example.seed.entity.AiRecommendation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AiRecommendationRepository
        extends JpaRepository<AiRecommendation, Integer> {

    List<AiRecommendation> findByReportIdOrderByIdAsc(Integer reportId);
}