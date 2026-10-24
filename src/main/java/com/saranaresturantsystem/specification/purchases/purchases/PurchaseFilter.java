package com.saranaresturantsystem.specification.purchases.purchases;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record PurchaseFilter(
        String referenceNo,
        String status,
        String paymentStatus,
        Long supplierId,
        Long storeId
) {
}