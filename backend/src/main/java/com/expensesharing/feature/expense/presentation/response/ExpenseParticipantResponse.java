package com.expensesharing.feature.expense.presentation.response;

import com.expensesharing.feature.expense.domain.model.ExpenseParticipant;

import java.math.BigDecimal;
import java.util.UUID;

public record ExpenseParticipantResponse(
        UUID userId,
        String username,
        BigDecimal shareAmount,
        BigDecimal sharePercentage
) {

    public static ExpenseParticipantResponse from(ExpenseParticipant participant) {
        return new ExpenseParticipantResponse(
                participant.getUserId(),
                null,
                participant.getShareAmount(),
                participant.getSharePercentage()
        );
    }
}
