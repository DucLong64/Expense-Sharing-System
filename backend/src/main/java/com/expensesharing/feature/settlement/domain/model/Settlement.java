package com.expensesharing.feature.settlement.domain.model;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Getter
@Builder
public class Settlement {

    private final UUID id;
    private final UUID houseId;
    private final UUID fromUserId;
    private final UUID toUserId;
    private final BigDecimal amount;
    private final String note;
    private final Instant settledAt;
    private final UUID createdBy;
}
