package com.expensesharing.feature.dashboard.presentation.response;

import com.expensesharing.feature.dashboard.domain.model.MonthlySpending;

import java.math.BigDecimal;

public record MonthlySpendingResponse(int year, int month, BigDecimal amount) {

    public static MonthlySpendingResponse from(MonthlySpending spending) {
        return new MonthlySpendingResponse(spending.year(), spending.month(), spending.amount());
    }
}
