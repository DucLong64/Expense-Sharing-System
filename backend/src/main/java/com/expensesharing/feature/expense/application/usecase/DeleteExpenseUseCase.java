package com.expensesharing.feature.expense.application.usecase;

import com.expensesharing.common.exception.ForbiddenException;
import com.expensesharing.common.exception.NotFoundException;
import com.expensesharing.feature.activity.application.service.ActivityLogService;
import com.expensesharing.feature.activity.domain.model.ActivityTargetType;
import com.expensesharing.feature.activity.domain.model.ActivityType;
import com.expensesharing.feature.expense.domain.model.Expense;
import com.expensesharing.feature.expense.domain.repository.ExpenseParticipantRepository;
import com.expensesharing.feature.expense.domain.repository.ExpenseRepository;
import com.expensesharing.feature.house.domain.model.HouseRole;
import com.expensesharing.feature.house.domain.repository.HouseMemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DeleteExpenseUseCase {

    private final ActivityLogService activityLogService;
    private final ExpenseRepository expenseRepository;
    private final ExpenseParticipantRepository participantRepository;
    private final HouseMemberRepository houseMemberRepository;

    @Transactional
    public void execute(UUID expenseId, UUID houseId, UUID requesterId) {
        Expense expense = expenseRepository.findById(expenseId)
                .orElseThrow(() -> new NotFoundException("EXPENSE_NOT_FOUND", "Expense not found."));

        if (!expense.getHouseId().equals(houseId)) {
            throw new NotFoundException("EXPENSE_NOT_FOUND", "Expense not found in this house.");
        }

        var requester = houseMemberRepository.findByHouseIdAndUserId(houseId, requesterId)
                .orElseThrow(() -> new ForbiddenException("NOT_A_MEMBER", "You are not a member of this house."));

        boolean isAdminOrAbove = requester.hasPermission(HouseRole.ADMIN);
        boolean isCreator = expense.getCreatedBy().equals(requesterId);

        if (!isAdminOrAbove && !isCreator) {
            throw new ForbiddenException("INSUFFICIENT_PERMISSION", "You can only delete your own expenses.");
        }

        participantRepository.deleteAllByExpenseId(expenseId);
        expenseRepository.deleteById(expenseId);

        activityLogService.log(
                houseId,
                requesterId,
                ActivityType.EXPENSE_DELETED,
                ActivityTargetType.EXPENSE,
                expenseId,
                "Deleted expense: " + expense.getTitle()
        );
    }
}
