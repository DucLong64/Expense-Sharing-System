package com.expensesharing.feature.report.infrastructure;

import com.expensesharing.common.exception.BusinessException;
import com.expensesharing.feature.report.application.dto.ExportedFile;
import com.expensesharing.feature.report.application.dto.HouseReportData;
import com.expensesharing.feature.report.application.port.ExcelReportExporter;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Component
@RequiredArgsConstructor
public class ApachePoiExcelReportExporter implements ExcelReportExporter {

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    @Override
    public ExportedFile export(HouseReportData data) {
        try (var workbook = new XSSFWorkbook(); var outputStream = new ByteArrayOutputStream()) {
            CellStyle headerStyle = createHeaderStyle(workbook);

            writeSummarySheet(workbook, data, headerStyle);
            writeExpensesSheet(workbook, data, headerStyle);
            writeDebtsSheet(workbook, data, headerStyle);
            writeSettlementsSheet(workbook, data, headerStyle);

            workbook.write(outputStream);
            return new ExportedFile(
                    outputStream.toByteArray(),
                    ReportFileNameBuilder.build(data.houseName(), "xlsx"),
                    "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
            );
        } catch (IOException exception) {
            throw new BusinessException("REPORT_EXPORT_FAILED", "Không thể xuất file Excel.");
        }
    }

    private void writeSummarySheet(XSSFWorkbook workbook, HouseReportData data, CellStyle headerStyle) {
        Sheet sheet = workbook.createSheet("Tổng quan");
        int rowIndex = 0;
        rowIndex = writeLabelValue(sheet, rowIndex, "Nhóm", data.houseName());
        rowIndex = writeLabelValue(sheet, rowIndex, "Mô tả", nullSafe(data.houseDescription()));
        rowIndex = writeLabelValue(
                sheet,
                rowIndex,
                "Thời gian xuất",
                DATE_TIME_FORMAT.format(data.generatedAt().atZone(ZoneId.systemDefault()))
        );
        rowIndex = writeLabelValue(sheet, rowIndex, "Tổng chi tiêu", formatAmount(data.totalSpending()));
        rowIndex = writeLabelValue(sheet, rowIndex, "Đã thanh toán", formatAmount(data.totalSettled()));
        rowIndex += 1;

        Row header = sheet.createRow(rowIndex++);
        writeHeader(header, headerStyle, "Tháng", "Năm", "Số tiền");
        for (HouseReportData.MonthlySpendingRow item : data.spendingByMonth()) {
            Row row = sheet.createRow(rowIndex++);
            row.createCell(0).setCellValue(item.month());
            row.createCell(1).setCellValue(item.year());
            row.createCell(2).setCellValue(formatAmount(item.amount()));
        }

        rowIndex += 1;
        Row memberHeader = sheet.createRow(rowIndex++);
        writeHeader(memberHeader, headerStyle, "Thành viên", "Chi tiêu");
        for (HouseReportData.MemberSpendingRow item : data.spendingByMember()) {
            Row row = sheet.createRow(rowIndex++);
            row.createCell(0).setCellValue(item.username());
            row.createCell(1).setCellValue(formatAmount(item.amount()));
        }

        autosize(sheet, 3);
    }

    private void writeExpensesSheet(XSSFWorkbook workbook, HouseReportData data, CellStyle headerStyle) {
        Sheet sheet = workbook.createSheet("Khoản chi");
        Row header = sheet.createRow(0);
        writeHeader(header, headerStyle, "Tiêu đề", "Ngày", "Số tiền", "Người trả", "Cách chia", "Thành viên", "Ghi chú");

        int rowIndex = 1;
        for (HouseReportData.ExpenseRow expense : data.expenses()) {
            Row row = sheet.createRow(rowIndex++);
            row.createCell(0).setCellValue(expense.title());
            row.createCell(1).setCellValue(DATE_FORMAT.format(expense.expenseDate()));
            row.createCell(2).setCellValue(formatAmount(expense.amount()));
            row.createCell(3).setCellValue(expense.paidByUsername());
            row.createCell(4).setCellValue(expense.splitType());
            row.createCell(5).setCellValue(expense.participantsSummary());
            row.createCell(6).setCellValue(nullSafe(expense.note()));
        }

        autosize(sheet, 7);
    }

    private void writeDebtsSheet(XSSFWorkbook workbook, HouseReportData data, CellStyle headerStyle) {
        Sheet sheet = workbook.createSheet("Công nợ");
        Row header = sheet.createRow(0);
        writeHeader(header, headerStyle, "Người nợ", "Người nhận", "Số tiền");

        int rowIndex = 1;
        for (HouseReportData.DebtRow debt : data.currentDebts()) {
            Row row = sheet.createRow(rowIndex++);
            row.createCell(0).setCellValue(debt.fromUsername());
            row.createCell(1).setCellValue(debt.toUsername());
            row.createCell(2).setCellValue(formatAmount(debt.amount()));
        }

        autosize(sheet, 3);
    }

    private void writeSettlementsSheet(XSSFWorkbook workbook, HouseReportData data, CellStyle headerStyle) {
        Sheet sheet = workbook.createSheet("Thanh toán");
        Row header = sheet.createRow(0);
        writeHeader(header, headerStyle, "Thời gian", "Người trả", "Người nhận", "Số tiền", "Ghi chú");

        int rowIndex = 1;
        for (HouseReportData.SettlementRow settlement : data.settlements()) {
            Row row = sheet.createRow(rowIndex++);
            row.createCell(0).setCellValue(
                    DATE_TIME_FORMAT.format(settlement.settledAt().atZone(ZoneId.systemDefault()))
            );
            row.createCell(1).setCellValue(settlement.fromUsername());
            row.createCell(2).setCellValue(settlement.toUsername());
            row.createCell(3).setCellValue(formatAmount(settlement.amount()));
            row.createCell(4).setCellValue(nullSafe(settlement.note()));
        }

        autosize(sheet, 5);
    }

    private CellStyle createHeaderStyle(XSSFWorkbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        style.setFont(font);
        return style;
    }

    private int writeLabelValue(Sheet sheet, int rowIndex, String label, String value) {
        Row row = sheet.createRow(rowIndex);
        row.createCell(0).setCellValue(label);
        row.createCell(1).setCellValue(value);
        return rowIndex + 1;
    }

    private void writeHeader(Row row, CellStyle headerStyle, String... headers) {
        for (int index = 0; index < headers.length; index++) {
            Cell cell = row.createCell(index);
            cell.setCellValue(headers[index]);
            cell.setCellStyle(headerStyle);
        }
    }

    private void autosize(Sheet sheet, int columnCount) {
        for (int index = 0; index < columnCount; index++) {
            sheet.autoSizeColumn(index);
        }
    }

    private String formatAmount(BigDecimal amount) {
        if (amount == null) {
            return "0";
        }
        return amount.stripTrailingZeros().toPlainString();
    }

    private String nullSafe(String value) {
        return value == null ? "" : value;
    }
}
