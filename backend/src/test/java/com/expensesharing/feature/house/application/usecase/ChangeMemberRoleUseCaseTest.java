package com.expensesharing.feature.house.application.usecase;

import com.expensesharing.TestDataFactory;
import com.expensesharing.common.exception.BusinessException;
import com.expensesharing.common.exception.ForbiddenException;
import com.expensesharing.feature.house.application.dto.ChangeMemberRoleCommand;
import com.expensesharing.feature.house.domain.model.HouseRole;
import com.expensesharing.feature.house.domain.repository.HouseMemberRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class ChangeMemberRoleUseCaseTest {

    @Mock HouseMemberRepository houseMemberRepository;

    @InjectMocks ChangeMemberRoleUseCase changeMemberRoleUseCase;

    @Test
    void changeRole_byOwner_success() {
        var command = new ChangeMemberRoleCommand(TestDataFactory.HOUSE_ID, TestDataFactory.USER_ID,
                TestDataFactory.OTHER_USER_ID, HouseRole.ADMIN);

        given(houseMemberRepository.findByHouseIdAndUserId(TestDataFactory.HOUSE_ID, TestDataFactory.USER_ID))
                .willReturn(Optional.of(TestDataFactory.aMember(TestDataFactory.HOUSE_ID, TestDataFactory.USER_ID, HouseRole.OWNER)));
        given(houseMemberRepository.findByHouseIdAndUserId(TestDataFactory.HOUSE_ID, TestDataFactory.OTHER_USER_ID))
                .willReturn(Optional.of(TestDataFactory.aMember(TestDataFactory.HOUSE_ID, TestDataFactory.OTHER_USER_ID, HouseRole.MEMBER)));
        given(houseMemberRepository.save(any())).willAnswer(inv -> inv.getArgument(0));

        var result = changeMemberRoleUseCase.execute(command);

        assertThat(result.getRole()).isEqualTo(HouseRole.ADMIN);
    }

    @Test
    void changeRole_byAdmin_throwsForbidden() {
        var command = new ChangeMemberRoleCommand(TestDataFactory.HOUSE_ID, TestDataFactory.USER_ID,
                TestDataFactory.OTHER_USER_ID, HouseRole.ADMIN);

        given(houseMemberRepository.findByHouseIdAndUserId(TestDataFactory.HOUSE_ID, TestDataFactory.USER_ID))
                .willReturn(Optional.of(TestDataFactory.aMember(TestDataFactory.HOUSE_ID, TestDataFactory.USER_ID, HouseRole.ADMIN)));

        assertThatThrownBy(() -> changeMemberRoleUseCase.execute(command))
                .isInstanceOf(ForbiddenException.class);
    }

    @Test
    void changeRole_targetIsOwner_throwsException() {
        var command = new ChangeMemberRoleCommand(TestDataFactory.HOUSE_ID, TestDataFactory.USER_ID,
                TestDataFactory.OTHER_USER_ID, HouseRole.MEMBER);

        given(houseMemberRepository.findByHouseIdAndUserId(TestDataFactory.HOUSE_ID, TestDataFactory.USER_ID))
                .willReturn(Optional.of(TestDataFactory.aMember(TestDataFactory.HOUSE_ID, TestDataFactory.USER_ID, HouseRole.OWNER)));
        given(houseMemberRepository.findByHouseIdAndUserId(TestDataFactory.HOUSE_ID, TestDataFactory.OTHER_USER_ID))
                .willReturn(Optional.of(TestDataFactory.aMember(TestDataFactory.HOUSE_ID, TestDataFactory.OTHER_USER_ID, HouseRole.OWNER)));

        assertThatThrownBy(() -> changeMemberRoleUseCase.execute(command))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Cannot change the role of the OWNER");
    }

    @Test
    void changeRole_assignOwner_throwsException() {
        var command = new ChangeMemberRoleCommand(TestDataFactory.HOUSE_ID, TestDataFactory.USER_ID,
                TestDataFactory.OTHER_USER_ID, HouseRole.OWNER);

        given(houseMemberRepository.findByHouseIdAndUserId(TestDataFactory.HOUSE_ID, TestDataFactory.USER_ID))
                .willReturn(Optional.of(TestDataFactory.aMember(TestDataFactory.HOUSE_ID, TestDataFactory.USER_ID, HouseRole.OWNER)));
        given(houseMemberRepository.findByHouseIdAndUserId(TestDataFactory.HOUSE_ID, TestDataFactory.OTHER_USER_ID))
                .willReturn(Optional.of(TestDataFactory.aMember(TestDataFactory.HOUSE_ID, TestDataFactory.OTHER_USER_ID, HouseRole.MEMBER)));

        assertThatThrownBy(() -> changeMemberRoleUseCase.execute(command))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Cannot assign OWNER");
    }
}
