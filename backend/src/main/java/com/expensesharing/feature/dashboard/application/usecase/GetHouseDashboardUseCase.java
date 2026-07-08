package com.expensesharing.feature.dashboard.application.usecase;

import com.expensesharing.common.exception.ForbiddenException;
import com.expensesharing.common.exception.NotFoundException;
import com.expensesharing.feature.dashboard.application.service.DashboardCalculator;
import com.expensesharing.feature.dashboard.domain.model.HouseDashboard;
import com.expensesharing.feature.house.domain.repository.HouseMemberRepository;
import com.expensesharing.feature.house.domain.repository.HouseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GetHouseDashboardUseCase {

    private final HouseRepository houseRepository;
    private final HouseMemberRepository houseMemberRepository;
    private final DashboardCalculator dashboardCalculator;

    @Transactional(readOnly = true)
    public HouseDashboard execute(UUID houseId, UUID requesterId) {
        houseRepository.findById(houseId)
                .orElseThrow(() -> new NotFoundException("HOUSE_NOT_FOUND", "House not found."));

        if (!houseMemberRepository.existsByHouseIdAndUserId(houseId, requesterId)) {
            throw new ForbiddenException("NOT_A_MEMBER", "You are not a member of this house.");
        }

        return dashboardCalculator.calculate(houseId);
    }
}
