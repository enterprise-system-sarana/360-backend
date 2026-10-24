package com.saranaresturantsystem.reports.dto.purchases;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@JsonIgnoreProperties(ignoreUnknown = true)
public record PurchaseReportFilter(
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        LocalDate startDate,
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        LocalDate endDate,
        Long storeId,
        Long supplierId,
        Long productId,
        Long categoryId,
        Long brandId,
        String status,
        String paymentStatus
) {
}
