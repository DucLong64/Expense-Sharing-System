package com.expensesharing.feature.house.application.usecase;

import com.expensesharing.TestDataFactory;
import com.expensesharing.feature.activity.application.service.ActivityLogService;
import com.expensesharing.feature.house.application.dto.CreateHouseCommand;
import com.expensesharing.feature.house.domain.model.House;
import com.expensesharing.feature.house.domain.model.HouseRole;
import com.expensesharing.feature.house.domain.repository.HouseMemberRepository;
import com.expensesharing.feature.house.domain.repository.HouseRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class CreateHouseUseCaseTest {

    @Mock ActivityLogService activityLogService;
    @Mock HouseRepository houseRepository;
    @Mock HouseMemberRepository houseMemberRepository;

    @InjectMocks CreateHouseUseCase createHouseUseCase;

    @Test
    void createHouse_success_returnsHouseAndAssignsOwner() {
        var command = new CreateHouseCommand("My House", "Description", TestDataFactory.USER_ID);
        given(houseRepository.save(any())).willAnswer(inv -> inv.getArgument(0));

        House result = createHouseUseCase.execute(command);

        assertThat(result.getName()).isEqualTo("My House");
        assertThat(result.getCreatedBy()).isEqualTo(TestDataFactory.USER_ID);

        var memberCaptor = ArgumentCaptor.forClass(com.expensesharing.feature.house.domain.model.HouseMember.class);
        then(houseMemberRepository).should().save(memberCaptor.capture());
        assertThat(memberCaptor.getValue().getRole()).isEqualTo(HouseRole.OWNER);
        assertThat(memberCaptor.getValue().getUserId()).isEqualTo(TestDataFactory.USER_ID);
    }
}
