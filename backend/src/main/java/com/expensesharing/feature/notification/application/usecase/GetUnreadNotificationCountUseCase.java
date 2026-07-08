package com.expensesharing.feature.notification.application.usecase;

import com.expensesharing.feature.notification.domain.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GetUnreadNotificationCountUseCase {

    private final NotificationRepository notificationRepository;

    @Transactional(readOnly = true)
    public long execute(UUID recipientUserId) {
        return notificationRepository.countUnreadByRecipientUserId(recipientUserId);
    }
}
