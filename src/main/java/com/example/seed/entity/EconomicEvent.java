package com.example.seed.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "economic_event")
public class EconomicEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 45)
    private String name;

    @Column(nullable = false, length = 45)
    private String level;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "`trigger`", nullable = false, length = 45)
    private String trigger;

    @Column(length = 45)
    private String target;

    @Column(length = 45)
    private String effect;

    private Integer value;

    protected EconomicEvent() {
    }

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getLevel() {
        return level;
    }

    public String getDescription() {
        return description;
    }

    public String getTrigger() {
        return trigger;
    }

    public String getTarget() {
        return target;
    }

    public String getEffect() {
        return effect;
    }

    public Integer getValue() {
        return value;
    }
}