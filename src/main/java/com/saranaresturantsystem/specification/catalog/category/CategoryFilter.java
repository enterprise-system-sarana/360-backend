package com.saranaresturantsystem.specification.catalog.category;

import com.saranaresturantsystem.specification.common.StatusFilter;

public record CategoryFilter(
        String name,
        String code,
        String status
) implements StatusFilter {
}
