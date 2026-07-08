package com.expensesharing.feature.house.infrastructure.persistence;

import com.expensesharing.common.persistence.SoftDeletableEntity;
import com.expensesharing.feature.house.domain.model.HouseRole;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "house_members")
@SQLDelete(sql = "UPDATE house_members SET deleted_at = CURRENT_TIMESTAMP WHERE id = ?")
@SQLRestriction("deleted_at IS NULL")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HouseMemberJpaEntity extends SoftDeletableEntity {

    @Id
    @Column(nullable = false, updatable = false)
    private UUID id;

    @Column(name = "house_id", nullable = false, updatable = false)
    private UUID houseId;

    @Column(name = "user_id", nullable = false, updatable = false)
    private UUID userId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private HouseRole role;

    @Column(name = "joined_at", nullable = false, updatable = false)
    private Instant joinedAt;
}
