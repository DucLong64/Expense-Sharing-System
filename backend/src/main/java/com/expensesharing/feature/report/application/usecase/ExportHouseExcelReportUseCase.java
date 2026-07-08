package com.expensesharing.feature.report.application.usecase;

import com.expensesharing.feature.report.application.dto.ExportedFile;
import com.expensesharing.feature.report.application.port.ExcelReportExporter;
import com.expensesharing.feature.report.application.service.HouseReportAssembler;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ExportHouseExcelReportUseCase {

    private final HouseReportAssembler houseReportAssembler;
    private final ExcelReportExporter excelReportExporter;

    @Transactional(readOnly = true)
    public ExportedFile execute(UUID houseId, UUID requesterId) {
        var data = houseReportAssembler.assemble(houseId, requesterId);
        return excelReportExporter.export(data);
    }
}
