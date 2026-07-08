package com.expensesharing.feature.activity.infrastructure.persistence;

import com.expensesharing.feature.activity.domain.model.ActivityLog;
import com.expensesharing.feature.activity.domain.model.ActivityType;
import com.expensesharing.feature.activity.domain.repository.ActivityLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class ActivityLogRepositoryImpl implements ActivityLogRepository {

    private final ActivityLogJpaRepository jpaRepository;

    @Override
    public ActivityLog save(ActivityLog activityLog) {
        jpaRepository.save(toEntity(activityLog));
        return activityLog;
    }

    @Override
    public List<ActivityLog> findAllByHouseId(UUID houseId, ActivityType activityType) {
        List<ActivityLogJpaEntity> entities = activityType == null
                ? jpaRepository.findAllByHouseIdOrderByCreatedAtDesc(houseId)
                : jpaRepository.findAllByHouseIdAndTypeOrderByCreatedAtDesc(houseId, activityType);
        return entities.stream().map(this::toDomain).toList();
    }

    @Override
    public List<ActivityLog> findAllByActorUserId(UUID actorUserId, UUID houseId, ActivityType activityType) {
        List<ActivityLogJpaEntity> entities;

        if (houseId != null && activityType != null) {
            entities = jpaRepository.findAllByActorUserIdAndHouseIdAndTypeOrderByCreatedAtDesc(
                    actorUserId, houseId, activityType);
        } else if (houseId != null) {
            entities = jpaRepository.findAllByActorUserIdAndHouseIdOrderByCreatedAtDesc(actorUserId, houseId);
        } else if (activityType != null) {
            entities = jpaRepository.findAllByActorUserIdAndTypeOrderByCreatedAtDesc(actorUserId, activityType);
        } else {
            entities = jpaRepository.findAllByActorUserIdOrderByCreatedAtDesc(actorUserId);
        }

        return entities.stream().map(this::toDomain).toList();
    }

    private ActivityLogJpaEntity toEntity(ActivityLog activityLog) {
        return ActivityLogJpaEntity.builder()
                .id(activityLog.getId())
                .houseId(activityLog.getHouseId())
                .actorUserId(activityLog.getActorUserId())
                .type(activityLog.getType())
                .targetType(activityLog.getTargetType())
                .targetId(activityLog.getTargetId())
                .message(activityLog.getMessage())
                .createdAt(activityLog.getCreatedAt())
                .build();
    }

    private ActivityLog toDomain(ActivityLogJpaEntity entity) {
        return ActivityLog.builder()
                .id(entity.getId())
                .houseId(entity.getHouseId())
                .actorUserId(entity.getActorUserId())
                .type(entity.getType())
                .targetType(entity.getTargetType())
                .targetId(entity.getTargetId())
                .message(entity.getMessage())
                .createdAt(entity.getCreatedAt())
                .build();
    }
}
