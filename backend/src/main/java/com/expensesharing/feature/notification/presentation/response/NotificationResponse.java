package com.expensesharing.feature.notification.presentation.response;

import com.expensesharing.feature.notification.domain.model.Notification;
import com.expensesharing.feature.notification.domain.model.NotificationTargetType;
import com.expensesharing.feature.notification.domain.model.NotificationType;

import java.time.Instant;
import java.util.UUID;

public record NotificationResponse(
        UUID id,
        UUID houseId,
        UUID actorUserId,
        String actorUsername,
        NotificationType type,
        String message,
        NotificationTargetType targetType,
        UUID targetId,
        boolean read,
        Instant readAt,
        Instant createdAt
) {

    public static NotificationResponse from(Notification notification) {
        return new NotificationResponse(
                notification.getId(),
                notification.getHouseId(),
                notification.getActorUserId(),
                null,
                notification.getType(),
                notification.getMessage(),
                notification.getTargetType(),
                notification.getTargetId(),
                notification.isRead(),
                notification.getReadAt(),
                notification.getCreatedAt()
        );
    }
}
