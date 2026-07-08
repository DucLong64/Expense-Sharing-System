package com.expensesharing.feature.dashboard.domain.model;

import com.expensesharing.feature.settlement.domain.model.DebtSummary;

import java.math.BigDecimal;
import java.util.List;

public record HouseDashboard(
        BigDecimal totalSpending,
        BigDecimal totalSettled,
        List<MonthlySpending> spendingByMonth,
        List<MemberSpending> spendingByMember,
        List<DebtSummary> currentDebts
) {}
