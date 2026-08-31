package com.example.seed.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "ai_report")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class AiReport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Column(name = "report_type", nullable = false, length = 45)
    private String reportType;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String summary;

    @Column(columnDefinition = "TEXT")
    private String content;

    @Column(name = "expected_effect", columnDefinition = "TEXT")
    private String expectedEffect;

    @Column(name = "detail_json", columnDefinition = "JSON")
    private String detailJson;

    @Column(name = "generated_at", nullable = false)
    private LocalDate generatedAt;
}
