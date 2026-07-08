package com.expensesharing.feature.notification.application.usecase;

import com.expensesharing.feature.notification.domain.model.Notification;
import com.expensesharing.feature.notification.domain.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GetMyNotificationsUseCase {

    private final NotificationRepository notificationRepository;

    @Transactional(readOnly = true)
    public List<Notification> execute(UUID recipientUserId, UUID houseId, Boolean unreadOnly) {
        return notificationRepository.findAllByRecipientUserId(recipientUserId, houseId, unreadOnly);
    }
}
