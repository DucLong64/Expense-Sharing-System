package com.expensesharing.feature.settlement.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface SettlementJpaRepository extends JpaRepository<SettlementJpaEntity, UUID> {

    List<SettlementJpaEntity> findAllByHouseIdOrderBySettledAtDesc(UUID houseId);
}
