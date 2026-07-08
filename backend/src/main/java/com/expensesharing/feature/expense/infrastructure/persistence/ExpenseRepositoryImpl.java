package com.expensesharing.feature.expense.infrastructure.persistence;

import com.expensesharing.feature.expense.domain.model.Expense;
import com.expensesharing.feature.expense.domain.repository.ExpenseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class ExpenseRepositoryImpl implements ExpenseRepository {

    private final ExpenseJpaRepository jpaRepository;

    @Override
    public Expense save(Expense expense) {
        jpaRepository.save(toEntity(expense));
        return expense;
    }

    @Override
    public Optional<Expense> findById(UUID id) {
        return jpaRepository.findById(id).map(this::toDomain);
    }

    @Override
    public List<Expense> findAllByHouseId(UUID houseId) {
        return jpaRepository.findAllByHouseIdOrderByExpenseDateDesc(houseId)
                .stream().map(this::toDomain).toList();
    }

    @Override
    public void deleteById(UUID id) {
        jpaRepository.deleteById(id);
    }

    private ExpenseJpaEntity toEntity(Expense expense) {
        return ExpenseJpaEntity.builder()
                .id(expense.getId())
                .houseId(expense.getHouseId())
                .title(expense.getTitle())
                .description(expense.getDescription())
                .amount(expense.getAmount())
                .paidBy(expense.getPaidBy())
                .splitType(expense.getSplitType())
                .expenseDate(expense.getExpenseDate())
                .note(expense.getNote())
                .createdBy(expense.getCreatedBy())
                .createdAt(expense.getCreatedAt())
                .build();
    }

    private Expense toDomain(ExpenseJpaEntity entity) {
        return Expense.builder()
                .id(entity.getId())
                .houseId(entity.getHouseId())
                .title(entity.getTitle())
                .description(entity.getDescription())
                .amount(entity.getAmount())
                .paidBy(entity.getPaidBy())
                .splitType(entity.getSplitType())
                .expenseDate(entity.getExpenseDate())
                .note(entity.getNote())
                .createdBy(entity.getCreatedBy())
                .createdAt(entity.getCreatedAt())
                .build();
    }
}
