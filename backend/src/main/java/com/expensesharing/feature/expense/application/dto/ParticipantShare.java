package com.expensesharing.feature.expense.application.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record ParticipantShare(UUID userId, BigDecimal amount, BigDecimal percentage) {}
