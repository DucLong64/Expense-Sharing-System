package com.expensesharing.feature.expense.presentation.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.UUID;

public record ParticipantShareRequest(

        @NotNull(message = "User ID is required")
        UUID userId,

        @DecimalMin(value = "0.0", inclusive = false, message = "Amount must be greater than 0")
        BigDecimal amount,

        @DecimalMin(value = "0.0", inclusive = false, message = "Percentage must be greater than 0")
        BigDecimal percentage
) {}
