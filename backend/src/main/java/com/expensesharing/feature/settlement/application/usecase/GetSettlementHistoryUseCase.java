package com.expensesharing.feature.settlement.application.usecase;

import com.expensesharing.common.exception.ForbiddenException;
import com.expensesharing.common.exception.NotFoundException;
import com.expensesharing.feature.house.domain.repository.HouseMemberRepository;
import com.expensesharing.feature.house.domain.repository.HouseRepository;
import com.expensesharing.feature.settlement.domain.model.Settlement;
import com.expensesharing.feature.settlement.domain.repository.SettlementRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GetSettlementHistoryUseCase {

    private final HouseRepository houseRepository;
    private final HouseMemberRepository houseMemberRepository;
    private final SettlementRepository settlementRepository;

    @Transactional(readOnly = true)
    public List<Settlement> execute(UUID houseId, UUID requesterId) {
        houseRepository.findById(houseId)
                .orElseThrow(() -> new NotFoundException("HOUSE_NOT_FOUND", "House not found."));

        if (!houseMemberRepository.existsByHouseIdAndUserId(houseId, requesterId)) {
            throw new ForbiddenException("NOT_A_MEMBER", "You are not a member of this house.");
        }

        return settlementRepository.findAllByHouseId(houseId);
    }
}
