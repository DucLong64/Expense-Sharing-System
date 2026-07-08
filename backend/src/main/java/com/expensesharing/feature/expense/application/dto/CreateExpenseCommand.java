package com.expensesharing.feature.expense.application.dto;

import com.expensesharing.feature.expense.domain.model.SplitType;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public record CreateExpenseCommand(
        UUID houseId,
        UUID requesterId,
        String title,
        String description,
        BigDecimal amount,
        UUID paidBy,
        SplitType splitType,
        LocalDate expenseDate,
        String note,
        List<ParticipantShare> participants
) {}
