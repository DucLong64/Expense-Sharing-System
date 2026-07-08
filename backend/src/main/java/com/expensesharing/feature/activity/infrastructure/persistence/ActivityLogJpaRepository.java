package com.expensesharing.feature.activity.infrastructure.persistence;

import com.expensesharing.feature.activity.domain.model.ActivityType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ActivityLogJpaRepository extends JpaRepository<ActivityLogJpaEntity, UUID> {

    List<ActivityLogJpaEntity> findAllByHouseIdOrderByCreatedAtDesc(UUID houseId);

    List<ActivityLogJpaEntity> findAllByHouseIdAndTypeOrderByCreatedAtDesc(UUID houseId, ActivityType type);

    List<ActivityLogJpaEntity> findAllByActorUserIdOrderByCreatedAtDesc(UUID actorUserId);

    List<ActivityLogJpaEntity> findAllByActorUserIdAndTypeOrderByCreatedAtDesc(UUID actorUserId, ActivityType type);

    List<ActivityLogJpaEntity> findAllByActorUserIdAndHouseIdOrderByCreatedAtDesc(UUID actorUserId, UUID houseId);

    List<ActivityLogJpaEntity> findAllByActorUserIdAndHouseIdAndTypeOrderByCreatedAtDesc(
            UUID actorUserId,
            UUID houseId,
            ActivityType type
    );
}
