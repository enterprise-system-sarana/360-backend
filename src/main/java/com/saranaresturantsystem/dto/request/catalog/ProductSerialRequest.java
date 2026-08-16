package com.saranaresturantsystem.dto.request.catalog;

import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record ProductSerialRequest(
        String barcode,
        @NotNull(message = "price is required")
        BigDecimal price,
        BigDecimal cost,
        BigDecimal quantity,
        BigDecimal alertQuantity,
        Long storeId,
        Long purchaseItemId,
        @NotNull(message = "productId is required")
        Long productId,
        @NotNull(message = "status is required")
        String status
) {
}
