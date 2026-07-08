package com.expensesharing.feature.expense.domain.model;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Builder
public class ExpenseParticipant {

    private final UUID id;
    private final UUID expenseId;
    private final UUID userId;
    private BigDecimal shareAmount;
    private BigDecimal sharePercentage;
}
