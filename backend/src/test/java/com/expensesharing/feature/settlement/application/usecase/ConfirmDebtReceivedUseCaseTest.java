package com.expensesharing.feature.settlement.application.usecase;

import com.expensesharing.TestDataFactory;
import com.expensesharing.common.exception.BusinessException;
import com.expensesharing.common.exception.ForbiddenException;
import com.expensesharing.common.exception.NotFoundException;
import com.expensesharing.feature.activity.application.service.ActivityLogService;
import com.expensesharing.feature.house.domain.model.HouseRole;
import com.expensesharing.feature.house.domain.repository.HouseMemberRepository;
import com.expensesharing.feature.house.domain.repository.HouseRepository;
import com.expensesharing.feature.notification.application.service.NotificationService;
import com.expensesharing.feature.settlement.application.dto.ConfirmDebtReceivedCommand;
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
class ConfirmDebtReceivedUseCaseTest {

    @Mock ActivityLogService activityLogService;
    @Mock NotificationService notificationService;
    @Mock DebtCalculator debtCalculator;
    @Mock HouseRepository houseRepository;
    @Mock HouseMemberRepository houseMemberRepository;
    @Mock SettlementRepository settlementRepository;

    @InjectMocks ConfirmDebtReceivedUseCase confirmDebtReceivedUseCase;

    private void stubValidContext() {
        given(houseRepository.findById(TestDataFactory.HOUSE_ID)).willReturn(Optional.of(TestDataFactory.aHouse()));
        given(houseMemberRepository.findByHouseIdAndUserId(TestDataFactory.HOUSE_ID, TestDataFactory.OTHER_USER_ID))
                .willReturn(Optional.of(TestDataFactory.aMember(
                        TestDataFactory.HOUSE_ID, TestDataFactory.OTHER_USER_ID, HouseRole.MEMBER)));
        given(houseMemberRepository.existsByHouseIdAndUserId(TestDataFactory.HOUSE_ID, TestDataFactory.USER_ID))
                .willReturn(true);
        given(debtCalculator.calculate(TestDataFactory.HOUSE_ID)).willReturn(List.of(
                new DebtSummary(TestDataFactory.USER_ID, TestDataFactory.OTHER_USER_ID, new BigDecimal("300000"))
        ));
        given(settlementRepository.save(any())).willAnswer(inv -> inv.getArgument(0));
    }

    @Test
    void confirmReceived_success() {
        var command = new ConfirmDebtReceivedCommand(
                TestDataFactory.HOUSE_ID, TestDataFactory.OTHER_USER_ID,
                TestDataFactory.USER_ID, new BigDecimal("300000"), "Đã nhận tiền mặt");

        stubValidContext();

        Settlement result = confirmDebtReceivedUseCase.execute(command);

        assertThat(result.getFromUserId()).isEqualTo(TestDataFactory.USER_ID);
        assertThat(result.getToUserId()).isEqualTo(TestDataFactory.OTHER_USER_ID);
        assertThat(result.getCreatedBy()).isEqualTo(TestDataFactory.OTHER_USER_ID);
        assertThat(result.getAmount()).isEqualByComparingTo("300000");
    }

    @Test
    void confirmReceived_partial_success() {
        var command = new ConfirmDebtReceivedCommand(
                TestDataFactory.HOUSE_ID, TestDataFactory.OTHER_USER_ID,
                TestDataFactory.USER_ID, new BigDecimal("150000"), null);

        stubValidContext();

        Settlement result = confirmDebtReceivedUseCase.execute(command);

        assertThat(result.getAmount()).isEqualByComparingTo("150000");
    }

    @Test
    void confirmReceived_amountExceedsDebt_throwsBusinessException() {
        var command = new ConfirmDebtReceivedCommand(
                TestDataFactory.HOUSE_ID, TestDataFactory.OTHER_USER_ID,
                TestDataFactory.USER_ID, new BigDecimal("300001"), null);

        stubValidContext();

        assertThatThrownBy(() -> confirmDebtReceivedUseCase.execute(command))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("exceeds outstanding debt");
    }

    @Test
    void confirmReceived_noOutstandingDebt_throwsBusinessException() {
        var command = new ConfirmDebtReceivedCommand(
                TestDataFactory.HOUSE_ID, TestDataFactory.OTHER_USER_ID,
                TestDataFactory.USER_ID, new BigDecimal("300000"), null);

        given(houseRepository.findById(TestDataFactory.HOUSE_ID)).willReturn(Optional.of(TestDataFactory.aHouse()));
        given(houseMemberRepository.findByHouseIdAndUserId(TestDataFactory.HOUSE_ID, TestDataFactory.OTHER_USER_ID))
                .willReturn(Optional.of(TestDataFactory.aMember(
                        TestDataFactory.HOUSE_ID, TestDataFactory.OTHER_USER_ID, HouseRole.MEMBER)));
        given(houseMemberRepository.existsByHouseIdAndUserId(TestDataFactory.HOUSE_ID, TestDataFactory.USER_ID))
                .willReturn(true);
        given(debtCalculator.calculate(TestDataFactory.HOUSE_ID)).willReturn(List.of());

        assertThatThrownBy(() -> confirmDebtReceivedUseCase.execute(command))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("does not owe you");
    }

    @Test
    void confirmReceived_asViewer_throwsForbidden() {
        var command = new ConfirmDebtReceivedCommand(
                TestDataFactory.HOUSE_ID, TestDataFactory.OTHER_USER_ID,
                TestDataFactory.USER_ID, new BigDecimal("300000"), null);

        given(houseRepository.findById(TestDataFactory.HOUSE_ID)).willReturn(Optional.of(TestDataFactory.aHouse()));
        given(houseMemberRepository.findByHouseIdAndUserId(TestDataFactory.HOUSE_ID, TestDataFactory.OTHER_USER_ID))
                .willReturn(Optional.of(TestDataFactory.aMember(
                        TestDataFactory.HOUSE_ID, TestDataFactory.OTHER_USER_ID, HouseRole.VIEWER)));

        assertThatThrownBy(() -> confirmDebtReceivedUseCase.execute(command))
                .isInstanceOf(ForbiddenException.class);
    }
}
