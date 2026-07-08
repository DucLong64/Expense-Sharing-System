package com.expensesharing.feature.expense.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface ExpenseParticipantJpaRepository extends JpaRepository<ExpenseParticipantJpaEntity, UUID> {

    List<ExpenseParticipantJpaEntity> findAllByExpenseId(UUID expenseId);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
            UPDATE ExpenseParticipantJpaEntity p
            SET p.deletedAt = CURRENT_TIMESTAMP
            WHERE p.expenseId = :expenseId AND p.deletedAt IS NULL
            """)
    void softDeleteAllByExpenseId(@Param("expenseId") UUID expenseId);
}
