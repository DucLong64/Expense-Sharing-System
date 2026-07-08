package com.expensesharing.feature.house.application.usecase;

import com.expensesharing.TestDataFactory;
import com.expensesharing.common.exception.ForbiddenException;
import com.expensesharing.common.exception.NotFoundException;
import com.expensesharing.feature.activity.application.service.ActivityLogService;
import com.expensesharing.feature.house.application.dto.UpdateHouseCommand;
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
class UpdateHouseUseCaseTest {

    @Mock ActivityLogService activityLogService;
    @Mock HouseRepository houseRepository;
    @Mock HouseMemberRepository houseMemberRepository;

    @InjectMocks UpdateHouseUseCase updateHouseUseCase;

    @Test
    void update_byAdmin_success() {
        var command = new UpdateHouseCommand(TestDataFactory.HOUSE_ID, TestDataFactory.USER_ID, "New Name", "New Desc");
        given(houseRepository.findById(TestDataFactory.HOUSE_ID)).willReturn(Optional.of(TestDataFactory.aHouse()));
        given(houseMemberRepository.findByHouseIdAndUserId(TestDataFactory.HOUSE_ID, TestDataFactory.USER_ID))
                .willReturn(Optional.of(TestDataFactory.aMember(TestDataFactory.HOUSE_ID, TestDataFactory.USER_ID, HouseRole.ADMIN)));
        given(houseRepository.save(any())).willAnswer(inv -> inv.getArgument(0));

        var result = updateHouseUseCase.execute(command);

        assertThat(result.getName()).isEqualTo("New Name");
    }

    @Test
    void update_houseNotFound_throwsException() {
        var command = new UpdateHouseCommand(TestDataFactory.HOUSE_ID, TestDataFactory.USER_ID, "New Name", null);
        given(houseRepository.findById(TestDataFactory.HOUSE_ID)).willReturn(Optional.empty());

        assertThatThrownBy(() -> updateHouseUseCase.execute(command))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void update_byViewer_throwsForbidden() {
        var command = new UpdateHouseCommand(TestDataFactory.HOUSE_ID, TestDataFactory.USER_ID, "New Name", null);
        given(houseRepository.findById(TestDataFactory.HOUSE_ID)).willReturn(Optional.of(TestDataFactory.aHouse()));
        given(houseMemberRepository.findByHouseIdAndUserId(TestDataFactory.HOUSE_ID, TestDataFactory.USER_ID))
                .willReturn(Optional.of(TestDataFactory.aMember(TestDataFactory.HOUSE_ID, TestDataFactory.USER_ID, HouseRole.VIEWER)));

        assertThatThrownBy(() -> updateHouseUseCase.execute(command))
                .isInstanceOf(ForbiddenException.class);
    }

    @Test
    void update_notMember_throwsForbidden() {
        var command = new UpdateHouseCommand(TestDataFactory.HOUSE_ID, TestDataFactory.USER_ID, "New Name", null);
        given(houseRepository.findById(TestDataFactory.HOUSE_ID)).willReturn(Optional.of(TestDataFactory.aHouse()));
        given(houseMemberRepository.findByHouseIdAndUserId(TestDataFactory.HOUSE_ID, TestDataFactory.USER_ID))
                .willReturn(Optional.empty());

        assertThatThrownBy(() -> updateHouseUseCase.execute(command))
                .isInstanceOf(ForbiddenException.class);
    }
}
