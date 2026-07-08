package com.expensesharing.feature.activity.presentation.response;

import com.expensesharing.feature.activity.domain.model.ActivityLog;
import com.expensesharing.feature.activity.domain.model.ActivityTargetType;
import com.expensesharing.feature.activity.domain.model.ActivityType;

import java.time.Instant;
import java.util.UUID;

public record ActivityLogResponse(
        UUID id,
        UUID houseId,
        UUID actorUserId,
        String actorUsername,
        ActivityType type,
        ActivityTargetType targetType,
        UUID targetId,
        String message,
        Instant createdAt
) {

    public static ActivityLogResponse from(ActivityLog activityLog) {
        return new ActivityLogResponse(
                activityLog.getId(),
                activityLog.getHouseId(),
                activityLog.getActorUserId(),
                null,
                activityLog.getType(),
                activityLog.getTargetType(),
                activityLog.getTargetId(),
                activityLog.getMessage(),
                activityLog.getCreatedAt()
        );
    }
}
