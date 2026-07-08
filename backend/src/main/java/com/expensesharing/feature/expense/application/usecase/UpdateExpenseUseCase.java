package com.expensesharing.feature.expense.application.usecase;

import com.expensesharing.common.exception.ForbiddenException;
import com.expensesharing.common.exception.NotFoundException;
import com.expensesharing.feature.activity.application.service.ActivityLogService;
import com.expensesharing.feature.activity.domain.model.ActivityTargetType;
import com.expensesharing.feature.activity.domain.model.ActivityType;
import com.expensesharing.feature.expense.application.dto.ExpenseResult;
import com.expensesharing.feature.expense.application.dto.UpdateExpenseCommand;
import com.expensesharing.feature.expense.application.service.SplitCalculator;
import com.expensesharing.feature.expense.domain.model.Expense;
import com.expensesharing.feature.expense.domain.model.ExpenseParticipant;
import com.expensesharing.feature.expense.domain.repository.ExpenseParticipantRepository;
import com.expensesharing.feature.expense.domain.repository.ExpenseRepository;
import com.expensesharing.feature.house.domain.model.HouseRole;
import com.expensesharing.feature.house.domain.repository.HouseMemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UpdateExpenseUseCase {

    private final ActivityLogService activityLogService;
    private final ExpenseRepository expenseRepository;
    private final ExpenseParticipantRepository participantRepository;
    private final HouseMemberRepository houseMemberRepository;
    private final SplitCalculator splitCalculator;

    @Transactional
    public ExpenseResult execute(UpdateExpenseCommand command) {
        Expense expense = expenseRepository.findById(command.expenseId())
                .orElseThrow(() -> new NotFoundException("EXPENSE_NOT_FOUND", "Expense not found."));

        if (!expense.getHouseId().equals(command.houseId())) {
            throw new NotFoundException("EXPENSE_NOT_FOUND", "Expense not found in this house.");
        }

        var requester = houseMemberRepository.findByHouseIdAndUserId(command.houseId(), command.requesterId())
                .orElseThrow(() -> new ForbiddenException("NOT_A_MEMBER", "You are not a member of this house."));

        boolean isOwnerOrAdmin = requester.hasPermission(HouseRole.ADMIN);
        boolean isCreator = expense.getCreatedBy().equals(command.requesterId());

        if (!isOwnerOrAdmin && !isCreator) {
            throw new ForbiddenException("INSUFFICIENT_PERMISSION", "You can only update your own expenses.");
        }

        expense.update(command.title(), command.description(), command.amount(),
                command.splitType(), command.expenseDate(), command.note());

        List<ExpenseParticipant> participants = splitCalculator.calculate(
                expense.getId(), command.amount(), command.splitType(), command.participants());

        expenseRepository.save(expense);
        participantRepository.deleteAllByExpenseId(expense.getId());
        participantRepository.saveAll(participants);

        activityLogService.log(
                command.houseId(),
                command.requesterId(),
                ActivityType.EXPENSE_UPDATED,
                ActivityTargetType.EXPENSE,
                expense.getId(),
                "Updated expense: " + expense.getTitle()
        );

        return new ExpenseResult(expense, participants);
    }
}
