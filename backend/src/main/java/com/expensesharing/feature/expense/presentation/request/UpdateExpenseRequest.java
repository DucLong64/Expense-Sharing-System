package com.expensesharing.feature.expense.presentation.request;

import com.expensesharing.feature.expense.domain.model.SplitType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record UpdateExpenseRequest(

        @NotBlank(message = "Title is required")
        @Size(max = 200, message = "Title must not exceed 200 characters")
        String title,

        @Size(max = 500, message = "Description must not exceed 500 characters")
        String description,

        @NotNull(message = "Amount is required")
        @DecimalMin(value = "0.01", message = "Amount must be greater than 0")
        BigDecimal amount,

        @NotNull(message = "Split type is required")
        SplitType splitType,

        @NotNull(message = "Expense date is required")
        LocalDate expenseDate,

        @Size(max = 500, message = "Note must not exceed 500 characters")
        String note,

        @NotEmpty(message = "Participants list must not be empty")
        @Valid
        List<ParticipantShareRequest> participants
) {}
