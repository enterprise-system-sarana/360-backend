package com.saranaresturantsystem.reports.dto.payments;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PaymentReportResponse {
    private Long id;
    private LocalDate date;
    private String paymentReference;
    private String saleNo;
    private String customerName;
    private String paidBy;
    private BigDecimal amount;
    private String createdBy;
}