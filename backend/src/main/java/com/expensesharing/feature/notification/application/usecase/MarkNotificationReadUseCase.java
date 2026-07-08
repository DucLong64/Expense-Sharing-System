package com.expensesharing.feature.notification.application.usecase;

import com.expensesharing.common.exception.NotFoundException;
import com.expensesharing.feature.notification.domain.model.Notification;
import com.expensesharing.feature.notification.domain.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MarkNotificationReadUseCase {

    private final NotificationRepository notificationRepository;

    @Transactional
    public Notification execute(UUID notificationId, UUID recipientUserId) {
        Notification notification = notificationRepository.findByIdAndRecipientUserId(notificationId, recipientUserId)
                .orElseThrow(() -> new NotFoundException("NOTIFICATION_NOT_FOUND", "Notification not found."));

        if (!notification.isRead()) {
            notification.markAsRead(Instant.now());
            notificationRepository.save(notification);
        }

        return notification;
    }
}
