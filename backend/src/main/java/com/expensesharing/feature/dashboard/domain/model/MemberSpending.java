package com.expensesharing.feature.dashboard.domain.model;

import java.math.BigDecimal;
import java.util.UUID;

public record MemberSpending(UUID userId, BigDecimal amount) {}
