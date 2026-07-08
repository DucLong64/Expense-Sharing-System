package com.expensesharing.feature.house.application.dto;

import java.util.UUID;

public record UpdateHouseCommand(UUID houseId, UUID requesterId, String name, String description) {}
