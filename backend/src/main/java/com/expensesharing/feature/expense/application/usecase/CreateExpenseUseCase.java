package com.expensesharing.feature.expense.application.usecase;

import com.expensesharing.common.exception.ForbiddenException;
import com.expensesharing.common.exception.NotFoundException;
import com.expensesharing.feature.activity.application.service.ActivityLogService;
import com.expensesharing.feature.activity.domain.model.ActivityTargetType;
import com.expensesharing.feature.activity.domain.model.ActivityType;
import com.expensesharing.feature.expense.application.dto.CreateExpenseCommand;
import com.expensesharing.feature.expense.application.dto.ExpenseResult;
import com.expensesharing.feature.expense.application.service.SplitCalculator;
import com.expensesharing.feature.expense.domain.model.Expense;
import com.expensesharing.feature.expense.domain.model.ExpenseParticipant;
import com.expensesharing.feature.expense.domain.repository.ExpenseParticipantRepository;
import com.expensesharing.feature.expense.domain.repository.ExpenseRepository;
import com.expensesharing.feature.house.domain.model.HouseRole;
import com.expensesharing.feature.house.domain.repository.HouseMemberRepository;
import com.expensesharing.feature.house.domain.repository.HouseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CreateExpenseUseCase {

    private final ActivityLogService activityLogService;
    private final ExpenseRepository expenseRepository;
    private final ExpenseParticipantRepository participantRepository;
    private final HouseRepository houseRepository;
    private final HouseMemberRepository houseMemberRepository;
    private final SplitCalculator splitCalculator;

    @Transactional
    public ExpenseResult execute(CreateExpenseCommand command) {
        houseRepository.findById(command.houseId())
                .orElseThrow(() -> new NotFoundException("HOUSE_NOT_FOUND", "House not found."));

        var requester = houseMemberRepository.findByHouseIdAndUserId(command.houseId(), command.requesterId())
                .orElseThrow(() -> new ForbiddenException("NOT_A_MEMBER", "You are not a member of this house."));

        if (!requester.hasPermission(HouseRole.MEMBER)) {
            throw new ForbiddenException("INSUFFICIENT_PERMISSION", "Only MEMBER or above can create expenses.");
        }

        UUID expenseId = UUID.randomUUID();

        List<ExpenseParticipant> participants = splitCalculator.calculate(
                expenseId, command.amount(), command.splitType(), command.participants());

        Expense expense = Expense.builder()
                .id(expenseId)
                .houseId(command.houseId())
                .title(command.title())
                .description(command.description())
                .amount(command.amount())
                .paidBy(command.paidBy())
                .splitType(command.splitType())
                .expenseDate(command.expenseDate())
                .note(command.note())
                .createdBy(command.requesterId())
                .createdAt(Instant.now())
                .build();

        expenseRepository.save(expense);
        participantRepository.saveAll(participants);

        activityLogService.log(
                command.houseId(),
                command.requesterId(),
                ActivityType.EXPENSE_CREATED,
                ActivityTargetType.EXPENSE,
                expense.getId(),
                "Created expense: " + expense.getTitle()
        );

        return new ExpenseResult(expense, participants);
    }
}
