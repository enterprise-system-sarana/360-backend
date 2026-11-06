package com.saranaresturantsystem.specification.inventory.stock;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record StockFilter(
        Long productId,
        Long modelId,
        Long brandId,
        Long categoryId,
        Long storeId,
        String status,
        String search
) {
}

