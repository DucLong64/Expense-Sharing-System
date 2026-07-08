package com.expensesharing.feature.settlement.domain.model;

import java.math.BigDecimal;
import java.util.UUID;

public record DebtSummary(UUID fromUserId, UUID toUserId, BigDecimal amount) {}
