package com.example.seed.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "ai_recommendation")
public class AiRecommendation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "report_id", nullable = false)
    private Integer reportId;

    @Column(name = "recommendation_type")
    private String recommendationType;

    @Column(name = "content")
    private String content;

    @Column(name = "expected_effect")
    private String expectedEffect;

    @Column(name = "is_applied")
    private Boolean isApplied;

    protected AiRecommendation() {
    }

    public Integer getId() {
        return id;
    }

    public Integer getReportId() {
        return reportId;
    }

    public String getRecommendationType() {
        return recommendationType;
    }

    public String getContent() {
        return content;
    }

    public String getExpectedEffect() {
        return expectedEffect;
    }

    public Boolean getIsApplied() {
        return isApplied;
    }
}