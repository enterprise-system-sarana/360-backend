package com.saranaresturantsystem.specification.purchases.supplier;

import com.saranaresturantsystem.specification.common.StatusFilter;

public record SupplierFilter(
        String code,
        String name,
        String status
) implements StatusFilter {
}
