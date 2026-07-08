package com.expensesharing.feature.report.infrastructure;

import com.expensesharing.common.exception.BusinessException;
import com.expensesharing.feature.report.application.dto.ExportedFile;
import com.expensesharing.feature.report.application.dto.HouseReportData;
import com.expensesharing.feature.report.application.port.PdfReportExporter;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

@Component
public class OpenPdfHouseReportExporter implements PdfReportExporter {

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    @Override
    public ExportedFile export(HouseReportData data) {
        try (var outputStream = new ByteArrayOutputStream()) {
            BaseFont baseFont = ReportFontLoader.loadUnicodeFont();
            Font titleFont = new Font(baseFont, 16, Font.BOLD);
            Font sectionFont = new Font(baseFont, 12, Font.BOLD);
            Font normalFont = new Font(baseFont, 10, Font.NORMAL);
            Font headerFont = new Font(baseFont, 10, Font.BOLD);

            Document document = new Document(PageSize.A4, 36, 36, 48, 48);
            PdfWriter.getInstance(document, outputStream);
            document.open();

            document.add(new Paragraph("Báo cáo nhóm chi tiêu", titleFont));
            document.add(new Paragraph("Nhóm: " + data.houseName(), normalFont));
            if (data.houseDescription() != null && !data.houseDescription().isBlank()) {
                document.add(new Paragraph("Mô tả: " + data.houseDescription(), normalFont));
            }
            document.add(new Paragraph(
                    "Xuất lúc: " + DATE_TIME_FORMAT.format(data.generatedAt().atZone(ZoneId.systemDefault())),
                    normalFont
            ));
            document.add(new Paragraph(
                    "Tổng chi tiêu: " + formatAmount(data.totalSpending())
                            + " | Đã thanh toán: " + formatAmount(data.totalSettled()),
                    normalFont
            ));
            document.add(new Paragraph(" "));

            addSectionTitle(document, "Chi tiêu theo tháng", sectionFont);
            addTable(
                    document,
                    headerFont,
                    normalFont,
                    new String[]{"Tháng/Năm", "Số tiền"},
                    data.spendingByMonth().stream()
                            .map(item -> new String[]{
                                    item.month() + "/" + item.year(),
                                    formatAmount(item.amount())
                            })
                            .toList()
            );

            addSectionTitle(document, "Chi tiêu theo thành viên", sectionFont);
            addTable(
                    document,
                    headerFont,
                    normalFont,
                    new String[]{"Thành viên", "Số tiền"},
                    data.spendingByMember().stream()
                            .map(item -> new String[]{item.username(), formatAmount(item.amount())})
                            .toList()
            );

            addSectionTitle(document, "Công nợ hiện tại", sectionFont);
            addTable(
                    document,
                    headerFont,
                    normalFont,
                    new String[]{"Người nợ", "Người nhận", "Số tiền"},
                    data.currentDebts().stream()
                            .map(item -> new String[]{
                                    item.fromUsername(),
                                    item.toUsername(),
                                    formatAmount(item.amount())
                            })
                            .toList()
            );

            addSectionTitle(document, "Khoản chi", sectionFont);
            addTable(
                    document,
                    headerFont,
                    normalFont,
                    new String[]{"Tiêu đề", "Ngày", "Số tiền", "Người trả", "Cách chia"},
                    data.expenses().stream()
                            .map(item -> new String[]{
                                    item.title(),
                                    DATE_FORMAT.format(item.expenseDate()),
                                    formatAmount(item.amount()),
                                    item.paidByUsername(),
                                    item.splitType()
                            })
                            .toList()
            );

            addSectionTitle(document, "Lịch sử thanh toán", sectionFont);
            addTable(
                    document,
                    headerFont,
                    normalFont,
                    new String[]{"Thời gian", "Người trả", "Người nhận", "Số tiền"},
                    data.settlements().stream()
                            .map(item -> new String[]{
                                    DATE_TIME_FORMAT.format(item.settledAt().atZone(ZoneId.systemDefault())),
                                    item.fromUsername(),
                                    item.toUsername(),
                                    formatAmount(item.amount())
                            })
                            .toList()
            );

            document.close();
            return new ExportedFile(
                    outputStream.toByteArray(),
                    ReportFileNameBuilder.build(data.houseName(), "pdf"),
                    "application/pdf"
            );
        } catch (DocumentException exception) {
            throw new BusinessException("REPORT_EXPORT_FAILED", "Không thể xuất file PDF.");
        } catch (Exception exception) {
            throw new BusinessException("REPORT_EXPORT_FAILED", "Không thể xuất file PDF.");
        }
    }

    private void addSectionTitle(Document document, String title, Font font) throws DocumentException {
        Paragraph paragraph = new Paragraph(title, font);
        paragraph.setSpacingBefore(12f);
        paragraph.setSpacingAfter(6f);
        document.add(paragraph);
    }

    private void addTable(
            Document document,
            Font headerFont,
            Font bodyFont,
            String[] headers,
            java.util.List<String[]> rows
    ) throws DocumentException {
        PdfPTable table = new PdfPTable(headers.length);
        table.setWidthPercentage(100f);
        table.setSpacingAfter(8f);

        for (String header : headers) {
            PdfPCell cell = new PdfPCell(new Phrase(header, headerFont));
            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
            table.addCell(cell);
        }

        if (rows.isEmpty()) {
            PdfPCell emptyCell = new PdfPCell(new Phrase("Không có dữ liệu", bodyFont));
            emptyCell.setColspan(headers.length);
            table.addCell(emptyCell);
        } else {
            for (String[] row : rows) {
                for (String value : row) {
                    table.addCell(new Phrase(value == null ? "" : value, bodyFont));
                }
            }
        }

        document.add(table);
    }

    private String formatAmount(BigDecimal amount) {
        if (amount == null) {
            return "0";
        }
        return amount.stripTrailingZeros().toPlainString() + " VND";
    }
}
