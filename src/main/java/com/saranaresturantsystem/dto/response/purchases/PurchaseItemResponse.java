package com.saranaresturantsystem.dto.response.purchases;

import java.math.BigDecimal;
import java.util.List;

public record PurchaseItemResponse(
        Long id,
        Long purchaseId,
        Long productId,
        BigDecimal quantity,
        BigDecimal cost,
        BigDecimal subtotal,
        List<String> serialNumbers
) {
}