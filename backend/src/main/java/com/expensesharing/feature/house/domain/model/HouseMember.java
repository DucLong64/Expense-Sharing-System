package com.expensesharing.feature.house.domain.model;

import lombok.Builder;
import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

@Getter
@Builder
public class HouseMember {

    private final UUID id;
    private final UUID houseId;
    private final UUID userId;
    private HouseRole role;
    private final Instant joinedAt;

    public void changeRole(HouseRole newRole) {
        this.role = newRole;
    }

    public boolean hasPermission(HouseRole required) {
        return this.role.isAtLeast(required);
    }
}
