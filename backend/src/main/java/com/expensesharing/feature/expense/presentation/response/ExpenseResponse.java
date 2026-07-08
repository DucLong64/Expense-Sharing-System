package com.expensesharing.feature.expense.presentation.response;

import com.expensesharing.feature.expense.application.dto.ExpenseResult;
import com.expensesharing.feature.expense.domain.model.SplitType;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public record ExpenseResponse(
        UUID id,
        UUID houseId,
        String title,
        String description,
        BigDecimal amount,
        UUID paidBy,
        String paidByUsername,
        SplitType splitType,
        LocalDate expenseDate,
        String note,
        UUID createdBy,
        String createdByUsername,
        Instant createdAt,
        List<ExpenseParticipantResponse> participants
) {
    public static ExpenseResponse from(ExpenseResult result) {
        return new ExpenseResponse(
                result.expense().getId(),
                result.expense().getHouseId(),
                result.expense().getTitle(),
                result.expense().getDescription(),
                result.expense().getAmount(),
                result.expense().getPaidBy(),
                null,
                result.expense().getSplitType(),
                result.expense().getExpenseDate(),
                result.expense().getNote(),
                result.expense().getCreatedBy(),
                null,
                result.expense().getCreatedAt(),
                result.participants().stream().map(ExpenseParticipantResponse::from).toList()
        );
    }
}
