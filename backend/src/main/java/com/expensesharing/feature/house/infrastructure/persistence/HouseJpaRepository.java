package com.expensesharing.feature.house.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface HouseJpaRepository extends JpaRepository<HouseJpaEntity, UUID> {

    @Query("SELECT h FROM HouseJpaEntity h JOIN HouseMemberJpaEntity m ON m.houseId = h.id WHERE m.userId = :userId")
    List<HouseJpaEntity> findAllByMemberId(UUID userId);
}
