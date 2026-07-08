package com.expensesharing.feature.settlement.application.usecase;

import com.expensesharing.TestDataFactory;
import com.expensesharing.common.exception.BusinessException;
import com.expensesharing.common.exception.ForbiddenException;
import com.expensesharing.common.exception.NotFoundException;
import com.expensesharing.feature.activity.application.service.ActivityLogService;
import com.expensesharing.feature.house.domain.model.HouseRole;
import com.expensesharing.feature.house.domain.repository.HouseMemberRepository;
import com.expensesharing.feature.house.domain.repository.HouseRepository;
import com.expensesharing.feature.settlement.application.dto.SettleDebtCommand;
import com.expensesharing.feature.settlement.application.service.DebtCalculator;
import com.expensesharing.feature.settlement.domain.model.DebtSummary;
import com.expensesharing.feature.settlement.domain.model.Settlement;
import com.expensesharing.feature.settlement.domain.repository.SettlementRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class SettleDebtUseCaseTest {

    @Mock ActivityLogService activityLogService;
    @Mock DebtCalculator debtCalculator;
    @Mock HouseRepository houseRepository;
    @Mock HouseMemberRepository houseMemberRepository;
    @Mock SettlementRepository settlementRepository;

    @InjectMocks SettleDebtUseCase settleDebtUseCase;

    private void stubValidSettlementContext() {
        given(houseRepository.findById(TestDataFactory.HOUSE_ID)).willReturn(Optional.of(TestDataFactory.aHouse()));
        given(houseMemberRepository.findByHouseIdAndUserId(TestDataFactory.HOUSE_ID, TestDataFactory.USER_ID))
                .willReturn(Optional.of(TestDataFactory.aMember(TestDataFactory.HOUSE_ID, TestDataFactory.USER_ID, HouseRole.MEMBER)));
        given(houseMemberRepository.existsByHouseIdAndUserId(TestDataFactory.HOUSE_ID, TestDataFactory.OTHER_USER_ID))
                .willReturn(true);
        given(debtCalculator.calculate(TestDataFactory.HOUSE_ID)).willReturn(List.of(
                new DebtSummary(TestDataFactory.USER_ID, TestDataFactory.OTHER_USER_ID, new BigDecimal("300000"))
        ));
        given(settlementRepository.save(any())).willAnswer(inv -> inv.getArgument(0));
    }

    @Test
    void settle_success() {
        var command = new SettleDebtCommand(
                TestDataFactory.HOUSE_ID, TestDataFactory.USER_ID,
                TestDataFactory.OTHER_USER_ID, new BigDecimal("300000"), "Trả tiền điện");

        stubValidSettlementContext();

        Settlement result = settleDebtUseCase.execute(command);

        assertThat(result.getFromUserId()).isEqualTo(TestDataFactory.USER_ID);
        assertThat(result.getToUserId()).isEqualTo(TestDataFactory.OTHER_USER_ID);
        assertThat(result.getAmount()).isEqualByComparingTo("300000");
        assertThat(result.getNote()).isEqualTo("Trả tiền điện");
    }

    @Test
    void settle_partialPayment_success() {
        var command = new SettleDebtCommand(
                TestDataFactory.HOUSE_ID, TestDataFactory.USER_ID,
                TestDataFactory.OTHER_USER_ID, new BigDecimal("150000"), null);

        stubValidSettlementContext();

        Settlement result = settleDebtUseCase.execute(command);

        assertThat(result.getAmount()).isEqualByComparingTo("150000");
    }

    @Test
    void settle_amountExceedsDebt_throwsBusinessException() {
        var command = new SettleDebtCommand(
                TestDataFactory.HOUSE_ID, TestDataFactory.USER_ID,
                TestDataFactory.OTHER_USER_ID, new BigDecimal("300001"), null);

        stubValidSettlementContext();

        assertThatThrownBy(() -> settleDebtUseCase.execute(command))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("exceeds outstanding debt");
    }

    @Test
    void settle_noOutstandingDebt_throwsBusinessException() {
        var command = new SettleDebtCommand(
                TestDataFactory.HOUSE_ID, TestDataFactory.USER_ID,
                TestDataFactory.OTHER_USER_ID, new BigDecimal("300000"), null);

        given(houseRepository.findById(TestDataFactory.HOUSE_ID)).willReturn(Optional.of(TestDataFactory.aHouse()));
        given(houseMemberRepository.findByHouseIdAndUserId(TestDataFactory.HOUSE_ID, TestDataFactory.USER_ID))
                .willReturn(Optional.of(TestDataFactory.aMember(TestDataFactory.HOUSE_ID, TestDataFactory.USER_ID, HouseRole.MEMBER)));
        given(houseMemberRepository.existsByHouseIdAndUserId(TestDataFactory.HOUSE_ID, TestDataFactory.OTHER_USER_ID))
                .willReturn(true);
        given(debtCalculator.calculate(TestDataFactory.HOUSE_ID)).willReturn(List.of());

        assertThatThrownBy(() -> settleDebtUseCase.execute(command))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("do not owe");
    }

    @Test
    void settle_selfSettlement_throwsBusinessException() {
        var command = new SettleDebtCommand(
                TestDataFactory.HOUSE_ID, TestDataFactory.USER_ID,
                TestDataFactory.USER_ID, new BigDecimal("300000"), null);

        given(houseRepository.findById(TestDataFactory.HOUSE_ID)).willReturn(Optional.of(TestDataFactory.aHouse()));
        given(houseMemberRepository.findByHouseIdAndUserId(TestDataFactory.HOUSE_ID, TestDataFactory.USER_ID))
                .willReturn(Optional.of(TestDataFactory.aMember(TestDataFactory.HOUSE_ID, TestDataFactory.USER_ID, HouseRole.MEMBER)));
        given(houseMemberRepository.existsByHouseIdAndUserId(TestDataFactory.HOUSE_ID, TestDataFactory.USER_ID))
                .willReturn(true);

        assertThatThrownBy(() -> settleDebtUseCase.execute(command))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("yourself");
    }

    @Test
    void settle_houseNotFound_throwsException() {
        var command = new SettleDebtCommand(
                TestDataFactory.HOUSE_ID, TestDataFactory.USER_ID,
                TestDataFactory.OTHER_USER_ID, new BigDecimal("300000"), null);

        given(houseRepository.findById(TestDataFactory.HOUSE_ID)).willReturn(Optional.empty());

        assertThatThrownBy(() -> settleDebtUseCase.execute(command))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void settle_requesterNotMember_throwsForbidden() {
        var command = new SettleDebtCommand(
                TestDataFactory.HOUSE_ID, TestDataFactory.USER_ID,
                TestDataFactory.OTHER_USER_ID, new BigDecimal("300000"), null);

        given(houseRepository.findById(TestDataFactory.HOUSE_ID)).willReturn(Optional.of(TestDataFactory.aHouse()));
        given(houseMemberRepository.findByHouseIdAndUserId(TestDataFactory.HOUSE_ID, TestDataFactory.USER_ID))
                .willReturn(Optional.empty());

        assertThatThrownBy(() -> settleDebtUseCase.execute(command))
                .isInstanceOf(ForbiddenException.class);
    }

    @Test
    void settle_asViewer_throwsForbidden() {
        var command = new SettleDebtCommand(
                TestDataFactory.HOUSE_ID, TestDataFactory.USER_ID,
                TestDataFactory.OTHER_USER_ID, new BigDecimal("300000"), null);

        given(houseRepository.findById(TestDataFactory.HOUSE_ID)).willReturn(Optional.of(TestDataFactory.aHouse()));
        given(houseMemberRepository.findByHouseIdAndUserId(TestDataFactory.HOUSE_ID, TestDataFactory.USER_ID))
                .willReturn(Optional.of(TestDataFactory.aMember(TestDataFactory.HOUSE_ID, TestDataFactory.USER_ID, HouseRole.VIEWER)));

        assertThatThrownBy(() -> settleDebtUseCase.execute(command))
                .isInstanceOf(ForbiddenException.class);
    }

    @Test
    void settle_targetNotMember_throwsException() {
        var command = new SettleDebtCommand(
                TestDataFactory.HOUSE_ID, TestDataFactory.USER_ID,
                TestDataFactory.OTHER_USER_ID, new BigDecimal("300000"), null);

        given(houseRepository.findById(TestDataFactory.HOUSE_ID)).willReturn(Optional.of(TestDataFactory.aHouse()));
        given(houseMemberRepository.findByHouseIdAndUserId(TestDataFactory.HOUSE_ID, TestDataFactory.USER_ID))
                .willReturn(Optional.of(TestDataFactory.aMember(TestDataFactory.HOUSE_ID, TestDataFactory.USER_ID, HouseRole.MEMBER)));
        given(houseMemberRepository.existsByHouseIdAndUserId(TestDataFactory.HOUSE_ID, TestDataFactory.OTHER_USER_ID))
                .willReturn(false);

        assertThatThrownBy(() -> settleDebtUseCase.execute(command))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("not a member");
    }
}
