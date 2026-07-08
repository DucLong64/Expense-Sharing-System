package com.expensesharing.feature.house.application.usecase;

import com.expensesharing.common.exception.BusinessException;
import com.expensesharing.common.exception.ForbiddenException;
import com.expensesharing.common.exception.NotFoundException;
import com.expensesharing.feature.activity.application.service.ActivityLogService;
import com.expensesharing.feature.activity.domain.model.ActivityTargetType;
import com.expensesharing.feature.activity.domain.model.ActivityType;
import com.expensesharing.feature.house.domain.model.HouseMember;
import com.expensesharing.feature.house.domain.model.HouseRole;
import com.expensesharing.feature.house.domain.repository.HouseMemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RemoveMemberUseCase {

    private final ActivityLogService activityLogService;
    private final HouseMemberRepository houseMemberRepository;

    @Transactional
    public void execute(UUID houseId, UUID requesterId, UUID targetUserId) {
        HouseMember requester = houseMemberRepository.findByHouseIdAndUserId(houseId, requesterId)
                .orElseThrow(() -> new ForbiddenException("NOT_A_MEMBER", "You are not a member of this house."));

        if (!requester.hasPermission(HouseRole.ADMIN)) {
            throw new ForbiddenException("INSUFFICIENT_PERMISSION", "Only ADMIN or OWNER can remove members.");
        }

        HouseMember target = houseMemberRepository.findByHouseIdAndUserId(houseId, targetUserId)
                .orElseThrow(() -> new NotFoundException("MEMBER_NOT_FOUND", "Target member not found."));

        if (target.getRole() == HouseRole.OWNER) {
            throw new BusinessException("CANNOT_REMOVE_OWNER", "Cannot remove the OWNER from the house.");
        }

        houseMemberRepository.delete(target);

        activityLogService.log(
                houseId,
                requesterId,
                ActivityType.MEMBER_REMOVED,
                ActivityTargetType.USER,
                targetUserId,
                "Removed a member from the house."
        );
    }
}
