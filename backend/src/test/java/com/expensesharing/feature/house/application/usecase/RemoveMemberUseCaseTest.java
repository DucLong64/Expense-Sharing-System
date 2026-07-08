package com.expensesharing.feature.house.application.usecase;

import com.expensesharing.TestDataFactory;
import com.expensesharing.common.exception.BusinessException;
import com.expensesharing.common.exception.ForbiddenException;
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
class RemoveMemberUseCaseTest {

    @Mock ActivityLogService activityLogService;
    @Mock HouseMemberRepository houseMemberRepository;

    @InjectMocks RemoveMemberUseCase removeMemberUseCase;

    @Test
    void remove_byAdmin_success() {
        var target = TestDataFactory.aMember(TestDataFactory.HOUSE_ID, TestDataFactory.OTHER_USER_ID, HouseRole.MEMBER);

        given(houseMemberRepository.findByHouseIdAndUserId(TestDataFactory.HOUSE_ID, TestDataFactory.USER_ID))
                .willReturn(Optional.of(TestDataFactory.aMember(TestDataFactory.HOUSE_ID, TestDataFactory.USER_ID, HouseRole.ADMIN)));
        given(houseMemberRepository.findByHouseIdAndUserId(TestDataFactory.HOUSE_ID, TestDataFactory.OTHER_USER_ID))
                .willReturn(Optional.of(target));

        assertThatNoException().isThrownBy(() ->
                removeMemberUseCase.execute(TestDataFactory.HOUSE_ID, TestDataFactory.USER_ID, TestDataFactory.OTHER_USER_ID));

        then(houseMemberRepository).should().delete(target);
    }

    @Test
    void remove_byMember_throwsForbidden() {
        given(houseMemberRepository.findByHouseIdAndUserId(TestDataFactory.HOUSE_ID, TestDataFactory.USER_ID))
                .willReturn(Optional.of(TestDataFactory.aMember(TestDataFactory.HOUSE_ID, TestDataFactory.USER_ID, HouseRole.MEMBER)));

        assertThatThrownBy(() ->
                removeMemberUseCase.execute(TestDataFactory.HOUSE_ID, TestDataFactory.USER_ID, TestDataFactory.OTHER_USER_ID))
                .isInstanceOf(ForbiddenException.class);
    }

    @Test
    void remove_targetIsOwner_throwsException() {
        given(houseMemberRepository.findByHouseIdAndUserId(TestDataFactory.HOUSE_ID, TestDataFactory.USER_ID))
                .willReturn(Optional.of(TestDataFactory.aMember(TestDataFactory.HOUSE_ID, TestDataFactory.USER_ID, HouseRole.ADMIN)));
        given(houseMemberRepository.findByHouseIdAndUserId(TestDataFactory.HOUSE_ID, TestDataFactory.OTHER_USER_ID))
                .willReturn(Optional.of(TestDataFactory.aMember(TestDataFactory.HOUSE_ID, TestDataFactory.OTHER_USER_ID, HouseRole.OWNER)));

        assertThatThrownBy(() ->
                removeMemberUseCase.execute(TestDataFactory.HOUSE_ID, TestDataFactory.USER_ID, TestDataFactory.OTHER_USER_ID))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Cannot remove the OWNER");
    }
}
