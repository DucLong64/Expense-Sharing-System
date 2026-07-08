package com.expensesharing.feature.settlement.presentation.response;

import com.expensesharing.feature.settlement.domain.model.Settlement;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record SettlementResponse(
        UUID id,
        UUID houseId,
        UUID fromUserId,
        String fromUsername,
        UUID toUserId,
        String toUsername,
        BigDecimal amount,
        String note,
        Instant settledAt
) {
    public static SettlementResponse from(Settlement settlement) {
        return new SettlementResponse(
                settlement.getId(),
                settlement.getHouseId(),
                settlement.getFromUserId(),
                null,
                settlement.getToUserId(),
                null,
                settlement.getAmount(),
                settlement.getNote(),
                settlement.getSettledAt()
        );
    }
}
