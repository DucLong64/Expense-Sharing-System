package com.expensesharing.feature.expense.application.service;

import com.expensesharing.common.exception.BusinessException;
import com.expensesharing.feature.expense.application.dto.ParticipantShare;
import com.expensesharing.feature.expense.domain.model.ExpenseParticipant;
import com.expensesharing.feature.expense.domain.model.SplitType;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;

class SplitCalculatorTest {

    private final SplitCalculator calculator = new SplitCalculator();

    private final UUID expenseId = UUID.randomUUID();
    private final UUID user1 = UUID.randomUUID();
    private final UUID user2 = UUID.randomUUID();
    private final UUID user3 = UUID.randomUUID();

    // ==================== EQUAL ====================

    @Test
    void equal_threeUsers_splitEvenly() {
        var shares = List.of(
                new ParticipantShare(user1, null, null),
                new ParticipantShare(user2, null, null),
                new ParticipantShare(user3, null, null)
        );

        List<ExpenseParticipant> result = calculator.calculate(expenseId, new BigDecimal("900000"), SplitType.EQUAL, shares);

        assertThat(result).hasSize(3);
        BigDecimal total = result.stream().map(ExpenseParticipant::getShareAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
        assertThat(total).isEqualByComparingTo("900000");
    }

    @Test
    void equal_amountNotDivisible_remainderGoesToFirst() {
        var shares = List.of(
                new ParticipantShare(user1, null, null),
                new ParticipantShare(user2, null, null),
                new ParticipantShare(user3, null, null)
        );

        List<ExpenseParticipant> result = calculator.calculate(expenseId, new BigDecimal("100"), SplitType.EQUAL, shares);

        BigDecimal total = result.stream().map(ExpenseParticipant::getShareAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
        assertThat(total).isEqualByComparingTo("100");
        // 100 / 3 = 33.33, remainder 0.01 goes to first
        assertThat(result.get(0).getShareAmount()).isEqualByComparingTo("33.34");
        assertThat(result.get(1).getShareAmount()).isEqualByComparingTo("33.33");
        assertThat(result.get(2).getShareAmount()).isEqualByComparingTo("33.33");
    }

    @Test
    void equal_emptyParticipants_throwsException() {
        assertThatThrownBy(() -> calculator.calculate(expenseId, new BigDecimal("900000"), SplitType.EQUAL, List.of()))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Participants list must not be empty");
    }

    // ==================== FIXED ====================

    @Test
    void fixed_validAmounts_success() {
        var shares = List.of(
                new ParticipantShare(user1, new BigDecimal("500000"), null),
                new ParticipantShare(user2, new BigDecimal("300000"), null),
                new ParticipantShare(user3, new BigDecimal("100000"), null)
        );

        List<ExpenseParticipant> result = calculator.calculate(expenseId, new BigDecimal("900000"), SplitType.FIXED, shares);

        assertThat(result).hasSize(3);
        assertThat(result.get(0).getShareAmount()).isEqualByComparingTo("500000");
        assertThat(result.get(1).getShareAmount()).isEqualByComparingTo("300000");
        assertThat(result.get(2).getShareAmount()).isEqualByComparingTo("100000");
    }

    @Test
    void fixed_sumNotMatchTotal_throwsException() {
        var shares = List.of(
                new ParticipantShare(user1, new BigDecimal("500000"), null),
                new ParticipantShare(user2, new BigDecimal("300000"), null)
        );

        assertThatThrownBy(() -> calculator.calculate(expenseId, new BigDecimal("900000"), SplitType.FIXED, shares))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Sum of fixed amounts");
    }

    // ==================== PERCENTAGE ====================

    @Test
    void percentage_validPercentages_success() {
        var shares = List.of(
                new ParticipantShare(user1, null, new BigDecimal("50")),
                new ParticipantShare(user2, null, new BigDecimal("30")),
                new ParticipantShare(user3, null, new BigDecimal("20"))
        );

        List<ExpenseParticipant> result = calculator.calculate(expenseId, new BigDecimal("900000"), SplitType.PERCENTAGE, shares);

        assertThat(result).hasSize(3);
        BigDecimal total = result.stream().map(ExpenseParticipant::getShareAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
        assertThat(total).isEqualByComparingTo("900000");
        assertThat(result.get(0).getShareAmount()).isEqualByComparingTo("450000");
        assertThat(result.get(1).getShareAmount()).isEqualByComparingTo("270000");
        assertThat(result.get(2).getShareAmount()).isEqualByComparingTo("180000");
    }

    @Test
    void percentage_sumNot100_throwsException() {
        var shares = List.of(
                new ParticipantShare(user1, null, new BigDecimal("50")),
                new ParticipantShare(user2, null, new BigDecimal("30"))
        );

        assertThatThrownBy(() -> calculator.calculate(expenseId, new BigDecimal("900000"), SplitType.PERCENTAGE, shares))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Sum of percentages");
    }

    @Test
    void percentage_lastPersonAbsorbsRemainder() {
        var shares = List.of(
                new ParticipantShare(user1, null, new BigDecimal("33")),
                new ParticipantShare(user2, null, new BigDecimal("33")),
                new ParticipantShare(user3, null, new BigDecimal("34"))
        );

        List<ExpenseParticipant> result = calculator.calculate(expenseId, new BigDecimal("100"), SplitType.PERCENTAGE, shares);

        BigDecimal total = result.stream().map(ExpenseParticipant::getShareAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
        assertThat(total).isEqualByComparingTo("100");
    }
}
