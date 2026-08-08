package com.saranaresturantsystem.specification.sales;


public record SaleFilter(
        Long customerId,
        String status
) {
}
