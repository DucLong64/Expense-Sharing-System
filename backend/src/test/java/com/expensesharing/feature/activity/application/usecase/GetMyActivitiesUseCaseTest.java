package com.expensesharing.feature.activity.application.usecase;

import com.expensesharing.TestDataFactory;
import com.expensesharing.feature.activity.domain.model.ActivityLog;
import com.expensesharing.feature.activity.domain.model.ActivityTargetType;
import com.expensesharing.feature.activity.domain.model.ActivityType;
import com.expensesharing.feature.activity.domain.repository.ActivityLogRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class GetMyActivitiesUseCaseTest {

    @Mock ActivityLogRepository activityLogRepository;

    @InjectMocks GetMyActivitiesUseCase getMyActivitiesUseCase;

    @Test
    void getMyActivities_withHouseAndTypeFilter_success() {
        ActivityLog activityLog = ActivityLog.builder()
                .id(UUID.randomUUID())
                .houseId(TestDataFactory.HOUSE_ID)
                .actorUserId(TestDataFactory.USER_ID)
                .type(ActivityType.MEMBER_LEFT)
                .targetType(ActivityTargetType.USER)
                .targetId(TestDataFactory.USER_ID)
                .message("Left the house.")
                .createdAt(Instant.now())
                .build();

        given(activityLogRepository.findAllByActorUserId(
                TestDataFactory.USER_ID,
                TestDataFactory.HOUSE_ID,
                ActivityType.MEMBER_LEFT
        )).willReturn(List.of(activityLog));

        List<ActivityLog> result = getMyActivitiesUseCase.execute(
                TestDataFactory.USER_ID,
                TestDataFactory.HOUSE_ID,
                ActivityType.MEMBER_LEFT
        );

        assertThat(result).hasSize(1);
        assertThat(result.getFirst().getMessage()).isEqualTo("Left the house.");
    }
}
