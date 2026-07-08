package com.expensesharing.feature.notification.domain.repository;

import com.expensesharing.feature.notification.domain.model.Notification;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface NotificationRepository {

    Notification save(Notification notification);

    Optional<Notification> findByIdAndRecipientUserId(UUID id, UUID recipientUserId);

    List<Notification> findAllByRecipientUserId(UUID recipientUserId, UUID houseId, Boolean unreadOnly);

    long countUnreadByRecipientUserId(UUID recipientUserId);

    void markAllAsReadByRecipientUserId(UUID recipientUserId, java.time.Instant readAt);
}
