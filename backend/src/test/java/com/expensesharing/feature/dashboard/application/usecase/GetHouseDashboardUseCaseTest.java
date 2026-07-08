package com.expensesharing.feature.dashboard.application.usecase;

import com.expensesharing.TestDataFactory;
import com.expensesharing.common.exception.ForbiddenException;
import com.expensesharing.common.exception.NotFoundException;
import com.expensesharing.feature.dashboard.application.service.DashboardCalculator;
import com.expensesharing.feature.dashboard.domain.model.HouseDashboard;
import com.expensesharing.feature.house.domain.repository.HouseMemberRepository;
import com.expensesharing.feature.house.domain.repository.HouseRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class GetHouseDashboardUseCaseTest {

    @Mock HouseRepository houseRepository;
    @Mock HouseMemberRepository houseMemberRepository;
    @Mock DashboardCalculator dashboardCalculator;

    @InjectMocks GetHouseDashboardUseCase getHouseDashboardUseCase;

    @Test
    void getDashboard_success_returnsCalculatedDashboard() {
        var expectedDashboard = new HouseDashboard(
                new BigDecimal("1200000"),
                new BigDecimal("200000"),
                List.of(),
                List.of(),
                List.of()
        );

        given(houseRepository.findById(TestDataFactory.HOUSE_ID)).willReturn(Optional.of(TestDataFactory.aHouse()));
        given(houseMemberRepository.existsByHouseIdAndUserId(TestDataFactory.HOUSE_ID, TestDataFactory.USER_ID))
                .willReturn(true);
        given(dashboardCalculator.calculate(TestDataFactory.HOUSE_ID)).willReturn(expectedDashboard);

        HouseDashboard result = getHouseDashboardUseCase.execute(TestDataFactory.HOUSE_ID, TestDataFactory.USER_ID);

        assertThat(result.totalSpending()).isEqualByComparingTo("1200000");
        assertThat(result.totalSettled()).isEqualByComparingTo("200000");
    }

    @Test
    void getDashboard_houseNotFound_throwsException() {
        given(houseRepository.findById(TestDataFactory.HOUSE_ID)).willReturn(Optional.empty());

        assertThatThrownBy(() -> getHouseDashboardUseCase.execute(TestDataFactory.HOUSE_ID, TestDataFactory.USER_ID))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void getDashboard_notMember_throwsForbidden() {
        given(houseRepository.findById(TestDataFactory.HOUSE_ID)).willReturn(Optional.of(TestDataFactory.aHouse()));
        given(houseMemberRepository.existsByHouseIdAndUserId(TestDataFactory.HOUSE_ID, TestDataFactory.USER_ID))
                .willReturn(false);

        assertThatThrownBy(() -> getHouseDashboardUseCase.execute(TestDataFactory.HOUSE_ID, TestDataFactory.USER_ID))
                .isInstanceOf(ForbiddenException.class);
    }
}
