package com.expensesharing.feature.report.infrastructure;

import com.expensesharing.common.exception.BusinessException;
import com.lowagie.text.DocumentException;
import com.lowagie.text.pdf.BaseFont;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

final class ReportFontLoader {

    private ReportFontLoader() {}

    static BaseFont loadUnicodeFont() {
        try (InputStream inputStream = new ClassPathResource("fonts/NotoSans-Regular.ttf").getInputStream()) {
            byte[] fontBytes = inputStream.readAllBytes();
            return BaseFont.createFont(
                    "NotoSans-Regular.ttf",
                    BaseFont.IDENTITY_H,
                    BaseFont.EMBEDDED,
                    true,
                    fontBytes,
                    null
            );
        } catch (IOException | DocumentException classpathError) {
            String windowsDir = System.getenv("WINDIR");
            if (windowsDir != null) {
                Path arialPath = Path.of(windowsDir, "Fonts", "arial.ttf");
                if (Files.exists(arialPath)) {
                    try {
                        return BaseFont.createFont(arialPath.toString(), BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
                    } catch (DocumentException | IOException ignored) {
                        // Fall through to shared error below.
                    }
                }
            }
            throw new BusinessException(
                    "REPORT_FONT_NOT_AVAILABLE",
                    "Không thể tải font báo cáo. Thêm fonts/NotoSans-Regular.ttf vào classpath."
            );
        }
    }
}
