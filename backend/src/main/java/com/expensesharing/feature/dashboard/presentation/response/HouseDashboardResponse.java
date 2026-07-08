package com.expensesharing.feature.dashboard.presentation.response;

import com.expensesharing.feature.dashboard.domain.model.HouseDashboard;
import com.expensesharing.feature.settlement.presentation.response.DebtSummaryResponse;

import java.math.BigDecimal;
import java.util.List;

public record HouseDashboardResponse(
        BigDecimal totalSpending,
        BigDecimal totalSettled,
        List<MonthlySpendingResponse> spendingByMonth,
        List<MemberSpendingResponse> spendingByMember,
        List<DebtSummaryResponse> currentDebts
) {

    public static HouseDashboardResponse from(HouseDashboard dashboard) {
        return new HouseDashboardResponse(
                dashboard.totalSpending(),
                dashboard.totalSettled(),
                dashboard.spendingByMonth().stream().map(MonthlySpendingResponse::from).toList(),
                dashboard.spendingByMember().stream().map(MemberSpendingResponse::from).toList(),
                dashboard.currentDebts().stream().map(DebtSummaryResponse::from).toList()
        );
    }
}
