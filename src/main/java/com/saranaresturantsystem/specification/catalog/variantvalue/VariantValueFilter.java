package com.saranaresturantsystem.specification.catalog.variantvalue;

public record VariantValueFilter(
        String code,
        String name,
        Long variantTypeId
) {
}