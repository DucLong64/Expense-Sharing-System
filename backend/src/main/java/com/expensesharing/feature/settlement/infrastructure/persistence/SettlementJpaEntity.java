package com.expensesharing.feature.settlement.infrastructure.persistence;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "settlements")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SettlementJpaEntity {

    @Id
    @Column(nullable = false, updatable = false)
    private UUID id;

    @Column(name = "house_id", nullable = false, updatable = false)
    private UUID houseId;

    @Column(name = "from_user_id", nullable = false, updatable = false)
    private UUID fromUserId;

    @Column(name = "to_user_id", nullable = false, updatable = false)
    private UUID toUserId;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal amount;

    @Column(length = 500)
    private String note;

    @Column(name = "settled_at", nullable = false, updatable = false)
    private Instant settledAt;

    @Column(name = "created_by", nullable = false, updatable = false)
    private UUID createdBy;
}
