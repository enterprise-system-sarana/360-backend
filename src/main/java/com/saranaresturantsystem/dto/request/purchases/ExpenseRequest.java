package com.saranaresturantsystem.dto.request.purchases;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public record ExpenseRequest(
        @NotBlank(message = "Reference is required")
        String reference,
        @NotNull(message = "Amount is required")
        @DecimalMin(value = "0", message = "Amount must be greater than zero")
        BigDecimal amount,
        String note,
        @NotNull(message = "Store ID is required")
        Long storeId,
        Long bankId,
        @NotNull(message = "Expense Type ID is required")
        Long expenseTypeId,
        String status

) {}