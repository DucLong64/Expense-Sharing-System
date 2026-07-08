package com.expensesharing.feature.report.infrastructure;

import java.text.Normalizer;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

final class ReportFileNameBuilder {

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyyMMdd");

    private ReportFileNameBuilder() {}

    static String build(String houseName, String extension) {
        String slug = Normalizer.normalize(houseName == null ? "nhom" : houseName, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .toLowerCase()
                .replaceAll("[^a-z0-9]+", "-")
                .replaceAll("^-|-$", "");
        if (slug.isBlank()) {
            slug = "nhom";
        }
        return "bao-cao-" + slug + "-" + LocalDate.now().format(DATE_FORMAT) + "." + extension;
    }
}
