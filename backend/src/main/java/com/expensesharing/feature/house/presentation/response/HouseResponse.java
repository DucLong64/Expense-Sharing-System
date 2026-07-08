package com.expensesharing.feature.house.presentation.response;

import com.expensesharing.feature.house.domain.model.House;

import java.time.Instant;
import java.util.UUID;

public record HouseResponse(UUID id, String name, String description, UUID createdBy, Instant createdAt) {

    public static HouseResponse from(House house) {
        return new HouseResponse(
                house.getId(),
                house.getName(),
                house.getDescription(),
                house.getCreatedBy(),
                house.getCreatedAt()
        );
    }
}
