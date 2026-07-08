package com.expensesharing.feature.house.presentation.response;

import com.expensesharing.feature.house.domain.model.HouseMember;
import com.expensesharing.feature.house.domain.model.HouseRole;

import java.time.Instant;
import java.util.UUID;

public record HouseMemberResponse(
        UUID id,
        UUID houseId,
        UUID userId,
        String username,
        HouseRole role,
        Instant joinedAt
) {

    public static HouseMemberResponse from(HouseMember member, String username) {
        return new HouseMemberResponse(
                member.getId(),
                member.getHouseId(),
                member.getUserId(),
                username,
                member.getRole(),
                member.getJoinedAt()
        );
    }
}
