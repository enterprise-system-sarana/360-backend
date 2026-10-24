package com.saranaresturantsystem.reports.dto.payment;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SalePaymentSummaryResponse {
    private Double totalAmount;
    private Double totalReceivedAmount;
    private Double totalReturnAmount;
    private Double totalDueAmount;
    private Long totalTransactions;
    private Long storeId;
    private String storeName;
    private Long customerId;
    private String customerName;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private List<SalePaymentReportResponse> payments;
}
