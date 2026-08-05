package com.saranaresturantsystem.dto.response.purchases;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record PurchaseResponse(
        Long id,
        String referenceNo,
        Long supplierId,
        Long storeId,
        LocalDate purchaseDate,
        BigDecimal total,
        BigDecimal discount,
        BigDecimal grandTotal,
        String status,
        String note,
        List<PurchaseItemResponse> items
) {
}