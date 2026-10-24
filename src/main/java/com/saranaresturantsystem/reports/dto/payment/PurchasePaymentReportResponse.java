package com.saranaresturantsystem.reports.dto.payment;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PurchasePaymentReportResponse {
    private Long paymentId;
    private String paymentNo;
    private LocalDateTime paymentDate;
    private String paymentStatus;
    private Double totalAmount;
    private Double receivedAmount;
    private Double returnAmount;
    private Double dueAmount;
    private String note;
    private Long purchaseId;
    private String purchaseReferenceNo;
    private Long storeId;
    private String storeName;
    private Long supplierId;
    private String supplierName;
    private Long userId;
    private String createdByUser;
    private String paymentMethods;
    private String bankNames;
    private Long totalLinesCount;
}
