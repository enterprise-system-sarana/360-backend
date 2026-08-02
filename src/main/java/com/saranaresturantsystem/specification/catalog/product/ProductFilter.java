package com.saranaresturantsystem.specification.catalog.product;

public record ProductFilter(
        String code ,
        String status,
        Long modelId
) {
}
