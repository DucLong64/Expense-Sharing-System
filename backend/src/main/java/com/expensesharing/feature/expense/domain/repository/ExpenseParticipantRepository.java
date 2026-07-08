package com.expensesharing.feature.expense.domain.repository;

import com.expensesharing.feature.expense.domain.model.ExpenseParticipant;

import java.util.List;
import java.util.UUID;

public interface ExpenseParticipantRepository {

    List<ExpenseParticipant> saveAll(List<ExpenseParticipant> participants);

    List<ExpenseParticipant> findAllByExpenseId(UUID expenseId);

    void deleteAllByExpenseId(UUID expenseId);
}
