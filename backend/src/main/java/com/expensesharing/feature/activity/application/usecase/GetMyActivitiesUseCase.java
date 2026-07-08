package com.expensesharing.feature.activity.application.usecase;

import com.expensesharing.feature.activity.domain.model.ActivityLog;
import com.expensesharing.feature.activity.domain.model.ActivityType;
import com.expensesharing.feature.activity.domain.repository.ActivityLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GetMyActivitiesUseCase {

    private final ActivityLogRepository activityLogRepository;

    @Transactional(readOnly = true)
    public List<ActivityLog> execute(UUID requesterId, UUID houseId, ActivityType activityType) {
        return activityLogRepository.findAllByActorUserId(requesterId, houseId, activityType);
    }
}
