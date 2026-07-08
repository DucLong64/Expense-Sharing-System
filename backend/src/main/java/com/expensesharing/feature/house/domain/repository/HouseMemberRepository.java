package com.expensesharing.feature.house.domain.repository;

import com.expensesharing.feature.house.domain.model.HouseMember;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface HouseMemberRepository {

    HouseMember save(HouseMember member);

    Optional<HouseMember> findByHouseIdAndUserId(UUID houseId, UUID userId);

    List<HouseMember> findAllByHouseId(UUID houseId);

    void delete(HouseMember member);

    boolean existsByHouseIdAndUserId(UUID houseId, UUID userId);
}
