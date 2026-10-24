package com.saranaresturantsystem.specification.sales;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record SaleFilter(
        Long customerId,
        String status,
        Long storeId,
        String paymentStatus
) {
}
