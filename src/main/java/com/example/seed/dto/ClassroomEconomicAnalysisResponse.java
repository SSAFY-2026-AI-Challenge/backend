package com.example.seed.dto;

import java.time.LocalDateTime;
import java.util.List;

public class ClassroomEconomicAnalysisResponse {

    private final Integer classroomId;
    private final String economicStatus;
    private final String summary;
    private final List<String> mainFactors;
    private final LocalDateTime generatedAt;

    public ClassroomEconomicAnalysisResponse(
            Integer classroomId,
            String economicStatus,
            String summary,
            List<String> mainFactors,
            LocalDateTime generatedAt
    ) {
        this.classroomId = classroomId;
        this.economicStatus = economicStatus;
        this.summary = summary;
        this.mainFactors = mainFactors;
        this.generatedAt = generatedAt;
    }

    public Integer getClassroomId() {
        return classroomId;
    }

    public String getEconomicStatus() {
        return economicStatus;
    }

    public String getSummary() {
        return summary;
    }

    public List<String> getMainFactors() {
        return mainFactors;
    }

    public LocalDateTime getGeneratedAt() {
        return generatedAt;
    }
}