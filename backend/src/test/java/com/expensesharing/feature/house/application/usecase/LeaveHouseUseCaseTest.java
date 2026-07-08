package com.expensesharing.feature.house.application.usecase;

import com.expensesharing.TestDataFactory;
import com.expensesharing.common.exception.BusinessException;
import com.expensesharing.feature.activity.application.service.ActivityLogService;
import com.expensesharing.feature.house.domain.model.HouseRole;
import com.expensesharing.feature.house.domain.repository.HouseMemberRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class LeaveHouseUseCaseTest {

    @Mock ActivityLogService activityLogService;
    @Mock HouseMemberRepository houseMemberRepository;

    @InjectMocks LeaveHouseUseCase leaveHouseUseCase;

    @Test
    void leave_asMember_success() {
        var member = TestDataFactory.aMember(TestDataFactory.HOUSE_ID, TestDataFactory.USER_ID, HouseRole.MEMBER);
        given(houseMemberRepository.findByHouseIdAndUserId(TestDataFactory.HOUSE_ID, TestDataFactory.USER_ID))
                .willReturn(Optional.of(member));

        assertThatNoException().isThrownBy(() ->
                leaveHouseUseCase.execute(TestDataFactory.HOUSE_ID, TestDataFactory.USER_ID));

        then(houseMemberRepository).should().delete(member);
    }

    @Test
    void leave_asOwner_throwsException() {
        given(houseMemberRepository.findByHouseIdAndUserId(TestDataFactory.HOUSE_ID, TestDataFactory.USER_ID))
                .willReturn(Optional.of(TestDataFactory.aMember(TestDataFactory.HOUSE_ID, TestDataFactory.USER_ID, HouseRole.OWNER)));

        assertThatThrownBy(() -> leaveHouseUseCase.execute(TestDataFactory.HOUSE_ID, TestDataFactory.USER_ID))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("OWNER cannot leave");
    }
}
