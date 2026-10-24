package com.saranaresturantsystem.reports.dto.stock;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StockReportResponse {
    private BigDecimal totalQuantity;
    private BigDecimal totalCostValue;
    private BigDecimal totalSaleValue;
    private BigDecimal totalPotentialProfit;
    private Long totalItems;
    private Long inStockCount;
    private Long lowStockCount;
    private Long outOfStockCount;
    private Long storeId;
    private String storeName;
    private Long categoryId;
    private String categoryName;
    private Long brandId;
    private String brandName;
    private List<StockItemReportResponse> stocks;
}
