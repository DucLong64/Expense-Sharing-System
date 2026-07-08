package com.expensesharing.feature.house.application.usecase;

import com.expensesharing.TestDataFactory;
import com.expensesharing.common.exception.ForbiddenException;
import com.expensesharing.feature.activity.application.service.ActivityLogService;
import com.expensesharing.feature.expense.domain.repository.ExpenseParticipantRepository;
import com.expensesharing.feature.expense.domain.repository.ExpenseRepository;
import com.expensesharing.feature.house.domain.model.HouseRole;
import com.expensesharing.feature.house.domain.repository.HouseMemberRepository;
import com.expensesharing.feature.house.domain.repository.HouseRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class DeleteHouseUseCaseTest {

    @Mock ActivityLogService activityLogService;
    @Mock HouseRepository houseRepository;
    @Mock HouseMemberRepository houseMemberRepository;
    @Mock ExpenseRepository expenseRepository;
    @Mock ExpenseParticipantRepository expenseParticipantRepository;

    @InjectMocks DeleteHouseUseCase deleteHouseUseCase;

    @Test
    void delete_byOwner_success() {
        given(houseRepository.findById(TestDataFactory.HOUSE_ID)).willReturn(Optional.of(TestDataFactory.aHouse()));
        given(houseMemberRepository.findByHouseIdAndUserId(TestDataFactory.HOUSE_ID, TestDataFactory.USER_ID))
                .willReturn(Optional.of(TestDataFactory.aMember(TestDataFactory.HOUSE_ID, TestDataFactory.USER_ID, HouseRole.OWNER)));
        given(expenseRepository.findAllByHouseId(TestDataFactory.HOUSE_ID)).willReturn(List.of());
        given(houseMemberRepository.findAllByHouseId(TestDataFactory.HOUSE_ID)).willReturn(List.of());

        assertThatNoException().isThrownBy(() ->
                deleteHouseUseCase.execute(TestDataFactory.HOUSE_ID, TestDataFactory.USER_ID));

        then(houseRepository).should().deleteById(TestDataFactory.HOUSE_ID);
    }

    @Test
    void delete_byAdmin_throwsForbidden() {
        given(houseRepository.findById(TestDataFactory.HOUSE_ID)).willReturn(Optional.of(TestDataFactory.aHouse()));
        given(houseMemberRepository.findByHouseIdAndUserId(TestDataFactory.HOUSE_ID, TestDataFactory.USER_ID))
                .willReturn(Optional.of(TestDataFactory.aMember(TestDataFactory.HOUSE_ID, TestDataFactory.USER_ID, HouseRole.ADMIN)));

        assertThatThrownBy(() -> deleteHouseUseCase.execute(TestDataFactory.HOUSE_ID, TestDataFactory.USER_ID))
                .isInstanceOf(ForbiddenException.class);
    }
}
