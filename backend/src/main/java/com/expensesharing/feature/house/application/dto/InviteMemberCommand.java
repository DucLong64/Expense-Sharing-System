package com.expensesharing.feature.house.application.dto;

import com.expensesharing.feature.house.domain.model.HouseRole;

import java.util.UUID;

public record InviteMemberCommand(UUID houseId, UUID requesterId, String identifier, HouseRole role) {}
