package com.expensesharing.feature.report.application.dto;

public record ExportedFile(byte[] content, String filename, String contentType) {}
