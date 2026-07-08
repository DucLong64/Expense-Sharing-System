package com.expensesharing.feature.expense.infrastructure.persistence;

import com.expensesharing.feature.expense.domain.model.ExpenseParticipant;
import com.expensesharing.feature.expense.domain.repository.ExpenseParticipantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class ExpenseParticipantRepositoryImpl implements ExpenseParticipantRepository {

    private final ExpenseParticipantJpaRepository jpaRepository;

    @Override
    public List<ExpenseParticipant> saveAll(List<ExpenseParticipant> participants) {
        jpaRepository.saveAll(participants.stream().map(this::toEntity).toList());
        return participants;
    }

    @Override
    public List<ExpenseParticipant> findAllByExpenseId(UUID expenseId) {
        return jpaRepository.findAllByExpenseId(expenseId).stream().map(this::toDomain).toList();
    }

    @Override
    public void deleteAllByExpenseId(UUID expenseId) {
        jpaRepository.softDeleteAllByExpenseId(expenseId);
    }

    private ExpenseParticipantJpaEntity toEntity(ExpenseParticipant p) {
        return ExpenseParticipantJpaEntity.builder()
                .id(p.getId())
                .expenseId(p.getExpenseId())
                .userId(p.getUserId())
                .shareAmount(p.getShareAmount())
                .sharePercentage(p.getSharePercentage())
                .build();
    }

    private ExpenseParticipant toDomain(ExpenseParticipantJpaEntity entity) {
        return ExpenseParticipant.builder()
                .id(entity.getId())
                .expenseId(entity.getExpenseId())
                .userId(entity.getUserId())
                .shareAmount(entity.getShareAmount())
                .sharePercentage(entity.getSharePercentage())
                .build();
    }
}
