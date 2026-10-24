package com.saranaresturantsystem.reports.dto.stock;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StockMovementItemResponse {
    private Long transactionId;
    private Long productId;
    private String productCode;
    private String productName;
    private String imageUrl;
    private Long modelId;
    private String modelName;
    private Long brandId;
    private String brandName;
    private Long categoryId;
    private String categoryName;
    private Long storeId;
    private String storeName;
    private String storeCode;
    private BigDecimal quantity;
    private String transactionType;
    private Long referenceId;
    private LocalDateTime transactionDate;
    private String notes;
    private LocalDateTime createdAt;
    private String createdBy;
}
