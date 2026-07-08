package com.expensesharing.feature.settlement.application.usecase;

import com.expensesharing.TestDataFactory;
import com.expensesharing.common.exception.ForbiddenException;
import com.expensesharing.common.exception.NotFoundException;
import com.expensesharing.feature.house.domain.repository.HouseMemberRepository;
import com.expensesharing.feature.house.domain.repository.HouseRepository;
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
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class GetSettlementHistoryUseCaseTest {

    @Mock HouseRepository houseRepository;
    @Mock HouseMemberRepository houseMemberRepository;
    @Mock SettlementRepository settlementRepository;

    @InjectMocks GetSettlementHistoryUseCase getSettlementHistoryUseCase;

    @Test
    void getHistory_success_returnsSettlements() {
        var settlements = List.of(
                TestDataFactory.aSettlement(TestDataFactory.HOUSE_ID, TestDataFactory.USER_ID,
                        TestDataFactory.OTHER_USER_ID, new BigDecimal("300000"))
        );

        given(houseRepository.findById(TestDataFactory.HOUSE_ID)).willReturn(Optional.of(TestDataFactory.aHouse()));
        given(houseMemberRepository.existsByHouseIdAndUserId(TestDataFactory.HOUSE_ID, TestDataFactory.USER_ID)).willReturn(true);
        given(settlementRepository.findAllByHouseId(TestDataFactory.HOUSE_ID)).willReturn(settlements);

        List<Settlement> result = getSettlementHistoryUseCase.execute(TestDataFactory.HOUSE_ID, TestDataFactory.USER_ID);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getFromUserId()).isEqualTo(TestDataFactory.USER_ID);
        assertThat(result.get(0).getAmount()).isEqualByComparingTo("300000");
    }

    @Test
    void getHistory_houseNotFound_throwsException() {
        given(houseRepository.findById(TestDataFactory.HOUSE_ID)).willReturn(Optional.empty());

        assertThatThrownBy(() -> getSettlementHistoryUseCase.execute(TestDataFactory.HOUSE_ID, TestDataFactory.USER_ID))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void getHistory_notMember_throwsForbidden() {
        given(houseRepository.findById(TestDataFactory.HOUSE_ID)).willReturn(Optional.of(TestDataFactory.aHouse()));
        given(houseMemberRepository.existsByHouseIdAndUserId(TestDataFactory.HOUSE_ID, TestDataFactory.USER_ID)).willReturn(false);

        assertThatThrownBy(() -> getSettlementHistoryUseCase.execute(TestDataFactory.HOUSE_ID, TestDataFactory.USER_ID))
                .isInstanceOf(ForbiddenException.class);
    }
}
