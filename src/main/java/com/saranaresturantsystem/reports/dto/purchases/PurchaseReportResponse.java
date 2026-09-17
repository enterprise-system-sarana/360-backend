package com.saranaresturantsystem.reports.dto.purchases;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PurchaseReportResponse {
    private Long id;
    private String referenceNo;
    private LocalDate purchaseDate;
    private BigDecimal total;
    private BigDecimal discount;
    private BigDecimal grandTotal;
    private String status;
    private BigDecimal paidAmount;
    private BigDecimal dueAmount;
    private String paymentStatus;
    private String note;
    private Long bankId;
    private String bankName;
    private Long supplierId;
    private String supplierName;
    private Long storeId;
    private String storeName;
    private LocalDateTime createdAt;
}