package com.expensesharing.feature.expense.application.dto;

import com.expensesharing.feature.expense.domain.model.Expense;
import com.expensesharing.feature.expense.domain.model.ExpenseParticipant;

import java.util.List;

public record ExpenseResult(Expense expense, List<ExpenseParticipant> participants) {}
