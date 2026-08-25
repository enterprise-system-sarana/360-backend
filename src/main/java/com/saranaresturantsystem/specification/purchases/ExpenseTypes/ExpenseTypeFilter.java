package com.saranaresturantsystem.specification.purchases.ExpenseTypes;

import com.saranaresturantsystem.specification.common.StatusFilter;

public record ExpenseTypeFilter(
        String name,
        String code,
        String status
) implements StatusFilter {
}