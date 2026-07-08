package com.expensesharing.feature.house.application.usecase;

import com.expensesharing.common.exception.BusinessException;
import com.expensesharing.common.exception.NotFoundException;
import com.expensesharing.feature.house.domain.repository.HouseRepository;
import com.expensesharing.feature.notification.application.service.NotificationService;
import com.expensesharing.feature.notification.domain.model.NotificationTargetType;
import com.expensesharing.feature.notification.domain.model.NotificationType;
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
public class LeaveHouseUseCase {

    private final ActivityLogService activityLogService;
    private final NotificationService notificationService;
    private final HouseMemberRepository houseMemberRepository;
    private final HouseRepository houseRepository;

    @Transactional
    public void execute(UUID houseId, UUID userId) {
        HouseMember member = houseMemberRepository.findByHouseIdAndUserId(houseId, userId)
                .orElseThrow(() -> new NotFoundException("MEMBER_NOT_FOUND", "You are not a member of this house."));

        if (member.getRole() == HouseRole.OWNER) {
            throw new BusinessException("OWNER_CANNOT_LEAVE", "OWNER cannot leave. Transfer ownership or delete the house.");
        }

        houseMemberRepository.delete(member);

        String houseName = houseRepository.findById(houseId)
                .map(house -> house.getName())
                .orElse("nhóm");

        activityLogService.log(
                houseId,
                userId,
                ActivityType.MEMBER_LEFT,
                ActivityTargetType.USER,
                userId,
                "Left the house."
        );

        notificationService.notifyHouseMembersExcept(
                houseId,
                userId,
                NotificationType.MEMBER_LEFT,
                "Một thành viên đã rời nhóm \"" + houseName + "\".",
                NotificationTargetType.USER,
                userId
        );
    }
}
