package com.expensesharing.feature.house.application.usecase;

import com.expensesharing.common.exception.ForbiddenException;
import com.expensesharing.common.exception.NotFoundException;
import com.expensesharing.feature.house.domain.model.House;
import com.expensesharing.feature.house.domain.repository.HouseMemberRepository;
import com.expensesharing.feature.house.domain.repository.HouseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GetHouseUseCase {

    private final HouseRepository houseRepository;
    private final HouseMemberRepository houseMemberRepository;

    @Transactional(readOnly = true)
    public House getById(UUID houseId, UUID requesterId) {
        House house = houseRepository.findById(houseId)
                .orElseThrow(() -> new NotFoundException("HOUSE_NOT_FOUND", "House not found."));

        if (!houseMemberRepository.existsByHouseIdAndUserId(houseId, requesterId)) {
            throw new ForbiddenException("NOT_A_MEMBER", "You are not a member of this house.");
        }

        return house;
    }

    @Transactional(readOnly = true)
    public List<House> getMyHouses(UUID userId) {
        return houseRepository.findAllByMemberId(userId);
    }
}
