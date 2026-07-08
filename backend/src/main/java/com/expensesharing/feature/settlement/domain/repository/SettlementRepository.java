package com.expensesharing.feature.settlement.domain.repository;

import com.expensesharing.feature.settlement.domain.model.Settlement;

import java.util.List;
import java.util.UUID;

public interface SettlementRepository {

    Settlement save(Settlement settlement);

    List<Settlement> findAllByHouseId(UUID houseId);
}
