package com.saranaresturantsystem.reports.dto.stock;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record StockReportFilter(
        Long storeId,
        Long productId,
        Long categoryId,
        Long brandId,
        String stockStatus,
        Boolean lowStockOnly,
        String search
) {
}
