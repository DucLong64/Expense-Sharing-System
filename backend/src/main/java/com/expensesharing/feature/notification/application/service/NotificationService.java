package com.expensesharing.feature.notification.application.service;

import com.expensesharing.feature.house.domain.model.HouseMember;
import com.expensesharing.feature.house.domain.repository.HouseMemberRepository;
import com.expensesharing.feature.notification.domain.model.Notification;
import com.expensesharing.feature.notification.domain.model.NotificationTargetType;
import com.expensesharing.feature.notification.domain.model.NotificationType;
import com.expensesharing.feature.notification.domain.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final HouseMemberRepository houseMemberRepository;

    public void notifyHouseMembersExcept(
            UUID houseId,
            UUID actorUserId,
            NotificationType type,
            String message,
            NotificationTargetType targetType,
            UUID targetId
    ) {
        for (HouseMember member : houseMemberRepository.findAllByHouseId(houseId)) {
            if (!member.getUserId().equals(actorUserId)) {
                saveNotification(member.getUserId(), houseId, actorUserId, type, message, targetType, targetId);
            }
        }
    }

    public void notifyUser(
            UUID recipientUserId,
            UUID houseId,
            UUID actorUserId,
            NotificationType type,
            String message,
            NotificationTargetType targetType,
            UUID targetId
    ) {
        if (recipientUserId.equals(actorUserId)) {
            return;
        }
        saveNotification(recipientUserId, houseId, actorUserId, type, message, targetType, targetId);
    }

    private void saveNotification(
            UUID recipientUserId,
            UUID houseId,
            UUID actorUserId,
            NotificationType type,
            String message,
            NotificationTargetType targetType,
            UUID targetId
    ) {
        Notification notification = Notification.builder()
                .id(UUID.randomUUID())
                .houseId(houseId)
                .recipientUserId(recipientUserId)
                .actorUserId(actorUserId)
                .type(type)
                .message(message)
                .targetType(targetType)
                .targetId(targetId)
                .createdAt(Instant.now())
                .build();

        notificationRepository.save(notification);
    }
}
