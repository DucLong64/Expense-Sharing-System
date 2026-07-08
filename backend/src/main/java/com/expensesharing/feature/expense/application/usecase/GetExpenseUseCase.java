package com.expensesharing.feature.expense.application.usecase;

import com.expensesharing.common.exception.ForbiddenException;
import com.expensesharing.common.exception.NotFoundException;
import com.expensesharing.feature.expense.application.dto.ExpenseResult;
import com.expensesharing.feature.expense.domain.model.Expense;
import com.expensesharing.feature.expense.domain.repository.ExpenseParticipantRepository;
import com.expensesharing.feature.expense.domain.repository.ExpenseRepository;
import com.expensesharing.feature.house.domain.repository.HouseMemberRepository;
import com.expensesharing.feature.house.domain.repository.HouseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GetExpenseUseCase {

    private final ExpenseRepository expenseRepository;
    private final ExpenseParticipantRepository participantRepository;
    private final HouseRepository houseRepository;
    private final HouseMemberRepository houseMemberRepository;

    @Transactional(readOnly = true)
    public List<ExpenseResult> getAllByHouseId(UUID houseId, UUID requesterId) {
        houseRepository.findById(houseId)
                .orElseThrow(() -> new NotFoundException("HOUSE_NOT_FOUND", "House not found."));

        if (!houseMemberRepository.existsByHouseIdAndUserId(houseId, requesterId)) {
            throw new ForbiddenException("NOT_A_MEMBER", "You are not a member of this house.");
        }

        return expenseRepository.findAllByHouseId(houseId).stream()
                .map(e -> new ExpenseResult(e, participantRepository.findAllByExpenseId(e.getId())))
                .toList();
    }

    @Transactional(readOnly = true)
    public ExpenseResult getById(UUID expenseId, UUID houseId, UUID requesterId) {
        Expense expense = expenseRepository.findById(expenseId)
                .orElseThrow(() -> new NotFoundException("EXPENSE_NOT_FOUND", "Expense not found."));

        if (!expense.getHouseId().equals(houseId)) {
            throw new NotFoundException("EXPENSE_NOT_FOUND", "Expense not found in this house.");
        }

        if (!houseMemberRepository.existsByHouseIdAndUserId(houseId, requesterId)) {
            throw new ForbiddenException("NOT_A_MEMBER", "You are not a member of this house.");
        }

        return new ExpenseResult(expense, participantRepository.findAllByExpenseId(expenseId));
    }
}
