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
public class StockItemReportResponse {
    private Long stockId;
    private Long productId;
    private String productCode;
    private String productName;
    private String imageUrl;
    private String productStatus;
    private Boolean hasSerial;
    private Long modelId;
    private String modelName;
    private Long brandId;
    private String brandName;
    private Long categoryId;
    private String categoryName;
    private Long storeId;
    private String storeName;
    private String storeCode;
    private Long variantValueId;
    private String variantValueName;
    private BigDecimal currentQuantity;
    private BigDecimal alertQuantity;
    private Integer reorderLevel;
    private BigDecimal costPrice;
    private BigDecimal salePrice;
    private BigDecimal totalCostValue;
    private BigDecimal totalSaleValue;
    private BigDecimal potentialProfit;
    private BigDecimal marginPercentage;
    private String stockStatus;
    private Long availableSerialsCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
