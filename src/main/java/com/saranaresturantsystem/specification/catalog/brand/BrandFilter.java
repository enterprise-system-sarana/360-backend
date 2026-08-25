package com.saranaresturantsystem.specification.catalog.brand;

import com.saranaresturantsystem.specification.common.StatusFilter;

public record BrandFilter(
        String name,
        String status
) implements StatusFilter {
}
