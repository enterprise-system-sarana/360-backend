package com.saranaresturantsystem.specification.catalog.product;

import com.saranaresturantsystem.specification.common.StatusFilter;

public record ProductFilter(
        String status,
        Long modelId
) implements StatusFilter {
}
