package com.saranaresturantsystem.specification.sales;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record CashRegisterFilter(
        Long storeId,
        Long userId,
        String status
) {
}
