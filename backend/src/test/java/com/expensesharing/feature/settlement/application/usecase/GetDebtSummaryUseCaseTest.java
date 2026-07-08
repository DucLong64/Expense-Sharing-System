package com.expensesharing.feature.settlement.application.usecase;

import com.expensesharing.TestDataFactory;
import com.expensesharing.common.exception.ForbiddenException;
import com.expensesharing.common.exception.NotFoundException;
import com.expensesharing.feature.house.domain.repository.HouseMemberRepository;
import com.expensesharing.feature.house.domain.repository.HouseRepository;
import com.expensesharing.feature.settlement.application.service.DebtCalculator;
import com.expensesharing.feature.settlement.domain.model.DebtSummary;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class GetDebtSummaryUseCaseTest {

    @Mock HouseRepository houseRepository;
    @Mock HouseMemberRepository houseMemberRepository;
    @Mock DebtCalculator debtCalculator;

    @InjectMocks GetDebtSummaryUseCase getDebtSummaryUseCase;

    @Test
    void getDebts_success_returnsCalculatedDebts() {
        var expectedDebts = List.of(new DebtSummary(TestDataFactory.OTHER_USER_ID, TestDataFactory.USER_ID, new BigDecimal("300000")));

        given(houseRepository.findById(TestDataFactory.HOUSE_ID)).willReturn(Optional.of(TestDataFactory.aHouse()));
        given(houseMemberRepository.existsByHouseIdAndUserId(TestDataFactory.HOUSE_ID, TestDataFactory.USER_ID)).willReturn(true);
        given(debtCalculator.calculate(TestDataFactory.HOUSE_ID)).willReturn(expectedDebts);

        var result = getDebtSummaryUseCase.execute(TestDataFactory.HOUSE_ID, TestDataFactory.USER_ID);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).amount()).isEqualByComparingTo("300000");
    }

    @Test
    void getDebts_houseNotFound_throwsException() {
        given(houseRepository.findById(TestDataFactory.HOUSE_ID)).willReturn(Optional.empty());

        assertThatThrownBy(() -> getDebtSummaryUseCase.execute(TestDataFactory.HOUSE_ID, TestDataFactory.USER_ID))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void getDebts_notMember_throwsForbidden() {
        given(houseRepository.findById(TestDataFactory.HOUSE_ID)).willReturn(Optional.of(TestDataFactory.aHouse()));
        given(houseMemberRepository.existsByHouseIdAndUserId(TestDataFactory.HOUSE_ID, TestDataFactory.USER_ID)).willReturn(false);

        assertThatThrownBy(() -> getDebtSummaryUseCase.execute(TestDataFactory.HOUSE_ID, TestDataFactory.USER_ID))
                .isInstanceOf(ForbiddenException.class);
    }
}
