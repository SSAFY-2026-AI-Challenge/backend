package com.example.seed.dto;

public class EconomicEventResponse {

    private final Integer id;
    private final String name;
    private final String level;
    private final String description;
    private final String trigger;
    private final String target;
    private final String effect;
    private final Integer value;

    public EconomicEventResponse(
            Integer id,
            String name,
            String level,
            String description,
            String trigger,
            String target,
            String effect,
            Integer value
    ) {
        this.id = id;
        this.name = name;
        this.level = level;
        this.description = description;
        this.trigger = trigger;
        this.target = target;
        this.effect = effect;
        this.value = value;
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