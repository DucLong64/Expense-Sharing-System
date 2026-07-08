package com.expensesharing.feature.activity.domain.repository;

import com.expensesharing.feature.activity.domain.model.ActivityLog;
import com.expensesharing.feature.activity.domain.model.ActivityType;

import java.util.List;
import java.util.UUID;

public interface ActivityLogRepository {

    ActivityLog save(ActivityLog activityLog);

    List<ActivityLog> findAllByHouseId(UUID houseId, ActivityType activityType);

    List<ActivityLog> findAllByActorUserId(UUID actorUserId, UUID houseId, ActivityType activityType);
}
