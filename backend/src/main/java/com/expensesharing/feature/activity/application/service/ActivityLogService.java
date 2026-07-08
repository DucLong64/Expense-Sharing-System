package com.expensesharing.feature.activity.application.service;

import com.expensesharing.feature.activity.domain.model.ActivityLog;
import com.expensesharing.feature.activity.domain.model.ActivityTargetType;
import com.expensesharing.feature.activity.domain.model.ActivityType;
import com.expensesharing.feature.activity.domain.repository.ActivityLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ActivityLogService {

    private final ActivityLogRepository activityLogRepository;

    public void log(UUID houseId,
                    UUID actorUserId,
                    ActivityType type,
                    ActivityTargetType targetType,
                    UUID targetId,
                    String message) {
        ActivityLog activityLog = ActivityLog.builder()
                .id(UUID.randomUUID())
                .houseId(houseId)
                .actorUserId(actorUserId)
                .type(type)
                .targetType(targetType)
                .targetId(targetId)
                .message(message)
                .createdAt(Instant.now())
                .build();

        activityLogRepository.save(activityLog);
    }
}
