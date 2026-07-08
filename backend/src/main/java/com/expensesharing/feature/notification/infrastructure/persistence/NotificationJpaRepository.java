package com.expensesharing.feature.notification.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface NotificationJpaRepository extends JpaRepository<NotificationJpaEntity, UUID> {

    Optional<NotificationJpaEntity> findByIdAndRecipientUserId(UUID id, UUID recipientUserId);

    List<NotificationJpaEntity> findAllByRecipientUserIdOrderByCreatedAtDesc(UUID recipientUserId);

    List<NotificationJpaEntity> findAllByRecipientUserIdAndHouseIdOrderByCreatedAtDesc(
            UUID recipientUserId,
            UUID houseId
    );

    List<NotificationJpaEntity> findAllByRecipientUserIdAndReadAtIsNullOrderByCreatedAtDesc(UUID recipientUserId);

    List<NotificationJpaEntity> findAllByRecipientUserIdAndHouseIdAndReadAtIsNullOrderByCreatedAtDesc(
            UUID recipientUserId,
            UUID houseId
    );

    long countByRecipientUserIdAndReadAtIsNull(UUID recipientUserId);

    @Modifying
    @Query("""
            UPDATE NotificationJpaEntity notification
            SET notification.readAt = :readAt
            WHERE notification.recipientUserId = :recipientUserId
              AND notification.readAt IS NULL
            """)
    void markAllAsRead(@Param("recipientUserId") UUID recipientUserId, @Param("readAt") Instant readAt);
}
