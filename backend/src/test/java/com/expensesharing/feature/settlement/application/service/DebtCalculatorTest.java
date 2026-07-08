package com.expensesharing.feature.settlement.application.service;

import com.expensesharing.TestDataFactory;
import com.expensesharing.feature.expense.domain.model.Expense;
import com.expensesharing.feature.expense.domain.repository.ExpenseParticipantRepository;
import com.expensesharing.feature.expense.domain.repository.ExpenseRepository;
import com.expensesharing.feature.settlement.domain.model.DebtSummary;
import com.expensesharing.feature.settlement.domain.repository.SettlementRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class DebtCalculatorTest {

    @Mock ExpenseRepository expenseRepository;
    @Mock ExpenseParticipantRepository participantRepository;
    @Mock SettlementRepository settlementRepository;

    @InjectMocks DebtCalculator debtCalculator;

    private final UUID longId = UUID.randomUUID();
    private final UUID namId = UUID.randomUUID();
    private final UUID huyId = UUID.randomUUID();

    @Test
    void calculate_noExpenses_returnsEmpty() {
        given(expenseRepository.findAllByHouseId(TestDataFactory.HOUSE_ID)).willReturn(List.of());
        given(settlementRepository.findAllByHouseId(TestDataFactory.HOUSE_ID)).willReturn(List.of());

        List<DebtSummary> result = debtCalculator.calculate(TestDataFactory.HOUSE_ID);

        assertThat(result).isEmpty();
    }

    @Test
    void calculate_simpleDebt_correctResult() {
        // Long trả 900k, chia đều 3 người => Nam nợ Long 300k, Huy nợ Long 300k
        Expense expense = TestDataFactory.anExpense(TestDataFactory.HOUSE_ID, longId, longId, new BigDecimal("900000"));

        given(expenseRepository.findAllByHouseId(TestDataFactory.HOUSE_ID)).willReturn(List.of(expense));
        given(participantRepository.findAllByExpenseId(expense.getId())).willReturn(List.of(
                TestDataFactory.aParticipant(expense.getId(), longId, new BigDecimal("300000")),
                TestDataFactory.aParticipant(expense.getId(), namId, new BigDecimal("300000")),
                TestDataFactory.aParticipant(expense.getId(), huyId, new BigDecimal("300000"))
        ));
        given(settlementRepository.findAllByHouseId(TestDataFactory.HOUSE_ID)).willReturn(List.of());

        List<DebtSummary> result = debtCalculator.calculate(TestDataFactory.HOUSE_ID);

        assertThat(result).hasSize(2);
        BigDecimal totalDebt = result.stream().map(DebtSummary::amount).reduce(BigDecimal.ZERO, BigDecimal::add);
        assertThat(totalDebt).isEqualByComparingTo("600000");
        result.forEach(d -> assertThat(d.toUserId()).isEqualTo(longId));
    }

    @Test
    void calculate_multipleExpenses_optimizesTransactions() {
        // Long trả 900k (điện), Nam trả 600k (nước), chia đều 3 người
        // Net: Long = +900 - 300 - 200 = +400, Nam = +600 - 300 - 200 = +100, Huy = -300 - 200 = -500
        // => Huy trả Long 400k, Huy trả Nam 100k (2 giao dịch thay vì 4)
        Expense expense1 = TestDataFactory.anExpense(TestDataFactory.HOUSE_ID, longId, longId, new BigDecimal("900000"));
        Expense expense2 = TestDataFactory.anExpense(TestDataFactory.HOUSE_ID, namId, namId, new BigDecimal("600000"));

        given(expenseRepository.findAllByHouseId(TestDataFactory.HOUSE_ID)).willReturn(List.of(expense1, expense2));
        given(participantRepository.findAllByExpenseId(expense1.getId())).willReturn(List.of(
                TestDataFactory.aParticipant(expense1.getId(), longId, new BigDecimal("300000")),
                TestDataFactory.aParticipant(expense1.getId(), namId, new BigDecimal("300000")),
                TestDataFactory.aParticipant(expense1.getId(), huyId, new BigDecimal("300000"))
        ));
        given(participantRepository.findAllByExpenseId(expense2.getId())).willReturn(List.of(
                TestDataFactory.aParticipant(expense2.getId(), longId, new BigDecimal("200000")),
                TestDataFactory.aParticipant(expense2.getId(), namId, new BigDecimal("200000")),
                TestDataFactory.aParticipant(expense2.getId(), huyId, new BigDecimal("200000"))
        ));
        given(settlementRepository.findAllByHouseId(TestDataFactory.HOUSE_ID)).willReturn(List.of());

        List<DebtSummary> result = debtCalculator.calculate(TestDataFactory.HOUSE_ID);

        assertThat(result).hasSize(2);
        assertThat(result).allMatch(d -> d.fromUserId().equals(huyId));
        BigDecimal totalDebt = result.stream().map(DebtSummary::amount).reduce(BigDecimal.ZERO, BigDecimal::add);
        assertThat(totalDebt).isEqualByComparingTo("500000");
    }

    @Test
    void calculate_withExistingSettlement_reducesDebt() {
        // Long trả 900k, chia đều => Nam nợ 300k, Huy nợ 300k
        // Nam đã trả Long 300k => chỉ còn Huy nợ Long 300k
        Expense expense = TestDataFactory.anExpense(TestDataFactory.HOUSE_ID, longId, longId, new BigDecimal("900000"));

        given(expenseRepository.findAllByHouseId(TestDataFactory.HOUSE_ID)).willReturn(List.of(expense));
        given(participantRepository.findAllByExpenseId(expense.getId())).willReturn(List.of(
                TestDataFactory.aParticipant(expense.getId(), longId, new BigDecimal("300000")),
                TestDataFactory.aParticipant(expense.getId(), namId, new BigDecimal("300000")),
                TestDataFactory.aParticipant(expense.getId(), huyId, new BigDecimal("300000"))
        ));
        given(settlementRepository.findAllByHouseId(TestDataFactory.HOUSE_ID)).willReturn(List.of(
                TestDataFactory.aSettlement(TestDataFactory.HOUSE_ID, namId, longId, new BigDecimal("300000"))
        ));

        List<DebtSummary> result = debtCalculator.calculate(TestDataFactory.HOUSE_ID);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).fromUserId()).isEqualTo(huyId);
        assertThat(result.get(0).toUserId()).isEqualTo(longId);
        assertThat(result.get(0).amount()).isEqualByComparingTo("300000");
    }

    @Test
    void calculate_allSettled_returnsEmpty() {
        Expense expense = TestDataFactory.anExpense(TestDataFactory.HOUSE_ID, longId, longId, new BigDecimal("600000"));

        given(expenseRepository.findAllByHouseId(TestDataFactory.HOUSE_ID)).willReturn(List.of(expense));
        given(participantRepository.findAllByExpenseId(expense.getId())).willReturn(List.of(
                TestDataFactory.aParticipant(expense.getId(), longId, new BigDecimal("300000")),
                TestDataFactory.aParticipant(expense.getId(), namId, new BigDecimal("300000"))
        ));
        given(settlementRepository.findAllByHouseId(TestDataFactory.HOUSE_ID)).willReturn(List.of(
                TestDataFactory.aSettlement(TestDataFactory.HOUSE_ID, namId, longId, new BigDecimal("300000"))
        ));

        List<DebtSummary> result = debtCalculator.calculate(TestDataFactory.HOUSE_ID);

        assertThat(result).isEmpty();
    }
}
