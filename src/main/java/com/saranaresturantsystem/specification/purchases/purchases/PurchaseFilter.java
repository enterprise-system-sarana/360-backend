package com.saranaresturantsystem.specification.purchases.purchases;

import java.time.LocalDate;

public record PurchaseFilter(
        String referenceNo,
        String status,
        Long supplierId,
        Long storeId,
        LocalDate startDate,
        LocalDate endDate
) {
}