package com.saranaresturantsystem.reports.dto.payment;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@JsonIgnoreProperties(ignoreUnknown = true)
public record PaymentReportFilter(
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        LocalDate startDate,
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        LocalDate endDate,
        Long storeId,
        Long customerId,
        Long supplierId,
        Long saleId,
        Long purchaseId,
        Long bankId,
        String paymentStatus,
        String paymentMethod
) {
}
