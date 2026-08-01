package com.saranaresturantsystem.specification.catalog.product;

public record ProductFilter(
        String name ,
        String status,
        Long categoryId,
        Long brandId
) {
}
