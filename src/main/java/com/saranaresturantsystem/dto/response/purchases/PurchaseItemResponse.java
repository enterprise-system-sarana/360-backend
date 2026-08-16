package com.saranaresturantsystem.dto.response.purchases;

import com.saranaresturantsystem.dto.response.catalog.ProductSerialResponse;

import java.math.BigDecimal;
import java.util.List;

public record PurchaseItemResponse(
        Long id,
        Long purchaseId,
        Long productId,
        String productName,
        BigDecimal quantity,
        BigDecimal cost,
        BigDecimal subtotal,
        List <ProductSerialResponse> serialNumbers
) {
}