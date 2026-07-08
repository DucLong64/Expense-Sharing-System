package com.expensesharing.feature.expense.infrastructure.persistence;

import com.expensesharing.common.persistence.SoftDeletableEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "expense_participants")
@SQLDelete(sql = "UPDATE expense_participants SET deleted_at = CURRENT_TIMESTAMP WHERE id = ?")
@SQLRestriction("deleted_at IS NULL")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExpenseParticipantJpaEntity extends SoftDeletableEntity {

    @Id
    @Column(nullable = false, updatable = false)
    private UUID id;

    @Column(name = "expense_id", nullable = false, updatable = false)
    private UUID expenseId;

    @Column(name = "user_id", nullable = false, updatable = false)
    private UUID userId;

    @Column(name = "share_amount", nullable = false, precision = 15, scale = 2)
    private BigDecimal shareAmount;

    @Column(name = "share_percentage", precision = 5, scale = 2)
    private BigDecimal sharePercentage;
}
