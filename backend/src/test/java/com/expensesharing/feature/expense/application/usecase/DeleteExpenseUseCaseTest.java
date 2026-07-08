package com.expensesharing.feature.expense.application.usecase;

import com.expensesharing.TestDataFactory;
import com.expensesharing.common.exception.ForbiddenException;
import com.expensesharing.feature.activity.application.service.ActivityLogService;
import com.expensesharing.feature.expense.domain.model.Expense;
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
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class DeleteExpenseUseCaseTest {

    @Mock ActivityLogService activityLogService;
    @Mock ExpenseRepository expenseRepository;
    @Mock ExpenseParticipantRepository participantRepository;
    @Mock HouseMemberRepository houseMemberRepository;

    @InjectMocks DeleteExpenseUseCase deleteExpenseUseCase;

    private final Expense expense = TestDataFactory.anExpense(
            TestDataFactory.HOUSE_ID, TestDataFactory.USER_ID, TestDataFactory.USER_ID, new BigDecimal("900000"));

    @Test
    void delete_byCreator_success() {
        given(expenseRepository.findById(TestDataFactory.EXPENSE_ID)).willReturn(Optional.of(expense));
        given(houseMemberRepository.findByHouseIdAndUserId(TestDataFactory.HOUSE_ID, TestDataFactory.USER_ID))
                .willReturn(Optional.of(TestDataFactory.aMember(TestDataFactory.HOUSE_ID, TestDataFactory.USER_ID, HouseRole.MEMBER)));

        assertThatNoException().isThrownBy(() ->
                deleteExpenseUseCase.execute(TestDataFactory.EXPENSE_ID, TestDataFactory.HOUSE_ID, TestDataFactory.USER_ID));

        then(participantRepository).should().deleteAllByExpenseId(TestDataFactory.EXPENSE_ID);
        then(expenseRepository).should().deleteById(TestDataFactory.EXPENSE_ID);
    }

    @Test
    void delete_byAdmin_success() {
        given(expenseRepository.findById(TestDataFactory.EXPENSE_ID)).willReturn(Optional.of(expense));
        given(houseMemberRepository.findByHouseIdAndUserId(TestDataFactory.HOUSE_ID, TestDataFactory.OTHER_USER_ID))
                .willReturn(Optional.of(TestDataFactory.aMember(TestDataFactory.HOUSE_ID, TestDataFactory.OTHER_USER_ID, HouseRole.ADMIN)));

        assertThatNoException().isThrownBy(() ->
                deleteExpenseUseCase.execute(TestDataFactory.EXPENSE_ID, TestDataFactory.HOUSE_ID, TestDataFactory.OTHER_USER_ID));
    }

    @Test
    void delete_byOtherMember_throwsForbidden() {
        given(expenseRepository.findById(TestDataFactory.EXPENSE_ID)).willReturn(Optional.of(expense));
        given(houseMemberRepository.findByHouseIdAndUserId(TestDataFactory.HOUSE_ID, TestDataFactory.OTHER_USER_ID))
                .willReturn(Optional.of(TestDataFactory.aMember(TestDataFactory.HOUSE_ID, TestDataFactory.OTHER_USER_ID, HouseRole.MEMBER)));

        assertThatThrownBy(() ->
                deleteExpenseUseCase.execute(TestDataFactory.EXPENSE_ID, TestDataFactory.HOUSE_ID, TestDataFactory.OTHER_USER_ID))
                .isInstanceOf(ForbiddenException.class);
    }
}
