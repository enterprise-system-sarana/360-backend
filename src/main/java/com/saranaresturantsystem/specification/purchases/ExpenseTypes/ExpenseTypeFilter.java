package com.saranaresturantsystem.specification.purchases.ExpenseTypes;

public record ExpenseTypeFilter(
        String code,
        String name,
        String status
) {}