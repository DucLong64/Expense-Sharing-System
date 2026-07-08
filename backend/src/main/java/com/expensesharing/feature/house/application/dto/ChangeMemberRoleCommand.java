package com.expensesharing.feature.house.application.dto;

import com.expensesharing.feature.house.domain.model.HouseRole;

import java.util.UUID;

public record ChangeMemberRoleCommand(UUID houseId, UUID requesterId, UUID targetUserId, HouseRole newRole) {}
