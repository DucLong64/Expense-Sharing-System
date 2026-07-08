package com.expensesharing.feature.settlement.application.usecase;

import com.expensesharing.common.exception.BusinessException;
import com.expensesharing.common.exception.ForbiddenException;
import com.expensesharing.common.exception.NotFoundException;
import com.expensesharing.feature.activity.application.service.ActivityLogService;
import com.expensesharing.feature.activity.domain.model.ActivityTargetType;
import com.expensesharing.feature.activity.domain.model.ActivityType;
import com.expensesharing.feature.house.domain.model.HouseRole;
import com.expensesharing.feature.house.domain.repository.HouseMemberRepository;
import com.expensesharing.feature.house.domain.repository.HouseRepository;
import com.expensesharing.feature.notification.application.service.NotificationService;
import com.expensesharing.feature.notification.domain.model.NotificationTargetType;
import com.expensesharing.feature.notification.domain.model.NotificationType;
import com.expensesharing.feature.settlement.application.dto.ConfirmDebtReceivedCommand;
import com.expensesharing.feature.settlement.application.service.DebtCalculator;
import com.expensesharing.feature.settlement.domain.model.DebtSummary;
import com.expensesharing.feature.settlement.domain.model.Settlement;
import com.expensesharing.feature.settlement.domain.repository.SettlementRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ConfirmDebtReceivedUseCase {

    private final ActivityLogService activityLogService;
    private final NotificationService notificationService;
    private final DebtCalculator debtCalculator;
    private final HouseRepository houseRepository;
    private final HouseMemberRepository houseMemberRepository;
    private final SettlementRepository settlementRepository;

    @Transactional
    public Settlement execute(ConfirmDebtReceivedCommand command) {
        houseRepository.findById(command.houseId())
                .orElseThrow(() -> new NotFoundException("HOUSE_NOT_FOUND", "House not found."));

        var requester = houseMemberRepository.findByHouseIdAndUserId(command.houseId(), command.requesterId())
                .orElseThrow(() -> new ForbiddenException("NOT_A_MEMBER", "You are not a member of this house."));

        if (!requester.hasPermission(HouseRole.MEMBER)) {
            throw new ForbiddenException("INSUFFICIENT_PERMISSION", "Only MEMBER or above can confirm debt received.");
        }

        if (!houseMemberRepository.existsByHouseIdAndUserId(command.houseId(), command.fromUserId())) {
            throw new NotFoundException("MEMBER_NOT_FOUND", "Debtor is not a member of this house.");
        }

        if (command.requesterId().equals(command.fromUserId())) {
            throw new BusinessException("SELF_SETTLEMENT", "You cannot confirm debt with yourself.");
        }

        DebtSummary outstandingDebt = debtCalculator.calculate(command.houseId()).stream()
                .filter(debt -> debt.fromUserId().equals(command.fromUserId())
                        && debt.toUserId().equals(command.requesterId()))
                .findFirst()
                .orElseThrow(() -> new BusinessException(
                        "NO_OUTSTANDING_DEBT", "This member does not owe you."));

        if (command.amount().compareTo(outstandingDebt.amount()) > 0) {
            throw new BusinessException(
                    "AMOUNT_EXCEEDS_DEBT", "Confirmed amount exceeds outstanding debt.");
        }

        Settlement settlement = Settlement.builder()
                .id(UUID.randomUUID())
                .houseId(command.houseId())
                .fromUserId(command.fromUserId())
                .toUserId(command.requesterId())
                .amount(command.amount())
                .note(command.note())
                .settledAt(Instant.now())
                .createdBy(command.requesterId())
                .build();

        Settlement savedSettlement = settlementRepository.save(settlement);

        activityLogService.log(
                command.houseId(),
                command.requesterId(),
                ActivityType.DEBT_SETTLED,
                ActivityTargetType.SETTLEMENT,
                savedSettlement.getId(),
                "Confirmed debt received from a member."
        );

        notificationService.notifyUser(
                command.fromUserId(),
                command.houseId(),
                command.requesterId(),
                NotificationType.DEBT_SETTLED,
                "Có xác nhận thanh toán công nợ mới trong nhóm.",
                NotificationTargetType.SETTLEMENT,
                savedSettlement.getId()
        );

        notificationService.notifyHouseMembersExcept(
                command.houseId(),
                command.requesterId(),
                NotificationType.DEBT_SETTLED,
                "Có xác nhận thanh toán công nợ mới trong nhóm.",
                NotificationTargetType.SETTLEMENT,
                savedSettlement.getId()
        );

        return savedSettlement;
    }
}
