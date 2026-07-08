package com.expensesharing.feature.notification.domain.model;

import lombok.Builder;
import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

@Getter
@Builder
public class Notification {

    private final UUID id;
    private final UUID houseId;
    private final UUID recipientUserId;
    private final UUID actorUserId;
    private final NotificationType type;
    private final String message;
    private final NotificationTargetType targetType;
    private final UUID targetId;
    private Instant readAt;
    private final Instant createdAt;

    public void markAsRead(Instant readAt) {
        this.readAt = readAt;
    }

    public boolean isRead() {
        return readAt != null;
    }
}
