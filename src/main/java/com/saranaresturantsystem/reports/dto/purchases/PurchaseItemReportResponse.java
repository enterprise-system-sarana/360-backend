package com.saranaresturantsystem.reports.dto.purchases;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PurchaseItemReportResponse {
    private Long itemId;
    private Long purchaseId;
    private String referenceNo;
    private LocalDate purchaseDate;
    private String purchaseStatus;
    private String paymentStatus;
    private Long storeId;
    private String storeName;
    private Long supplierId;
    private String supplierName;
    private Long productId;
    private String productName;
    private String productCode;
    private Long modelId;
    private String modelName;
    private Long brandId;
    private String brandName;
    private Long categoryId;
    private String categoryName;
    private Double quantity;
    private Double unitCost;
    private Double unitPrice;
    private Double subtotal;
}
