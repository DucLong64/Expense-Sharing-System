package com.expensesharing.feature.activity.application.usecase;

import com.expensesharing.common.exception.ForbiddenException;
import com.expensesharing.common.exception.NotFoundException;
import com.expensesharing.feature.activity.domain.model.ActivityLog;
import com.expensesharing.feature.activity.domain.model.ActivityType;
import com.expensesharing.feature.activity.domain.repository.ActivityLogRepository;
import com.expensesharing.feature.house.domain.model.HouseRole;
import com.expensesharing.feature.house.domain.repository.HouseMemberRepository;
import com.expensesharing.feature.house.domain.repository.HouseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GetHouseActivitiesUseCase {

    private final ActivityLogRepository activityLogRepository;
    private final HouseRepository houseRepository;
    private final HouseMemberRepository houseMemberRepository;

    @Transactional(readOnly = true)
    public List<ActivityLog> execute(UUID houseId, UUID requesterId, ActivityType activityType) {
        houseRepository.findById(houseId)
                .orElseThrow(() -> new NotFoundException("HOUSE_NOT_FOUND", "House not found."));

        var requester = houseMemberRepository.findByHouseIdAndUserId(houseId, requesterId)
                .orElseThrow(() -> new ForbiddenException("NOT_A_MEMBER", "You are not a member of this house."));

        if (!requester.hasPermission(HouseRole.VIEWER)) {
            throw new ForbiddenException("INSUFFICIENT_PERMISSION", "You do not have permission to view activities.");
        }

        return activityLogRepository.findAllByHouseId(houseId, activityType);
    }
}
