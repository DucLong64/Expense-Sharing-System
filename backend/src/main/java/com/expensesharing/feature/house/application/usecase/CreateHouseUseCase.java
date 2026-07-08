package com.expensesharing.feature.house.application.usecase;

import com.expensesharing.feature.activity.application.service.ActivityLogService;
import com.expensesharing.feature.activity.domain.model.ActivityTargetType;
import com.expensesharing.feature.activity.domain.model.ActivityType;
import com.expensesharing.feature.house.application.dto.CreateHouseCommand;
import com.expensesharing.feature.house.domain.model.House;
import com.expensesharing.feature.house.domain.model.HouseMember;
import com.expensesharing.feature.house.domain.model.HouseRole;
import com.expensesharing.feature.house.domain.repository.HouseMemberRepository;
import com.expensesharing.feature.house.domain.repository.HouseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CreateHouseUseCase {

    private final ActivityLogService activityLogService;
    private final HouseRepository houseRepository;
    private final HouseMemberRepository houseMemberRepository;

    @Transactional
    public House execute(CreateHouseCommand command) {
        House house = House.builder()
                .id(UUID.randomUUID())
                .name(command.name())
                .description(command.description())
                .createdBy(command.createdBy())
                .createdAt(Instant.now())
                .build();

        houseRepository.save(house);

        HouseMember owner = HouseMember.builder()
                .id(UUID.randomUUID())
                .houseId(house.getId())
                .userId(command.createdBy())
                .role(HouseRole.OWNER)
                .joinedAt(Instant.now())
                .build();

        houseMemberRepository.save(owner);

        activityLogService.log(
                house.getId(),
                command.createdBy(),
                ActivityType.HOUSE_CREATED,
                ActivityTargetType.HOUSE,
                house.getId(),
                "Created house: " + house.getName()
        );

        return house;
    }
}
