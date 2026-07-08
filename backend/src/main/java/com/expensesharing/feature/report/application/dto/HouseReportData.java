package com.expensesharing.feature.report.application.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

public record HouseReportData(
        String houseName,
        String houseDescription,
        Instant generatedAt,
        BigDecimal totalSpending,
        BigDecimal totalSettled,
        List<MonthlySpendingRow> spendingByMonth,
        List<MemberSpendingRow> spendingByMember,
        List<DebtRow> currentDebts,
        List<ExpenseRow> expenses,
        List<SettlementRow> settlements
) {

    public record MonthlySpendingRow(int year, int month, BigDecimal amount) {}

    public record MemberSpendingRow(String username, BigDecimal amount) {}

    public record DebtRow(String fromUsername, String toUsername, BigDecimal amount) {}

    public record ExpenseRow(
            String title,
            LocalDate expenseDate,
            BigDecimal amount,
            String paidByUsername,
            String splitType,
            String participantsSummary,
            String note
    ) {}

    public record SettlementRow(
            Instant settledAt,
            String fromUsername,
            String toUsername,
            BigDecimal amount,
            String note
    ) {}
}
