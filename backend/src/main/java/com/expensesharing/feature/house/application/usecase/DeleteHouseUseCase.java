package com.expensesharing.feature.house.application.usecase;

import com.expensesharing.common.exception.ForbiddenException;
import com.expensesharing.common.exception.NotFoundException;
import com.expensesharing.feature.activity.application.service.ActivityLogService;
import com.expensesharing.feature.activity.domain.model.ActivityTargetType;
import com.expensesharing.feature.activity.domain.model.ActivityType;
import com.expensesharing.feature.expense.domain.model.Expense;
import com.expensesharing.feature.expense.domain.repository.ExpenseParticipantRepository;
import com.expensesharing.feature.expense.domain.repository.ExpenseRepository;
import com.expensesharing.feature.house.domain.model.House;
import com.expensesharing.feature.house.domain.model.HouseMember;
import com.expensesharing.feature.house.domain.model.HouseRole;
import com.expensesharing.feature.house.domain.repository.HouseMemberRepository;
import com.expensesharing.feature.house.domain.repository.HouseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DeleteHouseUseCase {

    private final ActivityLogService activityLogService;
    private final HouseRepository houseRepository;
    private final HouseMemberRepository houseMemberRepository;
    private final ExpenseRepository expenseRepository;
    private final ExpenseParticipantRepository expenseParticipantRepository;

    @Transactional
    public void execute(UUID houseId, UUID requesterId) {
        House house = houseRepository.findById(houseId)
                .orElseThrow(() -> new NotFoundException("HOUSE_NOT_FOUND", "House not found."));

        HouseMember requester = houseMemberRepository.findByHouseIdAndUserId(houseId, requesterId)
                .orElseThrow(() -> new ForbiddenException("NOT_A_MEMBER", "You are not a member of this house."));

        if (!requester.hasPermission(HouseRole.OWNER)) {
            throw new ForbiddenException("INSUFFICIENT_PERMISSION", "Only OWNER can delete the house.");
        }

        activityLogService.log(
                houseId,
                requesterId,
                ActivityType.HOUSE_DELETED,
                ActivityTargetType.HOUSE,
                houseId,
                "Deleted house: " + house.getName()
        );

        for (Expense expense : expenseRepository.findAllByHouseId(houseId)) {
            expenseParticipantRepository.deleteAllByExpenseId(expense.getId());
            expenseRepository.deleteById(expense.getId());
        }

        for (HouseMember member : houseMemberRepository.findAllByHouseId(houseId)) {
            houseMemberRepository.delete(member);
        }

        houseRepository.deleteById(houseId);
    }
}
