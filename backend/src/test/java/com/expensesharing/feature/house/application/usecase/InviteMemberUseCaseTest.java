package com.expensesharing.feature.house.application.usecase;

import com.expensesharing.TestDataFactory;
import com.expensesharing.common.exception.BusinessException;
import com.expensesharing.common.exception.ForbiddenException;
import com.expensesharing.feature.activity.application.service.ActivityLogService;
import com.expensesharing.feature.house.application.dto.InviteMemberCommand;
import com.expensesharing.feature.house.application.port.UserLookupPort;
import com.expensesharing.feature.house.domain.model.HouseRole;
import com.expensesharing.feature.house.domain.repository.HouseMemberRepository;
import com.expensesharing.feature.house.domain.repository.HouseRepository;
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
class InviteMemberUseCaseTest {

    @Mock ActivityLogService activityLogService;
    @Mock HouseRepository houseRepository;
    @Mock HouseMemberRepository houseMemberRepository;
    @Mock UserLookupPort userLookupPort;

    @InjectMocks InviteMemberUseCase inviteMemberUseCase;

    @Test
    void invite_byAdmin_success() {
        var command = new InviteMemberCommand(TestDataFactory.HOUSE_ID, TestDataFactory.USER_ID,
                "other_user", HouseRole.MEMBER);

        given(houseRepository.findById(TestDataFactory.HOUSE_ID)).willReturn(Optional.of(TestDataFactory.aHouse()));
        given(houseMemberRepository.findByHouseIdAndUserId(TestDataFactory.HOUSE_ID, TestDataFactory.USER_ID))
                .willReturn(Optional.of(TestDataFactory.aMember(TestDataFactory.HOUSE_ID, TestDataFactory.USER_ID, HouseRole.ADMIN)));
        given(userLookupPort.findUserIdByIdentifier("other_user")).willReturn(TestDataFactory.OTHER_USER_ID);
        given(houseMemberRepository.existsByHouseIdAndUserId(TestDataFactory.HOUSE_ID, TestDataFactory.OTHER_USER_ID))
                .willReturn(false);
        given(houseMemberRepository.save(any())).willAnswer(inv -> inv.getArgument(0));

        var result = inviteMemberUseCase.execute(command);

        assertThat(result.getUserId()).isEqualTo(TestDataFactory.OTHER_USER_ID);
        assertThat(result.getRole()).isEqualTo(HouseRole.MEMBER);
    }

    @Test
    void invite_alreadyMember_throwsException() {
        var command = new InviteMemberCommand(TestDataFactory.HOUSE_ID, TestDataFactory.USER_ID,
                "other@example.com", HouseRole.MEMBER);

        given(houseRepository.findById(TestDataFactory.HOUSE_ID)).willReturn(Optional.of(TestDataFactory.aHouse()));
        given(houseMemberRepository.findByHouseIdAndUserId(TestDataFactory.HOUSE_ID, TestDataFactory.USER_ID))
                .willReturn(Optional.of(TestDataFactory.aMember(TestDataFactory.HOUSE_ID, TestDataFactory.USER_ID, HouseRole.ADMIN)));
        given(userLookupPort.findUserIdByIdentifier("other@example.com")).willReturn(TestDataFactory.OTHER_USER_ID);
        given(houseMemberRepository.existsByHouseIdAndUserId(TestDataFactory.HOUSE_ID, TestDataFactory.OTHER_USER_ID))
                .willReturn(true);

        assertThatThrownBy(() -> inviteMemberUseCase.execute(command))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("already a member");
    }

    @Test
    void invite_byViewer_throwsForbidden() {
        var command = new InviteMemberCommand(TestDataFactory.HOUSE_ID, TestDataFactory.USER_ID,
                "other_user", HouseRole.MEMBER);

        given(houseRepository.findById(TestDataFactory.HOUSE_ID)).willReturn(Optional.of(TestDataFactory.aHouse()));
        given(houseMemberRepository.findByHouseIdAndUserId(TestDataFactory.HOUSE_ID, TestDataFactory.USER_ID))
                .willReturn(Optional.of(TestDataFactory.aMember(TestDataFactory.HOUSE_ID, TestDataFactory.USER_ID, HouseRole.VIEWER)));

        assertThatThrownBy(() -> inviteMemberUseCase.execute(command))
                .isInstanceOf(ForbiddenException.class);
    }

    @Test
    void invite_withOwnerRole_assignsAdminInstead() {
        var command = new InviteMemberCommand(TestDataFactory.HOUSE_ID, TestDataFactory.USER_ID,
                "other_user", HouseRole.OWNER);

        given(houseRepository.findById(TestDataFactory.HOUSE_ID)).willReturn(Optional.of(TestDataFactory.aHouse()));
        given(houseMemberRepository.findByHouseIdAndUserId(TestDataFactory.HOUSE_ID, TestDataFactory.USER_ID))
                .willReturn(Optional.of(TestDataFactory.aMember(TestDataFactory.HOUSE_ID, TestDataFactory.USER_ID, HouseRole.OWNER)));
        given(userLookupPort.findUserIdByIdentifier("other_user")).willReturn(TestDataFactory.OTHER_USER_ID);
        given(houseMemberRepository.existsByHouseIdAndUserId(TestDataFactory.HOUSE_ID, TestDataFactory.OTHER_USER_ID))
                .willReturn(false);
        given(houseMemberRepository.save(any())).willAnswer(inv -> inv.getArgument(0));

        var result = inviteMemberUseCase.execute(command);

        assertThat(result.getRole()).isEqualTo(HouseRole.ADMIN);
    }
}
