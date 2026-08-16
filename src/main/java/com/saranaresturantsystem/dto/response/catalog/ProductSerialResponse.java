package com.saranaresturantsystem.dto.response.catalog;

import java.math.BigDecimal;

public record ProductSerialResponse(
        Long id,
        String barcode,
        BigDecimal price,
        BigDecimal cost,
        BigDecimal quantity
        // BigDecimal alertQuantity,
        // Long storeId,
        // Long purchaseId,
        // Long purchaseItemId,
        // Long productId,
        // String productName,
        // String status
) {
}