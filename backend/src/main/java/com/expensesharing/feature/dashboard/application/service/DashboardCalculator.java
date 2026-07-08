package com.expensesharing.feature.dashboard.application.service;

import com.expensesharing.feature.dashboard.domain.model.HouseDashboard;
import com.expensesharing.feature.dashboard.domain.model.MemberSpending;
import com.expensesharing.feature.dashboard.domain.model.MonthlySpending;
import com.expensesharing.feature.expense.domain.model.Expense;
import com.expensesharing.feature.expense.domain.model.ExpenseParticipant;
import com.expensesharing.feature.expense.domain.repository.ExpenseParticipantRepository;
import com.expensesharing.feature.expense.domain.repository.ExpenseRepository;
import com.expensesharing.feature.settlement.application.service.DebtCalculator;
import com.expensesharing.feature.settlement.domain.model.Settlement;
import com.expensesharing.feature.settlement.domain.repository.SettlementRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DashboardCalculator {

    private final ExpenseRepository expenseRepository;
    private final ExpenseParticipantRepository participantRepository;
    private final SettlementRepository settlementRepository;
    private final DebtCalculator debtCalculator;

    public HouseDashboard calculate(UUID houseId) {
        List<Expense> expenses = expenseRepository.findAllByHouseId(houseId);

        BigDecimal totalSpending = expenses.stream()
                .map(Expense::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Map<YearMonth, BigDecimal> monthlyTotals = new HashMap<>();
        Map<UUID, BigDecimal> memberTotals = new HashMap<>();

        for (Expense expense : expenses) {
            YearMonth yearMonth = YearMonth.from(expense.getExpenseDate());
            monthlyTotals.merge(yearMonth, expense.getAmount(), BigDecimal::add);

            for (ExpenseParticipant participant : participantRepository.findAllByExpenseId(expense.getId())) {
                memberTotals.merge(participant.getUserId(), participant.getShareAmount(), BigDecimal::add);
            }
        }

        List<MonthlySpending> spendingByMonth = monthlyTotals.entrySet().stream()
                .sorted(Map.Entry.<YearMonth, BigDecimal>comparingByKey(Comparator.reverseOrder()))
                .map(entry -> new MonthlySpending(
                        entry.getKey().getYear(),
                        entry.getKey().getMonthValue(),
                        entry.getValue()))
                .toList();

        List<MemberSpending> spendingByMember = memberTotals.entrySet().stream()
                .sorted(Map.Entry.<UUID, BigDecimal>comparingByValue(Comparator.reverseOrder()))
                .map(entry -> new MemberSpending(entry.getKey(), entry.getValue()))
                .toList();

        BigDecimal totalSettled = settlementRepository.findAllByHouseId(houseId).stream()
                .map(Settlement::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return new HouseDashboard(
                totalSpending,
                totalSettled,
                spendingByMonth,
                spendingByMember,
                new ArrayList<>(debtCalculator.calculate(houseId))
        );
    }
}
