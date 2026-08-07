package com.saranaresturantsystem.dto.response.purchases;

import java.time.LocalDateTime;

public record ExpenseTypeResponse(
        Long id,
        String code,
        String name,
        String description,
        String status,
        LocalDateTime createdAt,
        String createdBy,
        LocalDateTime updatedAt,
        String updatedBy
) {}