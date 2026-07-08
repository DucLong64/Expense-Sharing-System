package com.expensesharing.feature.expense.application.usecase;

import com.expensesharing.TestDataFactory;
import com.expensesharing.common.exception.ForbiddenException;
import com.expensesharing.feature.activity.application.service.ActivityLogService;
import com.expensesharing.feature.expense.application.dto.CreateExpenseCommand;
import com.expensesharing.feature.expense.application.dto.ParticipantShare;
import com.expensesharing.feature.expense.application.service.SplitCalculator;
import com.expensesharing.feature.expense.domain.model.SplitType;
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

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class CreateExpenseUseCaseTest {

    @Mock ActivityLogService activityLogService;
    @Mock ExpenseRepository expenseRepository;
    @Mock ExpenseParticipantRepository participantRepository;
    @Mock HouseRepository houseRepository;
    @Mock HouseMemberRepository houseMemberRepository;
    @Mock SplitCalculator splitCalculator;

    @InjectMocks CreateExpenseUseCase createExpenseUseCase;

    private CreateExpenseCommand aCommand(HouseRole role) {
        return new CreateExpenseCommand(
                TestDataFactory.HOUSE_ID, TestDataFactory.USER_ID,
                "Tiền điện", null, new BigDecimal("900000"),
                TestDataFactory.USER_ID, SplitType.EQUAL,
                LocalDate.now(), null,
                List.of(new ParticipantShare(TestDataFactory.USER_ID, null, null))
        );
    }

    @Test
    void create_asMember_success() {
        given(houseRepository.findById(TestDataFactory.HOUSE_ID)).willReturn(Optional.of(TestDataFactory.aHouse()));
        given(houseMemberRepository.findByHouseIdAndUserId(TestDataFactory.HOUSE_ID, TestDataFactory.USER_ID))
                .willReturn(Optional.of(TestDataFactory.aMember(TestDataFactory.HOUSE_ID, TestDataFactory.USER_ID, HouseRole.MEMBER)));
        given(splitCalculator.calculate(any(), any(), any(), any())).willReturn(List.of());
        given(expenseRepository.save(any())).willAnswer(inv -> inv.getArgument(0));
        given(participantRepository.saveAll(any())).willReturn(List.of());

        var result = createExpenseUseCase.execute(aCommand(HouseRole.MEMBER));

        assertThat(result.expense().getTitle()).isEqualTo("Tiền điện");
        assertThat(result.expense().getHouseId()).isEqualTo(TestDataFactory.HOUSE_ID);
    }

    @Test
    void create_notMember_throwsForbidden() {
        given(houseRepository.findById(TestDataFactory.HOUSE_ID)).willReturn(Optional.of(TestDataFactory.aHouse()));
        given(houseMemberRepository.findByHouseIdAndUserId(TestDataFactory.HOUSE_ID, TestDataFactory.USER_ID))
                .willReturn(Optional.empty());

        assertThatThrownBy(() -> createExpenseUseCase.execute(aCommand(HouseRole.MEMBER)))
                .isInstanceOf(ForbiddenException.class);
    }

    @Test
    void create_asViewer_throwsForbidden() {
        given(houseRepository.findById(TestDataFactory.HOUSE_ID)).willReturn(Optional.of(TestDataFactory.aHouse()));
        given(houseMemberRepository.findByHouseIdAndUserId(TestDataFactory.HOUSE_ID, TestDataFactory.USER_ID))
                .willReturn(Optional.of(TestDataFactory.aMember(TestDataFactory.HOUSE_ID, TestDataFactory.USER_ID, HouseRole.VIEWER)));

        assertThatThrownBy(() -> createExpenseUseCase.execute(aCommand(HouseRole.VIEWER)))
                .isInstanceOf(ForbiddenException.class);
    }
}
