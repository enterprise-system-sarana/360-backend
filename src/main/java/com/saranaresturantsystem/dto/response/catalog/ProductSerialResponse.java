package com.saranaresturantsystem.dto.response.catalog;

import java.math.BigDecimal;

public record ProductSerialResponse(
        Long id,
        Long productId ,
        String productName ,
        Long storeId ,
        String storeName ,
        String barcode,
        BigDecimal price,
        BigDecimal cost,
        BigDecimal quantity,
        String status
) {
}