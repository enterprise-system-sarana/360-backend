package com.saranaresturantsystem.specification.catalog.variantvalue;

import com.saranaresturantsystem.specification.common.StatusFilter;

public record VariantValueFilter(
        String code,
        String name,
        Long variantTypeId,
        String status
) implements StatusFilter {
}