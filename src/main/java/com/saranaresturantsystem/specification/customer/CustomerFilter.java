package com.saranaresturantsystem.specification.customer;

import com.saranaresturantsystem.specification.common.StatusFilter;

public record CustomerFilter(
        String code,
        String name,
        String status
) implements StatusFilter {
}
