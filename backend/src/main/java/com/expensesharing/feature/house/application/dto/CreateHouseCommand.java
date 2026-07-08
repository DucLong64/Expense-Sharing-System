package com.expensesharing.feature.house.application.dto;

import java.util.UUID;

public record CreateHouseCommand(String name, String description, UUID createdBy) {}
