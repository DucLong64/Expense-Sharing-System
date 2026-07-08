package com.expensesharing.feature.settlement.application.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record UserBalance(UUID userId, BigDecimal balance) {}
