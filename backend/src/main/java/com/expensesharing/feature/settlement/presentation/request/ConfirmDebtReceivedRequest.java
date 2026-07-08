package com.expensesharing.feature.settlement.presentation.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.util.UUID;

public record ConfirmDebtReceivedRequest(

        @NotNull(message = "Debtor user ID is required")
        UUID fromUserId,

        @NotNull(message = "Amount is required")
        @DecimalMin(value = "0.01", message = "Amount must be greater than 0")
        BigDecimal amount,

        @Size(max = 500, message = "Note must not exceed 500 characters")
        String note
) {}
