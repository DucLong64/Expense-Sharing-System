package com.expensesharing.feature.dashboard.domain.model;

import java.math.BigDecimal;

public record MonthlySpending(int year, int month, BigDecimal amount) {}
