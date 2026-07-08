package com.expensesharing.feature.house.application.usecase;

import com.expensesharing.common.exception.BusinessException;
import com.expensesharing.common.exception.ForbiddenException;
import com.expensesharing.common.exception.NotFoundException;
import com.expensesharing.feature.activity.application.service.ActivityLogService;
import com.expensesharing.feature.activity.domain.model.ActivityTargetType;
import com.expensesharing.feature.activity.domain.model.ActivityType;
import com.expensesharing.feature.house.application.dto.InviteMemberCommand;
import com.expensesharing.feature.house.application.port.UserLookupPort;
import com.expensesharing.feature.house.domain.model.HouseMember;
import com.expensesharing.feature.house.domain.model.HouseRole;
import com.expensesharing.feature.house.domain.repository.HouseMemberRepository;
import com.expensesharing.feature.house.domain.repository.HouseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class InviteMemberUseCase {

    private final ActivityLogService activityLogService;
    private final HouseRepository houseRepository;
    private final HouseMemberRepository houseMemberRepository;
    private final UserLookupPort userLookupPort;

    @Transactional
    public HouseMember execute(InviteMemberCommand command) {
        houseRepository.findById(command.houseId())
                .orElseThrow(() -> new NotFoundException("HOUSE_NOT_FOUND", "House not found."));

        HouseMember requester = houseMemberRepository.findByHouseIdAndUserId(command.houseId(), command.requesterId())
                .orElseThrow(() -> new ForbiddenException("NOT_A_MEMBER", "You are not a member of this house."));

        if (!requester.hasPermission(HouseRole.ADMIN)) {
            throw new ForbiddenException("INSUFFICIENT_PERMISSION", "Only ADMIN or OWNER can invite members.");
        }

        UUID targetUserId = userLookupPort.findUserIdByIdentifier(command.identifier());

        if (houseMemberRepository.existsByHouseIdAndUserId(command.houseId(), targetUserId)) {
            throw new BusinessException("ALREADY_A_MEMBER", "User is already a member of this house.");
        }

        HouseRole assignedRole = command.role() == HouseRole.OWNER ? HouseRole.ADMIN : command.role();

        HouseMember newMember = HouseMember.builder()
                .id(UUID.randomUUID())
                .houseId(command.houseId())
                .userId(targetUserId)
                .role(assignedRole)
                .joinedAt(Instant.now())
                .build();

        HouseMember savedMember = houseMemberRepository.save(newMember);

        activityLogService.log(
                command.houseId(),
                command.requesterId(),
                ActivityType.MEMBER_INVITED,
                ActivityTargetType.USER,
                targetUserId,
                "Invited a new member to the house."
        );

        return savedMember;
    }
}
