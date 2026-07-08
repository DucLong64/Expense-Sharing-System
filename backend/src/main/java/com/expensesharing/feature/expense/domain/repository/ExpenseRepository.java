package com.expensesharing.feature.expense.domain.repository;

import com.expensesharing.feature.expense.domain.model.Expense;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ExpenseRepository {

    Expense save(Expense expense);

    Optional<Expense> findById(UUID id);

    List<Expense> findAllByHouseId(UUID houseId);

    void deleteById(UUID id);
}
