package com.saranaresturantsystem.specification.catalog.model;

public record ModelFilter(
        String name ,
        String status,
        Long categoryId,
        Long brandId
) {
}
