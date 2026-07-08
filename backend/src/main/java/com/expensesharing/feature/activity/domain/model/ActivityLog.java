package com.expensesharing.feature.activity.domain.model;

import lombok.Builder;
import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

@Getter
@Builder
public class ActivityLog {

    private final UUID id;
    private final UUID houseId;
    private final UUID actorUserId;
    private final ActivityType type;
    private final ActivityTargetType targetType;
    private final UUID targetId;
    private final String message;
    private final Instant createdAt;
}
