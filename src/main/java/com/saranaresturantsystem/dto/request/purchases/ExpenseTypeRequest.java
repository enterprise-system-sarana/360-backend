package com.saranaresturantsystem.dto.request.purchases;

import jakarta.validation.constraints.NotBlank;

public record ExpenseTypeRequest(
        @NotBlank(message = "Code is required")
        String code,
        @NotBlank(message = "Name is required")
        String name,
        String description,
        String status
) {}