package com.saranaresturantsystem.reports.dto.purchases;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PurchaseReportResponse {
    private Double totalAmount;
    private Double totalDiscount;
    private Double totalPaidAmount;
    private Double totalDueAmount;
    private Double totalQuantity;
    private Long totalTransactions;
    private Long storeId;
    private String storeName;
    private Long supplierId;
    private String supplierName;
    private LocalDate startDate;
    private LocalDate endDate;
    private List<PurchaseItemDetail> purchases;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PurchaseItemDetail {
        private Long purchaseId;
        private String referenceNo;
        private LocalDate purchaseDate;
        private String status;
        private String paymentStatus;
        private Double total;
        private Double discount;
        private Double grandTotal;
        private Double paidAmount;
        private Double dueAmount;
        private Long storeId;
        private String storeName;
        private Long supplierId;
        private String supplierName;
        private Long bankId;
        private String bankName;
        private Long totalItemsCount;
        private Double totalQuantity;
    }
}
