package com.expensesharing.feature.settlement.application.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record SettleDebtCommand(UUID houseId, UUID requesterId, UUID toUserId, BigDecimal amount, String note) {}
