package com.saranaresturantsystem.specification.purchases.Expenses;

public record ExpenseFilter(
        String reference,
        String createdBy,
        Integer storeId,
        Long bankId,
        Long expenseTypeId,
        String status
) {}