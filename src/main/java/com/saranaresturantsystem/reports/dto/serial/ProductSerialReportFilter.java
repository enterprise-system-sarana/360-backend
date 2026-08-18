package com.saranaresturantsystem.reports.dto.serial;

import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDate;

public record ProductSerialReportFilter(
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        LocalDate startDate,

        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        LocalDate endDate,

        Long storeId,
        Long productId,
        String status,
        String barcode
) {
}