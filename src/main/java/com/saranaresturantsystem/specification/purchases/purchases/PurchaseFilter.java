package com.saranaresturantsystem.specification.purchases.purchases;


public record PurchaseFilter(
        String referenceNo,
        String status,
        String paymentStatus,
        Long supplierId,
        Long storeId
) {
}