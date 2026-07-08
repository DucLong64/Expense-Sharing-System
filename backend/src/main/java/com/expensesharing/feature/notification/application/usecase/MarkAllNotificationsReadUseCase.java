package com.expensesharing.feature.notification.application.usecase;

import com.expensesharing.feature.notification.domain.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MarkAllNotificationsReadUseCase {

    private final NotificationRepository notificationRepository;

    @Transactional
    public void execute(UUID recipientUserId) {
        notificationRepository.markAllAsReadByRecipientUserId(recipientUserId, Instant.now());
    }
}
