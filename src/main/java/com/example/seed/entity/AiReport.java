package com.example.seed.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

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

    @Column(name = "credit_score", nullable = false)
    private Integer creditScore;

    @Column(nullable = false, length = 512)
    private String summary;

    @Column(nullable = false, length = 512)
    private String content;

    @Column(name = "expected_effect", nullable = false, length = 512)
    private String expectedEffect;

    @Column(nullable = false, columnDefinition = "json")
    private String features;

    @Column(name = "generated_at", nullable = false)
    private LocalDateTime generatedAt;
}
