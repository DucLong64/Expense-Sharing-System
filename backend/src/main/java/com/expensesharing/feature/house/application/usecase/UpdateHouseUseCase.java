package com.expensesharing.feature.house.application.usecase;

import com.expensesharing.common.exception.ForbiddenException;
import com.expensesharing.common.exception.NotFoundException;
import com.expensesharing.feature.activity.application.service.ActivityLogService;
import com.expensesharing.feature.activity.domain.model.ActivityTargetType;
import com.expensesharing.feature.activity.domain.model.ActivityType;
import com.expensesharing.feature.house.application.dto.UpdateHouseCommand;
import com.expensesharing.feature.house.domain.model.House;
import com.expensesharing.feature.house.domain.model.HouseMember;
import com.expensesharing.feature.house.domain.model.HouseRole;
import com.expensesharing.feature.house.domain.repository.HouseMemberRepository;
import com.expensesharing.feature.house.domain.repository.HouseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UpdateHouseUseCase {

    private final ActivityLogService activityLogService;
    private final HouseRepository houseRepository;
    private final HouseMemberRepository houseMemberRepository;

    @Transactional
    public House execute(UpdateHouseCommand command) {
        House house = houseRepository.findById(command.houseId())
                .orElseThrow(() -> new NotFoundException("HOUSE_NOT_FOUND", "House not found."));

        HouseMember requester = houseMemberRepository.findByHouseIdAndUserId(command.houseId(), command.requesterId())
                .orElseThrow(() -> new ForbiddenException("NOT_A_MEMBER", "You are not a member of this house."));

        if (!requester.hasPermission(HouseRole.ADMIN)) {
            throw new ForbiddenException("INSUFFICIENT_PERMISSION", "Only ADMIN or OWNER can update house info.");
        }

        house.update(command.name(), command.description());
        House updatedHouse = houseRepository.save(house);

        activityLogService.log(
                updatedHouse.getId(),
                command.requesterId(),
                ActivityType.HOUSE_UPDATED,
                ActivityTargetType.HOUSE,
                updatedHouse.getId(),
                "Updated house: " + updatedHouse.getName()
        );

        return updatedHouse;
    }
}
