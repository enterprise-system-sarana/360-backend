package com.saranaresturantsystem.dto.response.purchases;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ExpenseResponse(
        Long id,
        String reference,
        BigDecimal amount,
        String note,
        Integer storeId,
        Long bankId,
        Long expenseTypeId,
        String description,
        String status,
        LocalDateTime createdAt,
        String createdBy,
        LocalDateTime updatedAt,
        String updatedBy
) {}