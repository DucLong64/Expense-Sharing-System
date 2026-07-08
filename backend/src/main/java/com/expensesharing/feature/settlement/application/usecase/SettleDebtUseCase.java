package com.expensesharing.feature.settlement.application.usecase;

import com.expensesharing.common.exception.ForbiddenException;
import com.expensesharing.common.exception.NotFoundException;
import com.expensesharing.feature.activity.application.service.ActivityLogService;
import com.expensesharing.feature.activity.domain.model.ActivityTargetType;
import com.expensesharing.feature.activity.domain.model.ActivityType;
import com.expensesharing.feature.house.domain.model.HouseRole;
import com.expensesharing.feature.house.domain.repository.HouseMemberRepository;
import com.expensesharing.feature.house.domain.repository.HouseRepository;
import com.expensesharing.feature.settlement.application.dto.SettleDebtCommand;
import com.expensesharing.feature.settlement.domain.model.Settlement;
import com.expensesharing.feature.settlement.domain.repository.SettlementRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SettleDebtUseCase {

    private final ActivityLogService activityLogService;
    private final HouseRepository houseRepository;
    private final HouseMemberRepository houseMemberRepository;
    private final SettlementRepository settlementRepository;

    @Transactional
    public Settlement execute(SettleDebtCommand command) {
        houseRepository.findById(command.houseId())
                .orElseThrow(() -> new NotFoundException("HOUSE_NOT_FOUND", "House not found."));

        var requester = houseMemberRepository.findByHouseIdAndUserId(command.houseId(), command.requesterId())
                .orElseThrow(() -> new ForbiddenException("NOT_A_MEMBER", "You are not a member of this house."));

        if (!requester.hasPermission(HouseRole.MEMBER)) {
            throw new ForbiddenException("INSUFFICIENT_PERMISSION", "Only MEMBER or above can settle debts.");
        }

        if (!houseMemberRepository.existsByHouseIdAndUserId(command.houseId(), command.toUserId())) {
            throw new NotFoundException("MEMBER_NOT_FOUND", "Target user is not a member of this house.");
        }

        Settlement settlement = Settlement.builder()
                .id(UUID.randomUUID())
                .houseId(command.houseId())
                .fromUserId(command.requesterId())
                .toUserId(command.toUserId())
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
                "Recorded a debt settlement."
        );

        return savedSettlement;
    }
}
