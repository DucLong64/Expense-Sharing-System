package com.expensesharing.feature.house.domain.model;

import lombok.Builder;
import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

@Getter
@Builder
public class House {

    private final UUID id;
    private String name;
    private String description;
    private final UUID createdBy;
    private final Instant createdAt;

    public void update(String name, String description) {
        this.name = name;
        this.description = description;
    }
}
