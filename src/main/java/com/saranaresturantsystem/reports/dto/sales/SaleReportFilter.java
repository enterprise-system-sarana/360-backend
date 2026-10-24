package com.saranaresturantsystem.reports.dto.sales;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDate;

@JsonIgnoreProperties(ignoreUnknown = true)
public record SaleReportFilter(
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        LocalDate startDate,
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        LocalDate endDate,
        Long storeId,
        Long customerId,
        String saleStatus,
        String paymentStatus,
        Long productId,
        Long categoryId,
        Long brandId
) {
}