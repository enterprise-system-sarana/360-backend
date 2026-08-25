package com.saranaresturantsystem.specification.purchases.Expenses;

import com.saranaresturantsystem.specification.common.StatusFilter;

public record ExpenseFilter(
        String reference,
        String createdBy,
        Long storeId,
        Long bankId,
        Long expenseTypeId,
        String status
) implements StatusFilter {
}