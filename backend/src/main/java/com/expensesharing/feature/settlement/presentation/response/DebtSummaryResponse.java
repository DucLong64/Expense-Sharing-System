package com.expensesharing.feature.settlement.presentation.response;

import com.expensesharing.feature.settlement.domain.model.DebtSummary;

import java.math.BigDecimal;
import java.util.UUID;

public record DebtSummaryResponse(
        UUID fromUserId,
        String fromUsername,
        UUID toUserId,
        String toUsername,
        BigDecimal amount
) {

    public static DebtSummaryResponse from(DebtSummary debt) {
        return new DebtSummaryResponse(
                debt.fromUserId(),
                null,
                debt.toUserId(),
                null,
                debt.amount()
        );
    }
}
