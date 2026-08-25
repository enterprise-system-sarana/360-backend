package com.saranaresturantsystem.specification.catalog.varianttype;

import com.saranaresturantsystem.specification.common.StatusFilter;

public record VariantTypeFilter(
        String code,
        String name,
        String status
) implements StatusFilter {
}
