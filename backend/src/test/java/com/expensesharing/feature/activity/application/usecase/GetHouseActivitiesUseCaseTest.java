package com.expensesharing.feature.activity.application.usecase;

import com.expensesharing.TestDataFactory;
import com.expensesharing.common.exception.ForbiddenException;
import com.expensesharing.feature.activity.domain.model.ActivityLog;
import com.expensesharing.feature.activity.domain.model.ActivityTargetType;
import com.expensesharing.feature.activity.domain.model.ActivityType;
import com.expensesharing.feature.activity.domain.repository.ActivityLogRepository;
import com.expensesharing.feature.house.domain.model.HouseRole;
import com.expensesharing.feature.house.domain.repository.HouseMemberRepository;
import com.expensesharing.feature.house.domain.repository.HouseRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class GetHouseActivitiesUseCaseTest {

    @Mock ActivityLogRepository activityLogRepository;
    @Mock HouseRepository houseRepository;
    @Mock HouseMemberRepository houseMemberRepository;

    @InjectMocks GetHouseActivitiesUseCase getHouseActivitiesUseCase;

    @Test
    void getActivities_asViewer_success() {
        ActivityLog activityLog = ActivityLog.builder()
                .id(UUID.randomUUID())
                .houseId(TestDataFactory.HOUSE_ID)
                .actorUserId(TestDataFactory.USER_ID)
                .type(ActivityType.EXPENSE_CREATED)
                .targetType(ActivityTargetType.EXPENSE)
                .targetId(TestDataFactory.EXPENSE_ID)
                .message("Created expense: Test Expense")
                .createdAt(Instant.now())
                .build();

        given(houseRepository.findById(TestDataFactory.HOUSE_ID)).willReturn(Optional.of(TestDataFactory.aHouse()));
        given(houseMemberRepository.findByHouseIdAndUserId(TestDataFactory.HOUSE_ID, TestDataFactory.USER_ID))
                .willReturn(Optional.of(TestDataFactory.aMember(TestDataFactory.HOUSE_ID, TestDataFactory.USER_ID, HouseRole.VIEWER)));
        given(activityLogRepository.findAllByHouseId(TestDataFactory.HOUSE_ID, ActivityType.EXPENSE_CREATED))
                .willReturn(List.of(activityLog));

        List<ActivityLog> result = getHouseActivitiesUseCase.execute(
                TestDataFactory.HOUSE_ID,
                TestDataFactory.USER_ID,
                ActivityType.EXPENSE_CREATED
        );

        assertThat(result).hasSize(1);
        assertThat(result.getFirst().getType()).isEqualTo(ActivityType.EXPENSE_CREATED);
    }

    @Test
    void getActivities_notMember_throwsForbidden() {
        given(houseRepository.findById(TestDataFactory.HOUSE_ID)).willReturn(Optional.of(TestDataFactory.aHouse()));
        given(houseMemberRepository.findByHouseIdAndUserId(TestDataFactory.HOUSE_ID, TestDataFactory.USER_ID))
                .willReturn(Optional.empty());

        assertThatThrownBy(() -> getHouseActivitiesUseCase.execute(
                TestDataFactory.HOUSE_ID,
                TestDataFactory.USER_ID,
                null
        )).isInstanceOf(ForbiddenException.class);
    }
}
