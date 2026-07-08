package com.expensesharing.feature.house.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface HouseMemberJpaRepository extends JpaRepository<HouseMemberJpaEntity, UUID> {

    Optional<HouseMemberJpaEntity> findByHouseIdAndUserId(UUID houseId, UUID userId);

    List<HouseMemberJpaEntity> findAllByHouseId(UUID houseId);

    boolean existsByHouseIdAndUserId(UUID houseId, UUID userId);
}
