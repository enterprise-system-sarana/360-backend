package com.saranaresturantsystem.dto.response.inventory;

import java.math.BigDecimal;

public record StockResponse(
        Long id,
        Long productId,
        String productName,
        Long modelId,
        String modelName,
        Long brandId,
        String brandName,
        Long categoryId,
        String categoryName,
        Long storeId,
        String storeName,
        BigDecimal quantity,
        BigDecimal alertQuantity,
        Integer reorderLevel,
        String status
) {
}




