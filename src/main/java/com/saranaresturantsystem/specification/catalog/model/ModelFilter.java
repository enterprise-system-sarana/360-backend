package com.saranaresturantsystem.specification.catalog.model;

import com.saranaresturantsystem.specification.common.StatusFilter;

public record ModelFilter(
        Long categoryId,
        Long brandId,
        String name,
        String status
) implements StatusFilter {
}
