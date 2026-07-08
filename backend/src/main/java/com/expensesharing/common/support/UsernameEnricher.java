package com.expensesharing.common.support;

import com.expensesharing.common.port.UserProfilePort;
import com.expensesharing.feature.activity.domain.model.ActivityLog;
import com.expensesharing.feature.activity.presentation.response.ActivityLogResponse;
import com.expensesharing.feature.dashboard.domain.model.HouseDashboard;
import com.expensesharing.feature.dashboard.domain.model.MemberSpending;
import com.expensesharing.feature.dashboard.presentation.response.HouseDashboardResponse;
import com.expensesharing.feature.dashboard.presentation.response.MemberSpendingResponse;
import com.expensesharing.feature.dashboard.presentation.response.MonthlySpendingResponse;
import com.expensesharing.feature.expense.application.dto.ExpenseResult;
import com.expensesharing.feature.expense.domain.model.ExpenseParticipant;
import com.expensesharing.feature.expense.presentation.response.ExpenseParticipantResponse;
import com.expensesharing.feature.expense.presentation.response.ExpenseResponse;
import com.expensesharing.feature.notification.domain.model.Notification;
import com.expensesharing.feature.notification.presentation.response.NotificationResponse;
import com.expensesharing.feature.house.domain.model.HouseMember;
import com.expensesharing.feature.house.presentation.response.HouseMemberResponse;
import com.expensesharing.feature.settlement.domain.model.DebtSummary;
import com.expensesharing.feature.settlement.domain.model.Settlement;
import com.expensesharing.feature.settlement.presentation.response.DebtSummaryResponse;
import com.expensesharing.feature.settlement.presentation.response.SettlementResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class UsernameEnricher {

    private final UserProfilePort userProfilePort;

    public HouseMemberResponse toMemberResponse(HouseMember member) {
        return toMemberResponses(List.of(member)).getFirst();
    }

    public List<HouseMemberResponse> toMemberResponses(List<HouseMember> members) {
        Map<UUID, String> usernames = loadUsernames(collectMemberUserIds(members));
        return members.stream()
                .map(member -> HouseMemberResponse.from(member, username(usernames, member.getUserId())))
                .toList();
    }

    public ExpenseResponse toExpenseResponse(ExpenseResult result) {
        return toExpenseResponses(List.of(result)).getFirst();
    }

    public List<ExpenseResponse> toExpenseResponses(List<ExpenseResult> results) {
        Map<UUID, String> usernames = loadUsernames(collectExpenseUserIds(results));
        return results.stream()
                .map(result -> {
                    var expense = result.expense();
                    return new ExpenseResponse(
                            expense.getId(),
                            expense.getHouseId(),
                            expense.getTitle(),
                            expense.getDescription(),
                            expense.getAmount(),
                            expense.getPaidBy(),
                            username(usernames, expense.getPaidBy()),
                            expense.getSplitType(),
                            expense.getExpenseDate(),
                            expense.getNote(),
                            expense.getCreatedBy(),
                            username(usernames, expense.getCreatedBy()),
                            expense.getCreatedAt(),
                            result.participants().stream()
                                    .map(participant -> toParticipantResponse(participant, usernames))
                                    .toList()
                    );
                })
                .toList();
    }

    public List<DebtSummaryResponse> toDebtResponses(List<DebtSummary> debts) {
        Map<UUID, String> usernames = loadUsernames(collectDebtUserIds(debts));
        return debts.stream()
                .map(debt -> new DebtSummaryResponse(
                        debt.fromUserId(),
                        username(usernames, debt.fromUserId()),
                        debt.toUserId(),
                        username(usernames, debt.toUserId()),
                        debt.amount()
                ))
                .toList();
    }

    public SettlementResponse toSettlementResponse(Settlement settlement) {
        return toSettlementResponses(List.of(settlement)).getFirst();
    }

    public List<SettlementResponse> toSettlementResponses(List<Settlement> settlements) {
        Map<UUID, String> usernames = loadUsernames(collectSettlementUserIds(settlements));
        return settlements.stream()
                .map(settlement -> new SettlementResponse(
                        settlement.getId(),
                        settlement.getHouseId(),
                        settlement.getFromUserId(),
                        username(usernames, settlement.getFromUserId()),
                        settlement.getToUserId(),
                        username(usernames, settlement.getToUserId()),
                        settlement.getAmount(),
                        settlement.getNote(),
                        settlement.getSettledAt()
                ))
                .toList();
    }

    public HouseDashboardResponse toDashboardResponse(HouseDashboard dashboard) {
        Set<UUID> userIds = new HashSet<>();
        dashboard.spendingByMember().forEach(spending -> userIds.add(spending.userId()));
        dashboard.currentDebts().forEach(debt -> {
            userIds.add(debt.fromUserId());
            userIds.add(debt.toUserId());
        });
        Map<UUID, String> usernames = loadUsernames(userIds);

        return new HouseDashboardResponse(
                dashboard.totalSpending(),
                dashboard.totalSettled(),
                dashboard.spendingByMonth().stream().map(MonthlySpendingResponse::from).toList(),
                dashboard.spendingByMember().stream()
                        .map(spending -> toMemberSpendingResponse(spending, usernames))
                        .toList(),
                dashboard.currentDebts().stream()
                        .map(debt -> new DebtSummaryResponse(
                                debt.fromUserId(),
                                username(usernames, debt.fromUserId()),
                                debt.toUserId(),
                                username(usernames, debt.toUserId()),
                                debt.amount()
                        ))
                        .toList()
        );
    }

    public List<ActivityLogResponse> toActivityResponses(List<ActivityLog> activities) {
        Set<UUID> userIds = new HashSet<>();
        activities.forEach(activity -> userIds.add(activity.getActorUserId()));
        Map<UUID, String> usernames = loadUsernames(userIds);

        return activities.stream()
                .map(activity -> new ActivityLogResponse(
                        activity.getId(),
                        activity.getHouseId(),
                        activity.getActorUserId(),
                        username(usernames, activity.getActorUserId()),
                        activity.getType(),
                        activity.getTargetType(),
                        activity.getTargetId(),
                        activity.getMessage(),
                        activity.getCreatedAt()
                ))
                .toList();
    }

    public NotificationResponse toNotificationResponse(Notification notification) {
        return toNotificationResponses(List.of(notification)).getFirst();
    }

    public List<NotificationResponse> toNotificationResponses(List<Notification> notifications) {
        Set<UUID> userIds = new HashSet<>();
        notifications.forEach(notification -> userIds.add(notification.getActorUserId()));
        Map<UUID, String> usernames = loadUsernames(userIds);

        return notifications.stream()
                .map(notification -> new NotificationResponse(
                        notification.getId(),
                        notification.getHouseId(),
                        notification.getActorUserId(),
                        username(usernames, notification.getActorUserId()),
                        notification.getType(),
                        notification.getMessage(),
                        notification.getTargetType(),
                        notification.getTargetId(),
                        notification.isRead(),
                        notification.getReadAt(),
                        notification.getCreatedAt()
                ))
                .toList();
    }

    private ExpenseParticipantResponse toParticipantResponse(
            ExpenseParticipant participant,
            Map<UUID, String> usernames
    ) {
        return new ExpenseParticipantResponse(
                participant.getUserId(),
                username(usernames, participant.getUserId()),
                participant.getShareAmount(),
                participant.getSharePercentage()
        );
    }

    private MemberSpendingResponse toMemberSpendingResponse(
            MemberSpending spending,
            Map<UUID, String> usernames
    ) {
        return new MemberSpendingResponse(
                spending.userId(),
                username(usernames, spending.userId()),
                spending.amount()
        );
    }

    private Map<UUID, String> loadUsernames(Collection<UUID> userIds) {
        if (userIds.isEmpty()) {
            return Map.of();
        }
        return userProfilePort.findUsernamesByUserIds(userIds);
    }

    private String username(Map<UUID, String> usernames, UUID userId) {
        if (userId == null) {
            return null;
        }
        return usernames.get(userId);
    }

    private Set<UUID> collectMemberUserIds(List<HouseMember> members) {
        Set<UUID> userIds = new HashSet<>();
        members.forEach(member -> userIds.add(member.getUserId()));
        return userIds;
    }

    private Set<UUID> collectExpenseUserIds(List<ExpenseResult> results) {
        Set<UUID> userIds = new HashSet<>();
        for (ExpenseResult result : results) {
            userIds.add(result.expense().getPaidBy());
            userIds.add(result.expense().getCreatedBy());
            result.participants().forEach(participant -> userIds.add(participant.getUserId()));
        }
        return userIds;
    }

    private Set<UUID> collectDebtUserIds(List<DebtSummary> debts) {
        Set<UUID> userIds = new HashSet<>();
        debts.forEach(debt -> {
            userIds.add(debt.fromUserId());
            userIds.add(debt.toUserId());
        });
        return userIds;
    }

    private Set<UUID> collectSettlementUserIds(List<Settlement> settlements) {
        Set<UUID> userIds = new HashSet<>();
        settlements.forEach(settlement -> {
            userIds.add(settlement.getFromUserId());
            userIds.add(settlement.getToUserId());
        });
        return userIds;
    }
}
