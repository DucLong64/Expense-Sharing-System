package com.expensesharing.feature.notification.infrastructure.persistence;

import com.expensesharing.feature.notification.domain.model.Notification;
import com.expensesharing.feature.notification.domain.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class NotificationRepositoryImpl implements NotificationRepository {

    private final NotificationJpaRepository jpaRepository;

    @Override
    public Notification save(Notification notification) {
        jpaRepository.save(toEntity(notification));
        return notification;
    }

    @Override
    public Optional<Notification> findByIdAndRecipientUserId(UUID id, UUID recipientUserId) {
        return jpaRepository.findByIdAndRecipientUserId(id, recipientUserId).map(this::toDomain);
    }

    @Override
    public List<Notification> findAllByRecipientUserId(UUID recipientUserId, UUID houseId, Boolean unreadOnly) {
        List<NotificationJpaEntity> entities;
        if (houseId != null && Boolean.TRUE.equals(unreadOnly)) {
            entities = jpaRepository.findAllByRecipientUserIdAndHouseIdAndReadAtIsNullOrderByCreatedAtDesc(
                    recipientUserId, houseId);
        } else if (houseId != null) {
            entities = jpaRepository.findAllByRecipientUserIdAndHouseIdOrderByCreatedAtDesc(recipientUserId, houseId);
        } else if (Boolean.TRUE.equals(unreadOnly)) {
            entities = jpaRepository.findAllByRecipientUserIdAndReadAtIsNullOrderByCreatedAtDesc(recipientUserId);
        } else {
            entities = jpaRepository.findAllByRecipientUserIdOrderByCreatedAtDesc(recipientUserId);
        }
        return entities.stream().map(this::toDomain).toList();
    }

    @Override
    public long countUnreadByRecipientUserId(UUID recipientUserId) {
        return jpaRepository.countByRecipientUserIdAndReadAtIsNull(recipientUserId);
    }

    @Override
    public void markAllAsReadByRecipientUserId(UUID recipientUserId, Instant readAt) {
        jpaRepository.markAllAsRead(recipientUserId, readAt);
    }

    private NotificationJpaEntity toEntity(Notification notification) {
        return NotificationJpaEntity.builder()
                .id(notification.getId())
                .houseId(notification.getHouseId())
                .recipientUserId(notification.getRecipientUserId())
                .actorUserId(notification.getActorUserId())
                .type(notification.getType())
                .message(notification.getMessage())
                .targetType(notification.getTargetType())
                .targetId(notification.getTargetId())
                .readAt(notification.getReadAt())
                .createdAt(notification.getCreatedAt())
                .build();
    }

    private Notification toDomain(NotificationJpaEntity entity) {
        return Notification.builder()
                .id(entity.getId())
                .houseId(entity.getHouseId())
                .recipientUserId(entity.getRecipientUserId())
                .actorUserId(entity.getActorUserId())
                .type(entity.getType())
                .message(entity.getMessage())
                .targetType(entity.getTargetType())
                .targetId(entity.getTargetId())
                .readAt(entity.getReadAt())
                .createdAt(entity.getCreatedAt())
                .build();
    }
}
