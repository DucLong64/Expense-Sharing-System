package com.expensesharing.feature.settlement.application.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record ConfirmDebtReceivedCommand(
        UUID houseId,
        UUID requesterId,
        UUID fromUserId,
        BigDecimal amount,
        String note
) {}
