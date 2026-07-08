package com.expensesharing.feature.report.application.service;

import com.expensesharing.common.port.UserProfilePort;
import com.expensesharing.feature.dashboard.application.usecase.GetHouseDashboardUseCase;
import com.expensesharing.feature.dashboard.domain.model.HouseDashboard;
import com.expensesharing.feature.expense.application.dto.ExpenseResult;
import com.expensesharing.feature.expense.application.usecase.GetExpenseUseCase;
import com.expensesharing.feature.expense.domain.model.ExpenseParticipant;
import com.expensesharing.feature.expense.domain.model.SplitType;
import com.expensesharing.feature.house.application.usecase.GetHouseUseCase;
import com.expensesharing.feature.house.domain.model.House;
import com.expensesharing.feature.report.application.dto.HouseReportData;
import com.expensesharing.feature.settlement.application.usecase.GetSettlementHistoryUseCase;
import com.expensesharing.feature.settlement.domain.model.Settlement;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class HouseReportAssembler {

    private final GetHouseUseCase getHouseUseCase;
    private final GetHouseDashboardUseCase getHouseDashboardUseCase;
    private final GetExpenseUseCase getExpenseUseCase;
    private final GetSettlementHistoryUseCase getSettlementHistoryUseCase;
    private final UserProfilePort userProfilePort;

    public HouseReportData assemble(UUID houseId, UUID requesterId) {
        House house = getHouseUseCase.getById(houseId, requesterId);
        HouseDashboard dashboard = getHouseDashboardUseCase.execute(houseId, requesterId);
        List<ExpenseResult> expenses = getExpenseUseCase.getAllByHouseId(houseId, requesterId);
        List<Settlement> settlements = getSettlementHistoryUseCase.execute(houseId, requesterId);

        Map<UUID, String> usernames = loadUsernames(dashboard, expenses, settlements);

        return new HouseReportData(
                house.getName(),
                house.getDescription(),
                Instant.now(),
                dashboard.totalSpending(),
                dashboard.totalSettled(),
                dashboard.spendingByMonth().stream()
                        .map(item -> new HouseReportData.MonthlySpendingRow(item.year(), item.month(), item.amount()))
                        .toList(),
                dashboard.spendingByMember().stream()
                        .map(item -> new HouseReportData.MemberSpendingRow(
                                username(usernames, item.userId()), item.amount()))
                        .toList(),
                dashboard.currentDebts().stream()
                        .map(debt -> new HouseReportData.DebtRow(
                                username(usernames, debt.fromUserId()),
                                username(usernames, debt.toUserId()),
                                debt.amount()))
                        .toList(),
                expenses.stream()
                        .map(result -> toExpenseRow(result, usernames))
                        .toList(),
                settlements.stream()
                        .map(settlement -> new HouseReportData.SettlementRow(
                                settlement.getSettledAt(),
                                username(usernames, settlement.getFromUserId()),
                                username(usernames, settlement.getToUserId()),
                                settlement.getAmount(),
                                settlement.getNote()))
                        .toList()
        );
    }

    private HouseReportData.ExpenseRow toExpenseRow(ExpenseResult result, Map<UUID, String> usernames) {
        var expense = result.expense();
        return new HouseReportData.ExpenseRow(
                expense.getTitle(),
                expense.getExpenseDate(),
                expense.getAmount(),
                username(usernames, expense.getPaidBy()),
                splitTypeLabel(expense.getSplitType()),
                buildParticipantsSummary(result.participants(), usernames),
                expense.getNote()
        );
    }

    private String buildParticipantsSummary(List<ExpenseParticipant> participants, Map<UUID, String> usernames) {
        return participants.stream()
                .map(participant -> {
                    String name = username(usernames, participant.getUserId());
                    if (participant.getSharePercentage() != null) {
                        return name + ": " + participant.getSharePercentage() + "%";
                    }
                    return name + ": " + formatAmount(participant.getShareAmount());
                })
                .collect(Collectors.joining(", "));
    }

    private Map<UUID, String> loadUsernames(
            HouseDashboard dashboard,
            List<ExpenseResult> expenses,
            List<Settlement> settlements
    ) {
        Set<UUID> userIds = new HashSet<>();
        dashboard.spendingByMember().forEach(item -> userIds.add(item.userId()));
        dashboard.currentDebts().forEach(debt -> {
            userIds.add(debt.fromUserId());
            userIds.add(debt.toUserId());
        });
        expenses.forEach(result -> {
            userIds.add(result.expense().getPaidBy());
            result.participants().forEach(participant -> userIds.add(participant.getUserId()));
        });
        settlements.forEach(settlement -> {
            userIds.add(settlement.getFromUserId());
            userIds.add(settlement.getToUserId());
        });
        return userProfilePort.findUsernamesByUserIds(userIds);
    }

    private String username(Map<UUID, String> usernames, UUID userId) {
        if (userId == null) {
            return "—";
        }
        String value = usernames.get(userId);
        return value == null || value.isBlank() ? userId.toString().substring(0, 8) : value;
    }

    private String splitTypeLabel(SplitType splitType) {
        return switch (splitType) {
            case EQUAL -> "Chia đều";
            case FIXED -> "Số tiền cố định";
            case PERCENTAGE -> "Phần trăm";
        };
    }

    private String formatAmount(BigDecimal amount) {
        if (amount == null) {
            return "0";
        }
        return amount.stripTrailingZeros().toPlainString();
    }
}
