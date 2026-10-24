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
public class SalePaymentReportResponse {
    private Long paymentId;
    private String paymentNo;
    private LocalDateTime paymentDate;
    private String paymentStatus;
    private Double totalAmount;
    private Double receivedAmount;
    private Double returnAmount;
    private Double dueAmount;
    private String note;
    private Long saleId;
    private String saleNo;
    private String saleReference;
    private Long storeId;
    private String storeName;
    private Long customerId;
    private String customerName;
    private Long userId;
    private String cashierName;
    private String paymentMethods;
    private String bankNames;
    private Long totalLinesCount;
}
