package com.expensesharing.feature.expense.domain.model;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Getter
@Builder
public class Expense {

    private final UUID id;
    private final UUID houseId;
    private String title;
    private String description;
    private BigDecimal amount;
    private final UUID paidBy;
    private SplitType splitType;
    private LocalDate expenseDate;
    private String note;
    private final UUID createdBy;
    private final Instant createdAt;

    public void update(String title, String description, BigDecimal amount,
                       SplitType splitType, LocalDate expenseDate, String note) {
        this.title = title;
        this.description = description;
        this.amount = amount;
        this.splitType = splitType;
        this.expenseDate = expenseDate;
        this.note = note;
    }
}
