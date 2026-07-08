package com.expensesharing.feature.dashboard.presentation.response;

import com.expensesharing.feature.dashboard.domain.model.MemberSpending;

import java.math.BigDecimal;
import java.util.UUID;

public record MemberSpendingResponse(UUID userId, String username, BigDecimal amount) {

    public static MemberSpendingResponse from(MemberSpending spending) {
        return new MemberSpendingResponse(spending.userId(), null, spending.amount());
    }
}
