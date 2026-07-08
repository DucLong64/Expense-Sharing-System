package com.expensesharing.feature.report.presentation;

import com.expensesharing.feature.report.application.dto.ExportedFile;
import com.expensesharing.feature.report.application.usecase.ExportHouseExcelReportUseCase;
import com.expensesharing.feature.report.application.usecase.ExportHousePdfReportUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/houses/{houseId}/reports")
@RequiredArgsConstructor
public class ReportController {

    private final ExportHouseExcelReportUseCase exportHouseExcelReportUseCase;
    private final ExportHousePdfReportUseCase exportHousePdfReportUseCase;

    @GetMapping("/excel")
    public ResponseEntity<byte[]> exportExcel(
            @PathVariable UUID houseId,
            @AuthenticationPrincipal UUID userId) {
        return toFileResponse(exportHouseExcelReportUseCase.execute(houseId, userId));
    }

    @GetMapping("/pdf")
    public ResponseEntity<byte[]> exportPdf(
            @PathVariable UUID houseId,
            @AuthenticationPrincipal UUID userId) {
        return toFileResponse(exportHousePdfReportUseCase.execute(houseId, userId));
    }

    private ResponseEntity<byte[]> toFileResponse(ExportedFile file) {
        ContentDisposition disposition = ContentDisposition.attachment()
                .filename(file.filename(), StandardCharsets.UTF_8)
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, disposition.toString())
                .contentType(MediaType.parseMediaType(file.contentType()))
                .body(file.content());
    }
}
