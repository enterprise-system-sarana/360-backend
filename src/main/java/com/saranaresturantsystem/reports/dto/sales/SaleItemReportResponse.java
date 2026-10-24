package com.saranaresturantsystem.reports.dto.sales;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SaleItemReportResponse {
    private Long itemId;
    private Long saleId;
    private String saleNo;
    private String saleReference;
    private LocalDateTime saleDate;
    private String saleStatus;
    private String paymentStatus;
    private Long storeId;
    private String storeName;
    private Long customerId;
    private String customerName;
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
    private Double returnedQuantity;
    private Double unitPrice;
    private Double unitCost;
    private Double itemDiscount;
    private Double subtotal;
    private Double totalCost;
    private Double totalProfit;
}
