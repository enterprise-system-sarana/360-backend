package com.saranaresturantsystem.reports.dto.sales;

import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDate;

public record SaleReportFilter(
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        LocalDate startDate,

        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        LocalDate endDate,

        Long storeId,
        Long customerId,
        String saleStatus,
        String paymentStatus,
        Long productId
) {
}