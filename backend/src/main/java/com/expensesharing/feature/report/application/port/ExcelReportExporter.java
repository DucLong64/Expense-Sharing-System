package com.expensesharing.feature.report.application.port;

import com.expensesharing.feature.report.application.dto.ExportedFile;
import com.expensesharing.feature.report.application.dto.HouseReportData;

public interface ExcelReportExporter {

    ExportedFile export(HouseReportData data);
}
