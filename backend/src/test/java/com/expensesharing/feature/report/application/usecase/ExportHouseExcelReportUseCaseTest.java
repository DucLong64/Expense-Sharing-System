package com.expensesharing.feature.report.application.usecase;

import com.expensesharing.TestDataFactory;
import com.expensesharing.common.exception.ForbiddenException;
import com.expensesharing.common.port.UserProfilePort;
import com.expensesharing.feature.dashboard.application.usecase.GetHouseDashboardUseCase;
import com.expensesharing.feature.dashboard.domain.model.HouseDashboard;
import com.expensesharing.feature.expense.application.usecase.GetExpenseUseCase;
import com.expensesharing.feature.house.application.usecase.GetHouseUseCase;
import com.expensesharing.feature.house.domain.model.House;
import com.expensesharing.feature.report.application.dto.ExportedFile;
import com.expensesharing.feature.report.application.port.ExcelReportExporter;
import com.expensesharing.feature.report.application.service.HouseReportAssembler;
import com.expensesharing.feature.settlement.application.usecase.GetSettlementHistoryUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class ExportHouseExcelReportUseCaseTest {

    @Mock GetHouseUseCase getHouseUseCase;
    @Mock GetHouseDashboardUseCase getHouseDashboardUseCase;
    @Mock GetExpenseUseCase getExpenseUseCase;
    @Mock GetSettlementHistoryUseCase getSettlementHistoryUseCase;
    @Mock UserProfilePort userProfilePort;
    @Mock ExcelReportExporter excelReportExporter;

    private ExportHouseExcelReportUseCase exportHouseExcelReportUseCase;

    @BeforeEach
    void setUp() {
        HouseReportAssembler houseReportAssembler = new HouseReportAssembler(
                getHouseUseCase,
                getHouseDashboardUseCase,
                getExpenseUseCase,
                getSettlementHistoryUseCase,
                userProfilePort
        );
        exportHouseExcelReportUseCase = new ExportHouseExcelReportUseCase(houseReportAssembler, excelReportExporter);
    }

    @Test
    void export_success() {
        House house = TestDataFactory.aHouse();
        HouseDashboard dashboard = new HouseDashboard(
                BigDecimal.valueOf(1_000_000),
                BigDecimal.valueOf(200_000),
                List.of(),
                List.of(),
                List.of()
        );
        ExportedFile expectedFile = new ExportedFile(
                new byte[]{1, 2, 3},
                "bao-cao-test.xlsx",
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
        );

        given(getHouseUseCase.getById(TestDataFactory.HOUSE_ID, TestDataFactory.USER_ID)).willReturn(house);
        given(getHouseDashboardUseCase.execute(TestDataFactory.HOUSE_ID, TestDataFactory.USER_ID)).willReturn(dashboard);
        given(getExpenseUseCase.getAllByHouseId(TestDataFactory.HOUSE_ID, TestDataFactory.USER_ID)).willReturn(List.of());
        given(getSettlementHistoryUseCase.execute(TestDataFactory.HOUSE_ID, TestDataFactory.USER_ID)).willReturn(List.of());
        given(userProfilePort.findUsernamesByUserIds(any())).willReturn(Map.of());
        given(excelReportExporter.export(any())).willReturn(expectedFile);

        ExportedFile result = exportHouseExcelReportUseCase.execute(TestDataFactory.HOUSE_ID, TestDataFactory.USER_ID);

        assertThat(result.filename()).isEqualTo("bao-cao-test.xlsx");
        then(excelReportExporter).should().export(any());
    }

    @Test
    void export_notMember_throwsException() {
        given(getHouseUseCase.getById(TestDataFactory.HOUSE_ID, TestDataFactory.USER_ID))
                .willThrow(new ForbiddenException("NOT_A_MEMBER", "You are not a member of this house."));

        assertThatThrownBy(() -> exportHouseExcelReportUseCase.execute(TestDataFactory.HOUSE_ID, TestDataFactory.USER_ID))
                .isInstanceOf(ForbiddenException.class);
    }
}
