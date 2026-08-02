package com.saranaresturantsystem.specification.catalog.productVariant;

public record ProductVariantFilter(
        String code ,
        String sellingPrice,
        String status
) {
}
