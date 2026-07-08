package com.expensesharing.feature.expense.application.usecase;

import com.expensesharing.TestDataFactory;
import com.expensesharing.common.exception.ForbiddenException;
import com.expensesharing.feature.activity.application.service.ActivityLogService;
import com.expensesharing.feature.expense.application.dto.ParticipantShare;
import com.expensesharing.feature.expense.application.dto.UpdateExpenseCommand;
import com.expensesharing.feature.expense.application.service.SplitCalculator;
import com.expensesharing.feature.expense.domain.model.Expense;
import com.expensesharing.feature.expense.domain.model.SplitType;
import com.expensesharing.feature.expense.domain.repository.ExpenseParticipantRepository;
import com.expensesharing.feature.expense.domain.repository.ExpenseRepository;
import com.expensesharing.feature.house.domain.model.HouseRole;
import com.expensesharing.feature.house.domain.repository.HouseMemberRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class UpdateExpenseUseCaseTest {

    @Mock ActivityLogService activityLogService;
    @Mock ExpenseRepository expenseRepository;
    @Mock ExpenseParticipantRepository participantRepository;
    @Mock HouseMemberRepository houseMemberRepository;
    @Mock SplitCalculator splitCalculator;

    @InjectMocks UpdateExpenseUseCase updateExpenseUseCase;

    private final Expense expense = TestDataFactory.anExpense(
            TestDataFactory.HOUSE_ID, TestDataFactory.USER_ID, TestDataFactory.USER_ID, new BigDecimal("900000"));

    private UpdateExpenseCommand aCommand(UUID requesterId) {
        return new UpdateExpenseCommand(
                TestDataFactory.EXPENSE_ID, TestDataFactory.HOUSE_ID, requesterId,
                "Updated Title", null, new BigDecimal("900000"),
                SplitType.EQUAL, LocalDate.now(), null,
                List.of(new ParticipantShare(TestDataFactory.USER_ID, null, null))
        );
    }

    @Test
    void update_byCreator_success() {
        given(expenseRepository.findById(TestDataFactory.EXPENSE_ID)).willReturn(Optional.of(expense));
        given(houseMemberRepository.findByHouseIdAndUserId(TestDataFactory.HOUSE_ID, TestDataFactory.USER_ID))
                .willReturn(Optional.of(TestDataFactory.aMember(TestDataFactory.HOUSE_ID, TestDataFactory.USER_ID, HouseRole.MEMBER)));
        given(splitCalculator.calculate(any(), any(), any(), any())).willReturn(List.of());
        given(expenseRepository.save(any())).willAnswer(inv -> inv.getArgument(0));
        given(participantRepository.saveAll(any())).willReturn(List.of());

        var result = updateExpenseUseCase.execute(aCommand(TestDataFactory.USER_ID));

        assertThat(result.expense().getTitle()).isEqualTo("Updated Title");
    }

    @Test
    void update_byAdmin_success() {
        given(expenseRepository.findById(TestDataFactory.EXPENSE_ID)).willReturn(Optional.of(expense));
        given(houseMemberRepository.findByHouseIdAndUserId(TestDataFactory.HOUSE_ID, TestDataFactory.OTHER_USER_ID))
                .willReturn(Optional.of(TestDataFactory.aMember(TestDataFactory.HOUSE_ID, TestDataFactory.OTHER_USER_ID, HouseRole.ADMIN)));
        given(splitCalculator.calculate(any(), any(), any(), any())).willReturn(List.of());
        given(expenseRepository.save(any())).willAnswer(inv -> inv.getArgument(0));
        given(participantRepository.saveAll(any())).willReturn(List.of());

        var result = updateExpenseUseCase.execute(aCommand(TestDataFactory.OTHER_USER_ID));

        assertThat(result.expense().getTitle()).isEqualTo("Updated Title");
    }

    @Test
    void update_byOtherMember_throwsForbidden() {
        given(expenseRepository.findById(TestDataFactory.EXPENSE_ID)).willReturn(Optional.of(expense));
        given(houseMemberRepository.findByHouseIdAndUserId(TestDataFactory.HOUSE_ID, TestDataFactory.OTHER_USER_ID))
                .willReturn(Optional.of(TestDataFactory.aMember(TestDataFactory.HOUSE_ID, TestDataFactory.OTHER_USER_ID, HouseRole.MEMBER)));

        assertThatThrownBy(() -> updateExpenseUseCase.execute(aCommand(TestDataFactory.OTHER_USER_ID)))
                .isInstanceOf(ForbiddenException.class)
                .hasMessageContaining("only update your own expenses");
    }
}
