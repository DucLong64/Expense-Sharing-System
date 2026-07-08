package com.expensesharing.feature.dashboard.application.service;

import com.expensesharing.TestDataFactory;
import com.expensesharing.feature.dashboard.domain.model.HouseDashboard;
import com.expensesharing.feature.expense.domain.model.Expense;
import com.expensesharing.feature.expense.domain.model.SplitType;
import com.expensesharing.feature.expense.domain.repository.ExpenseParticipantRepository;
import com.expensesharing.feature.expense.domain.repository.ExpenseRepository;
import com.expensesharing.feature.settlement.application.service.DebtCalculator;
import com.expensesharing.feature.settlement.domain.model.DebtSummary;
import com.expensesharing.feature.settlement.domain.repository.SettlementRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class DashboardCalculatorTest {

    @Mock ExpenseRepository expenseRepository;
    @Mock ExpenseParticipantRepository participantRepository;
    @Mock SettlementRepository settlementRepository;
    @Mock DebtCalculator debtCalculator;

    @InjectMocks DashboardCalculator dashboardCalculator;

    private final UUID userA = UUID.randomUUID();
    private final UUID userB = UUID.randomUUID();

    @Test
    void calculate_emptyHouse_returnsZeroTotals() {
        given(expenseRepository.findAllByHouseId(TestDataFactory.HOUSE_ID)).willReturn(List.of());
        given(settlementRepository.findAllByHouseId(TestDataFactory.HOUSE_ID)).willReturn(List.of());
        given(debtCalculator.calculate(TestDataFactory.HOUSE_ID)).willReturn(List.of());

        HouseDashboard result = dashboardCalculator.calculate(TestDataFactory.HOUSE_ID);

        assertThat(result.totalSpending()).isEqualByComparingTo("0");
        assertThat(result.totalSettled()).isEqualByComparingTo("0");
        assertThat(result.spendingByMonth()).isEmpty();
        assertThat(result.spendingByMember()).isEmpty();
        assertThat(result.currentDebts()).isEmpty();
    }

    @Test
    void calculate_withExpensesAndSettlements_returnsAggregatedDashboard() {
        UUID expenseJulyId = UUID.randomUUID();
        UUID expenseAugustId = UUID.randomUUID();

        Expense expenseJuly = Expense.builder()
                .id(expenseJulyId)
                .houseId(TestDataFactory.HOUSE_ID)
                .title("Tiền điện")
                .amount(new BigDecimal("900000"))
                .paidBy(userA)
                .splitType(SplitType.EQUAL)
                .expenseDate(LocalDate.of(2026, 7, 15))
                .createdBy(userA)
                .createdAt(Instant.now())
                .build();

        Expense expenseAugust = Expense.builder()
                .id(expenseAugustId)
                .houseId(TestDataFactory.HOUSE_ID)
                .title("Tiền nước")
                .amount(new BigDecimal("300000"))
                .paidBy(userB)
                .splitType(SplitType.EQUAL)
                .expenseDate(LocalDate.of(2026, 8, 1))
                .createdBy(userB)
                .createdAt(Instant.now())
                .build();

        given(expenseRepository.findAllByHouseId(TestDataFactory.HOUSE_ID))
                .willReturn(List.of(expenseJuly, expenseAugust));
        given(participantRepository.findAllByExpenseId(expenseJulyId)).willReturn(List.of(
                TestDataFactory.aParticipant(expenseJulyId, userA, new BigDecimal("450000")),
                TestDataFactory.aParticipant(expenseJulyId, userB, new BigDecimal("450000"))
        ));
        given(participantRepository.findAllByExpenseId(expenseAugustId)).willReturn(List.of(
                TestDataFactory.aParticipant(expenseAugustId, userA, new BigDecimal("150000")),
                TestDataFactory.aParticipant(expenseAugustId, userB, new BigDecimal("150000"))
        ));
        given(settlementRepository.findAllByHouseId(TestDataFactory.HOUSE_ID)).willReturn(List.of(
                TestDataFactory.aSettlement(TestDataFactory.HOUSE_ID, userB, userA, new BigDecimal("200000"))
        ));
        given(debtCalculator.calculate(TestDataFactory.HOUSE_ID)).willReturn(List.of(
                new DebtSummary(userB, userA, new BigDecimal("400000"))
        ));

        HouseDashboard result = dashboardCalculator.calculate(TestDataFactory.HOUSE_ID);

        assertThat(result.totalSpending()).isEqualByComparingTo("1200000");
        assertThat(result.totalSettled()).isEqualByComparingTo("200000");
        assertThat(result.spendingByMonth()).hasSize(2);
        assertThat(result.spendingByMonth().getFirst().year()).isEqualTo(2026);
        assertThat(result.spendingByMonth().getFirst().month()).isEqualTo(8);
        assertThat(result.spendingByMonth().getFirst().amount()).isEqualByComparingTo("300000");
        assertThat(result.spendingByMember()).hasSize(2);
        assertThat(result.spendingByMember().getFirst().userId()).isEqualTo(userA);
        assertThat(result.spendingByMember().getFirst().amount()).isEqualByComparingTo("600000");
        assertThat(result.currentDebts()).hasSize(1);
        assertThat(result.currentDebts().getFirst().amount()).isEqualByComparingTo("400000");
    }
}
